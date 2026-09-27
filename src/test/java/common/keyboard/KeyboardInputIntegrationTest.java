package common.keyboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.platform.win32.User32;

import common.data.KeyboardData;
import common.eventType.DataType;
import common.eventType.KeyboardEventType;
import common.json.InputConvertedData;
import common.json.JsonConverter;
import common.windows.WindowsExecutor;
import client.keyboard.WindowsKeyboardHooker;

@EnabledOnOs(OS.WINDOWS)
class KeyboardInputIntegrationTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void sendInputChangesWindowsKeyState() throws Exception {
        WindowsExecutor executor = new WindowsExecutor(null);
        KeyboardData aKey = new KeyboardData(
                KeyboardEventType.KEY_DOWN,
                "A",
                0x41,
                0x1E,
                false,
                false);

        try {
            executor.pressedKey(aKey);
            Thread.sleep(50);
            assertTrue((User32.INSTANCE.GetAsyncKeyState(0x41) & 0x8000) != 0,
                    "SendInput did not place the A key in the pressed state");
        } finally {
            executor.ReleasedKey(aKey);
        }
    }

    @Test
    void keyboardPayloadPreservesInjectionFields() throws Exception {
        KeyboardData original = new KeyboardData(
                KeyboardEventType.KEY_DOWN,
                "A",
                0x41,
                0x1E,
                false,
                false);

        String json = JsonConverter.toJson(DataType.KEYBOARD, original);
        InputConvertedData envelope = MAPPER.readValue(json, InputConvertedData.class);
        KeyboardData restored = MAPPER.treeToValue(envelope.getData(), KeyboardData.class);

        assertEquals(DataType.KEYBOARD, envelope.getDataType());
        assertEquals(KeyboardEventType.KEY_DOWN, restored.getEventType());
        assertEquals(0x41, restored.getVirtualKeyCode());
        assertEquals(0x1E, restored.getScanCode());
        assertEquals(false, restored.isExtendedKey());
    }

    @Test
    void windowsHookForwardsInjectedInputFromExternalSources() throws Exception {
        CountDownLatch eventsReceived = new CountDownLatch(2);
        List<WindowsKeyboardHooker.HookEvent> events = new CopyOnWriteArrayList<>();
        WindowsKeyboardHooker hooker = new WindowsKeyboardHooker(event -> {
            if (event.virtualKeyCode() == KeyEvent.VK_B) {
                events.add(event);
                eventsReceived.countDown();
            }
        });
        try {
            hooker.start();
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_B);
            robot.keyRelease(KeyEvent.VK_B);

            assertTrue(eventsReceived.await(2, TimeUnit.SECONDS),
                    "The keyboard hook discarded input injected by an external source");
            assertEquals(WindowsKeyboardHooker.KeyAction.DOWN, events.get(0).action());
            assertEquals(WindowsKeyboardHooker.KeyAction.UP, events.get(1).action());
        } finally {
            hooker.close();
        }
    }

    @Test
    void windowsHookIgnoresInputInjectedByThisApplication() throws Exception {
        AtomicInteger receivedEvents = new AtomicInteger();
        WindowsKeyboardHooker hooker = new WindowsKeyboardHooker(event -> {
            if (event.virtualKeyCode() == KeyEvent.VK_C) {
                receivedEvents.incrementAndGet();
            }
        });
        KeyboardData cKey = new KeyboardData(
                KeyboardEventType.KEY_DOWN,
                "C",
                KeyEvent.VK_C,
                0x2E,
                false,
                false);
        WindowsExecutor executor = new WindowsExecutor(null);

        try {
            hooker.start();
            executor.pressedKey(cKey);
            executor.ReleasedKey(cKey);
            Thread.sleep(300);

            assertEquals(0, receivedEvents.get(),
                    "The hook must not retransmit keys injected by this application");
        } finally {
            executor.ReleasedKey(cKey);
            hooker.close();
        }
    }
}
