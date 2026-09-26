package common.data;

import common.eventType.KeyboardEventType;

public class KeyboardData {
    private KeyboardEventType eventType;
    private String keyName;
    private int virtualKeyCode;
    private int scanCode;
    private boolean repeat;

    public KeyboardData(KeyboardEventType eventType,String keyName){
        this.eventType = eventType;
        this.keyName = keyName;
    }

    public KeyboardData(){}

    public KeyboardData(KeyboardEventType eventType,String keyName,int virtualKeyCode,int scanCode,boolean repeat){
        this.eventType = eventType;
        this.keyName = keyName;
        this.virtualKeyCode = virtualKeyCode;
        this.scanCode = scanCode;
        this.repeat = repeat;
    }

    public KeyboardEventType getEventType() {
        return eventType;
    }

    public void setEventType(KeyboardEventType eventType) {
        this.eventType = eventType;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public int getVirtualKeyCode() {
        return virtualKeyCode;
    }

    public void setVirtualKeyCode(int virtualKeyCode) {
        this.virtualKeyCode = virtualKeyCode;
    }

    public int getScanCode() {
        return scanCode;
    }

    public void setScanCode(int scanCode) {
        this.scanCode = scanCode;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
}
