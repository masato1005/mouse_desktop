package Listener;

public interface GUIListener {
	public void choiseServer();

	public void choiseClient();

	public void pushStop();

	public void retry();
	
	public void choiceNorthWall();
	
	public void choiceSouthWall();
	
	public void choiceWestWall();
	
	public void choiceEastWall();

    public void pushedSuccessOkButton();

	public void moveWheel(int amount);

	public void clickLeftMouse(boolean pressed);

	public void clickWheelMouse(boolean pressed);

	public void clickRightMouse(boolean pressed);

	public void systemExit();
}
