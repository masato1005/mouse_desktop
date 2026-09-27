package common.windows;

/** 自プロセスのSendInputをキーボードフックで識別するための値。 */
public final class KeyboardInjectionMarker {
    public static final long VALUE = 0x534F4D4BL;

    private KeyboardInjectionMarker() {
    }
}
