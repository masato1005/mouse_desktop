package server.mouse;

import java.util.Timer;
import java.util.TimerTask;

import common.data.MouseData;
import common.eventtype.WallType;
import common.mouse.MouseManager;

public class ServerMouseManager extends MouseManager implements MouseCallback {
    private final int START_TIME = 0;
    private final int TIMER_RATE = 16;

    @SuppressWarnings("FieldMayBeFinal")
    private Timer mouseTimer = new Timer(true);

    private ServerCursorLocater cursorLocater;

    @Override
    public void start() {
        cursorLocater = new ServerCursorLocater(listener, errorListener, this);
        startTimer(cursorLocater::updateAndCheck);
    }

    private void startTimer(Runnable task) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
        mouseTimer.scheduleAtFixedRate(timerTask, START_TIME, TIMER_RATE);
    }

    @Override
    public void setWallType(WallType wallType) {
        cursorLocater.setWallType(wallType);
    }

    @Override
    public void errorHandle() {
        stopTimer();
    }

    private void stopTimer() {
        mouseTimer.cancel();
    }

    @Override
    public void mouseMoved(MouseData mouseData) {

    }

    @Override
    public void buttonPressed(int buttonNumber) {

    }

    @Override
    public void buttonReleased(int buttonNumber) {

    }

    @Override
    public void receiveData(MouseData mouseData) {
        cursorLocater.receiveCursor(mouseData);
    }

}
