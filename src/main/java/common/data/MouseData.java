package common.data;

import common.eventtype.MouseEventType;


public class MouseData {
    private MouseEventType mouseEventType;

    private int mouseX;
    private int mouseY;
    private int dx;
    private int dy;
    private int WheelAmount; //上がマイナス
    
    private boolean pressed;

    private Modifiers modifiers;

    public MouseData() {
    }

    public MouseData(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public MouseData(MouseEventType mouseEventType, int mouseX, int mouseY, int dx, int dy, int wheelAmount,
            boolean pressed, Modifiers modifiers) {
        this.mouseEventType = mouseEventType;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.dx = dx;
        this.dy = dy;
        this.WheelAmount = wheelAmount;
        this.pressed = pressed;
        this.modifiers = modifiers;
    }

    public MouseEventType getMouseEventType() {
        return mouseEventType;
    }

    public void setMouseEventType(MouseEventType mouseEventType) {
        this.mouseEventType = mouseEventType;
    }

    public int getMouseX() {
        return mouseX;
    }

    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public void setMouseY(int mouseY) {
        this.mouseY = mouseY;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getWheelAmount() {
        return WheelAmount;
    }

    public void setWheelAmount(int wheelAmount) {
        WheelAmount = wheelAmount;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public void setModifiers(Modifiers modifiers) {
        this.modifiers = modifiers;
    }    
}
