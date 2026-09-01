package server.mouse;

import java.util.Timer;
import java.util.TimerTask;

import common.EventType.WallType;
import common.Listener.MouseEventListener;
import common.Listener.implemented.ImplementedMouseListener;
import common.main.ErrorHandle;
import common.main.ErrorListener;

public class ServerMouseManager implements ErrorHandle, MouseCallback{
    private final ImplementedMouseListener eventListener = new ImplementedMouseListener();
    private final int START_TIME = 0;
    private final int TIMER_RATE = 16;

    @SuppressWarnings("FieldMayBeFinal")
    private Timer mouseTimer = new Timer(true);

    private CursorLocater serverMouse;
    private ErrorListener errorListener;

    public void setEventListener(MouseEventListener eventListener) {
        this.eventListener.setListener(eventListener);
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setWallType(WallType wallType) {
        serverMouse.setWallType(wallType);
    }

    public void start() {
        serverMouse = new CursorLocater(eventListener, errorListener,this);
        startTimer(serverMouse::updateAndCheck);
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

    private void stopTimer(){
		mouseTimer.cancel();
	}


    @Override
    public void errorHandle() {
        stopTimer();
    }

    @Override
    public void receivedCursor() {
        
    }

    @Override
    public void touchWall(){
        
    }

}
