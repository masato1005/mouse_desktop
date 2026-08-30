package common.Listener;

public interface GuiListener {
	public void chooseServer();

	public void chooseClient();

    public void pushedSuccessOkButton();

	public void choiceNorthWall();
	
	public void choiceSouthWall();
	
	public void choiceWestWall();
	
	public void choiceEastWall();
	
	public void pushStop();

	public void retry();

	public void moveWheel(int amount);

	public void clickLeftMouse(boolean pressed);

	public void clickWheelMouse(boolean pressed);

	public void clickRightMouse(boolean pressed);

	public void systemExit();
}
