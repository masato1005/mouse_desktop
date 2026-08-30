package common.Keyboard.contents;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public final class CheckInputtable implements AutoCloseable {

    /*
     * CUIAutomation
     */
    private static final Guid.CLSID CLSID_CUI_AUTOMATION = new Guid.CLSID(
            "{FF48DBA4-60EF-4201-AA87-54103EEF594E}");

    /*
     * IUIAutomation
     */
    private static final Guid.IID IID_IUI_AUTOMATION = new Guid.IID(
            "{30CBE57D-D9D0-452A-AB13-7AC5AC4825EE}");

    /*
     * UI AutomationプロパティID
     */
    private static final int UIA_CONTROL_TYPE_PROPERTY_ID = 30003;
    private static final int UIA_HAS_KEYBOARD_FOCUS_PROPERTY_ID = 30008;
    private static final int UIA_IS_KEYBOARD_FOCUSABLE_PROPERTY_ID = 30009;
    private static final int UIA_IS_ENABLED_PROPERTY_ID = 30010;
    private static final int UIA_IS_OFFSCREEN_PROPERTY_ID = 30022;

    private static final int UIA_IS_TEXT_PATTERN_AVAILABLE_PROPERTY_ID = 30040;

    private static final int UIA_IS_VALUE_PATTERN_AVAILABLE_PROPERTY_ID = 30043;

    private static final int UIA_VALUE_IS_READ_ONLY_PROPERTY_ID = 30046;

    /*
     * ControlType ID
     */
    private static final int UIA_EDIT_CONTROL_TYPE_ID = 50004;
    private static final int UIA_DOCUMENT_CONTROL_TYPE_ID = 50030;

    /*
     * COM初期化フラグ
     */
    private static final int COINIT_MULTITHREADED = 0x0;

    private final Consumer<InputState> listener;

    private final AtomicReference<InputState> currentState = new AtomicReference<>(InputState.UNKNOWN);

    private final AtomicBoolean started = new AtomicBoolean(false);

    private final CountDownLatch initializationFinished = new CountDownLatch(1);

    private final CountDownLatch stopRequested = new CountDownLatch(1);

    private final AtomicReference<Throwable> initializationError = new AtomicReference<>();

    private Thread workerThread;

    public CheckInputtable(Consumer<InputState> listener) {
        this.listener = listener != null ? listener : state -> {};
    }

    public enum InputState {
        /*
         * ValuePatternによって編集可能と確認できた
         */
        EDITABLE,

        /*
         * Edit/Document＋TextPatternのため、
         * Robot入力を受け付ける可能性が高い
         */
        LIKELY_EDITABLE,

        NOT_EDITABLE,

        /*
         * UIAエラーや要素消失などで判定不能
         */
        UNKNOWN;

        public boolean allowsRobotInput() {
            return this == EDITABLE ||
                    this == LIKELY_EDITABLE;
        }

        public boolean definitelyEditable() {
            return this == EDITABLE;
        }
    }

    public void start() {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException(
                    "このモニターは既に開始されています");
        }

        workerThread = new Thread(
                this::runMonitor,
                "windows-uia-focus-monitor");

        workerThread.setDaemon(true);
        workerThread.start();

        try {
            boolean completed = initializationFinished.await(
                    5,
                    TimeUnit.SECONDS);

            if (!completed) {
                throw new IllegalStateException(
                        "UI Automationの初期化がタイムアウトしました");
            }

            Throwable error = initializationError.get();

            if (error != null) {
                throw new IllegalStateException(
                        "UI Automationを初期化できませんでした",
                        error);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "初期化待機が中断されました",
                    e);
        }
    }

    private void runMonitor() {
        UiaAutomation automation = null;
        FocusChangedHandler handler = null;

        boolean comInitialized = false;
        boolean eventRegistered = false;

        try {
            /*
             * この専用スレッドをCOM MTAとして初期化
             */
            HRESULT initializeResult = Ole32.INSTANCE.CoInitializeEx(
                    null,
                    COINIT_MULTITHREADED);

            COMUtils.checkRC(initializeResult);
            comInitialized = true;

            /*
             * CUIAutomationのインスタンスを生成
             */
            PointerByReference result = new PointerByReference();

            HRESULT createResult = Ole32.INSTANCE.CoCreateInstance(
                    CLSID_CUI_AUTOMATION,
                    null,
                    WTypes.CLSCTX_INPROC_SERVER,
                    IID_IUI_AUTOMATION,
                    result);

            COMUtils.checkRC(createResult);

            automation = new UiaAutomation(result.getValue());

            /*
             * JavaでCOMコールバックを作成
             */
            handler = new FocusChangedHandler(
                    this::handleFocusChanged);

            /*
             * システム全体のフォーカス変更イベントを登録
             */
            COMUtils.checkRC(
                    automation.addFocusChangedEventHandler(
                            handler.getPointer()));

            eventRegistered = true;

            /*
             * 起動時点のフォーカスも判定
             */
            evaluateInitiallyFocusedElement(automation);

            initializationFinished.countDown();

            /*
             * close()されるまで専用スレッドを維持
             */
            stopRequested.await();

        } catch (Throwable e) {
            initializationError.compareAndSet(null, e);
            initializationFinished.countDown();

            publish(InputState.UNKNOWN);

        } finally {
            /*
             * 登録したスレッドと同じスレッドで解除する
             */
            if (eventRegistered &&
                    automation != null &&
                    handler != null) {

                try {
                    COMUtils.checkRC(
                            automation
                                    .removeFocusChangedEventHandler(
                                            handler.getPointer()));
                } catch (Throwable ignored) {
                    // 終了処理なので継続
                }
            }

            if (automation != null) {
                automation.Release();
            }

            /*
             * handlerはここまで強参照を維持する。
             * 登録中にGCされるとネイティブコールバックが
             * 無効になるため重要。
             */
            handler = null;

            if (comInitialized) {
                Ole32.INSTANCE.CoUninitialize();
            }

            initializationFinished.countDown();
        }
    }

    private void evaluateInitiallyFocusedElement(
            UiaAutomation automation) {

        PointerByReference result = new PointerByReference();

        HRESULT hr = automation.getFocusedElement(result);

        if (COMUtils.FAILED(hr)) {
            publish(InputState.UNKNOWN);
            return;
        }

        Pointer pointer = result.getValue();

        if (pointer == null) {
            publish(InputState.UNKNOWN);
            return;
        }

        /*
         * GetFocusedElementで取得したポインタは
         * 呼び出し側がReleaseする。
         */
        UiaElement element = new UiaElement(pointer);

        try {
            publish(classify(element));
        } finally {
            element.Release();
        }
    }

    private InputState classify(UiaElement element) {
        try {
            boolean enabled = element.booleanProperty(
                    UIA_IS_ENABLED_PROPERTY_ID);

            boolean hasFocus = element.booleanProperty(
                    UIA_HAS_KEYBOARD_FOCUS_PROPERTY_ID);

            boolean keyboardFocusable = element.booleanProperty(
                    UIA_IS_KEYBOARD_FOCUSABLE_PROPERTY_ID);

            boolean offscreen = element.booleanProperty(
                    UIA_IS_OFFSCREEN_PROPERTY_ID);

            if (!enabled ||
                    !hasFocus ||
                    !keyboardFocusable ||
                    offscreen) {

                return InputState.NOT_EDITABLE;
            }

            /*
             * ValuePatternが利用可能なら、
             * IsReadOnlyが最も強い判定材料になる
             */
            boolean valuePatternAvailable = element.booleanProperty(
                    UIA_IS_VALUE_PATTERN_AVAILABLE_PROPERTY_ID);

            if (valuePatternAvailable) {
                boolean readOnly = element.booleanProperty(
                        UIA_VALUE_IS_READ_ONLY_PROPERTY_ID);

                return readOnly
                        ? InputState.NOT_EDITABLE
                        : InputState.EDITABLE;
            }

            /*
             * TextPatternは読み取り用なので、
             * これだけで編集可能とは断定しない。
             */
            int controlType = element.intProperty(
                    UIA_CONTROL_TYPE_PROPERTY_ID);

            boolean textPatternAvailable = element.booleanProperty(
                    UIA_IS_TEXT_PATTERN_AVAILABLE_PROPERTY_ID);

            boolean textControl = controlType == UIA_EDIT_CONTROL_TYPE_ID ||
                    controlType == UIA_DOCUMENT_CONTROL_TYPE_ID;

            if (textControl && textPatternAvailable) {
                return InputState.LIKELY_EDITABLE;
            }

            return InputState.NOT_EDITABLE;

        } catch (Throwable e) {
            /*
             * フォーカス移動直後に要素が消えることがある
             */
            return InputState.UNKNOWN;
        }
    }

    private void publish(InputState newState) {
        InputState oldState = currentState.getAndSet(newState);

        if (oldState != newState) {
            try {
                /*
                 * listenerでは重い処理を実行しないこと。
                 * Robot入力も別処理へ渡す方が安全。
                 */
                listener.accept(newState);
            } catch (Throwable ignored) {
                // ネイティブコールバックへ例外を出さない
            }
        }
    }

    /*
     * UI Automationのコールバック内で実行される。
     *
     * senderはコールバック中だけ使用し、
     * 別スレッドへPointerを渡さない。
     */
    private void handleFocusChanged(Pointer sender) {
        if (sender == null) {
            publish(InputState.UNKNOWN);
            return;
        }

        try {
            UiaElement element = new UiaElement(sender);

            /*
             * senderはUI Automation側が所有しているため、
             * ここではReleaseしない。
             */
            publish(classify(element));

        } catch (Throwable e) {
            /*
             * JNAのネイティブコールバック外へ
             * 例外を出さない
             */
            publish(InputState.UNKNOWN);
        }
    }

    public InputState getCurrentState() {
        return currentState.get();
    }

    public boolean canUseRobot() {
        return currentState
                .get()
                .allowsRobotInput();
    }

    @Override
    public void close() {
        stopRequested.countDown();

        Thread thread = workerThread;

        if (thread == null ||
                thread == Thread.currentThread()) {
            return;
        }

        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /*
     * IUIAutomationの必要なメソッドだけをラップする。
     *
     * VTableインデックス:
     * 8 = GetFocusedElement
     * 39 = AddFocusChangedEventHandler
     * 40 = RemoveFocusChangedEventHandler
     */
    private static final class UiaAutomation
            extends Unknown {

        private UiaAutomation(Pointer pointer) {
            super(pointer);
        }

        private HRESULT addFocusChangedEventHandler(
                Pointer handler) {

            return (HRESULT) _invokeNativeObject(
                    39,
                    new Object[] {
                            getPointer(),
                            null, // CacheRequest
                            handler
                    },
                    HRESULT.class);
        }

        private HRESULT getFocusedElement(
                PointerByReference result) {

            return (HRESULT) _invokeNativeObject(
                    8,
                    new Object[] {
                            getPointer(),
                            result
                    },
                    HRESULT.class);
        }

        private HRESULT removeFocusChangedEventHandler(
                Pointer handler) {

            return (HRESULT) _invokeNativeObject(
                    40,
                    new Object[] {
                            getPointer(),
                            handler
                    },
                    HRESULT.class);
        }
    }

    /*
     * IUIAutomationElementの必要なメソッドだけをラップする。
     *
     * VTableインデックス:
     * 10 = GetCurrentPropertyValue
     */
    private static final class UiaElement
            extends Unknown {

        private UiaElement(Pointer pointer) {
            super(pointer);
        }

        private boolean booleanProperty(int propertyId) {
            Variant.VARIANT value = getCurrentPropertyValue(propertyId);

            try {
                return value.booleanValue();
            } finally {
                OleAuto.INSTANCE.VariantClear(value);
            }
        }

        private Variant.VARIANT getCurrentPropertyValue(
                int propertyId) {

            Variant.VARIANT value = new Variant.VARIANT();

            HRESULT hr = (HRESULT) _invokeNativeObject(
                    10,
                    new Object[] {
                            getPointer(),
                            propertyId,
                            value
                    },
                    HRESULT.class);

            COMUtils.checkRC(hr);

            /*
             * ネイティブ側から書かれたVARIANTを
             * Javaフィールドへ反映
             */
            value.read();

            return value;
        }

        private int intProperty(int propertyId) {
            Variant.VARIANT value = getCurrentPropertyValue(propertyId);

            try {
                return value.intValue();
            } finally {
                OleAuto.INSTANCE.VariantClear(value);
            }
        }
    }

    /*
     * IUIAutomationFocusChangedEventHandlerのVTable
     *
     * IUnknown:
     * 0 QueryInterface
     * 1 AddRef
     * 2 Release
     *
     * FocusChangedEventHandler:
     * 3 HandleFocusChangedEvent
     */
    @Structure.FieldOrder({
            "queryInterface",
            "addRef",
            "release",
            "handleFocusChanged"
    })
    public static class FocusChangedVTable
            extends Structure {

        public QueryInterfaceCallback queryInterface;
        public AddRefCallback addRef;
        public ReleaseCallback release;
        public HandleFocusChangedCallback handleFocusChanged;

        public static class ByReference
                extends FocusChangedVTable
                implements Structure.ByReference {
        }

        public interface QueryInterfaceCallback
                extends StdCallLibrary.StdCallCallback {

            HRESULT invoke(
                    Pointer thisPointer,
                    Guid.REFIID requestedInterface,
                    PointerByReference result);
        }

        public interface AddRefCallback
                extends StdCallLibrary.StdCallCallback {

            int invoke(Pointer thisPointer);
        }

        public interface ReleaseCallback
                extends StdCallLibrary.StdCallCallback {

            int invoke(Pointer thisPointer);
        }

        public interface HandleFocusChangedCallback
                extends StdCallLibrary.StdCallCallback {

            HRESULT invoke(
                    Pointer thisPointer,
                    Pointer sender);
        }
    }

    /*
     * Java上にCOMコールバックオブジェクトを構築する。
     */
    @Structure.FieldOrder("vtable")
    public static class FocusChangedHandler
            extends Structure {

        private static final Guid.IID IID_FOCUS_HANDLER = new Guid.IID(
                "{C270F6B5-5C69-4290-9745-7A7F97169468}");

        public FocusChangedVTable.ByReference vtable;

        private final AtomicInteger referenceCount = new AtomicInteger(1);

        private final Consumer<Pointer> callback;

        public FocusChangedHandler(
                Consumer<Pointer> callback) {

            this.callback = callback;

            vtable = new FocusChangedVTable.ByReference();

            vtable.queryInterface = this::queryInterface;

            vtable.addRef = this::addRef;

            vtable.release = this::release;

            vtable.handleFocusChanged = this::handleFocusChanged;

            /*
             * VTableを先にネイティブメモリへ書き込み、
             * そのポインタをCOMオブジェクトへ設定
             */
            vtable.write();
            write();
        }

        private HRESULT queryInterface(
                Pointer thisPointer,
                Guid.REFIID requestedInterface,
                PointerByReference result) {

            if (requestedInterface == null) {
                result.setValue(null);

                return new HRESULT(
                        WinError.E_NOINTERFACE);
            }

            Guid.IID requested = requestedInterface.getValue();

            boolean supported = sameGuid(
                    requested,
                    IUnknown.IID_IUNKNOWN) ||
                    sameGuid(
                            requested,
                            IID_FOCUS_HANDLER);

            if (!supported) {
                result.setValue(null);

                return new HRESULT(
                        WinError.E_NOINTERFACE);
            }

            result.setValue(thisPointer);
            referenceCount.incrementAndGet();

            return WinError.S_OK;
        }

        private static boolean sameGuid(
                Guid.GUID first,
                Guid.GUID second) {

            return first != null &&
                    second != null &&
                    Arrays.equals(
                            first.toByteArray(),
                            second.toByteArray());
        }

        private int addRef(Pointer thisPointer) {
            return referenceCount.incrementAndGet();
        }

        private int release(Pointer thisPointer) {
            return referenceCount.updateAndGet(
                    value -> Math.max(0, value - 1));
        }

        private HRESULT handleFocusChanged(
                Pointer thisPointer,
                Pointer sender) {

            try {
                callback.accept(sender);
            } catch (Throwable ignored) {
                // COM境界を越えて例外を投げない
            }

            return WinError.S_OK;
        }
    }
}