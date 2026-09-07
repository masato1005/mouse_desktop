package common.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fasterxml.jackson.core.JsonProcessingException;

import common.data.MouseData;
import common.eventtype.DataType;
import common.json.JsonConverter;
import common.main.ErrorHandle;
import common.main.ErrorListener;
import common.network.listener.NetworkListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class NetworkManager implements ErrorHandle, ThreadStopper {
	protected final int portNumber;

	private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
	private boolean threadRunning = false;
	private boolean acceptAddTask = false;

	protected ErrorListener errorListener;
	protected NetworkListener listener;

	private final Thread networkThread = new Thread(() -> {
		while (threadRunning) {
			try {
				Runnable task = tasks.take();
				task.run();
				if (!threadRunning)
					break;
			} catch (InterruptedException e) {
				break;
			}
		}
	});

	public NetworkManager(int portNumber) {
		this.portNumber = portNumber;
		threadStart();
	}

	private void threadStart() {
		threadRunning = true;
		acceptAddTask = true;
		networkThread.start();
	}

	public void setEventListener(NetworkListener networkListener) {
		this.listener = networkListener;
	}

	public void setErrorListener(ErrorListener errorListener) {
		this.errorListener = errorListener;
	}

	public abstract void start();

	protected void addTask(Runnable task) {
		if (acceptAddTask)
			tasks.add(task);
	}

	public void sendData(Object data) {
		if (!(data instanceof String)) {
			System.out.println("dataの内容が適切な形になっていません");
		} else {
			String json = data.toString();
			send(json);
		}
	}

	public void touchWall(MouseData mouseData){
		try {
            String json = JsonConverter.toJson(DataType.MOUSE, mouseData);
            send(json);
        } catch (JsonProcessingException e) {
            errorListener.happenError("Json処理で不具合が発生しました");
        }
	}

	protected abstract void send(String json);

	public void systemExit() {
		close();
	}

	public final void close() {
		threadStop();
		closeUdpAndTcp();
	}

	private void threadStop() {
		threadRunning = false;
		acceptAddTask = false;
		tasks.clear();
		networkThread.interrupt();
	}

	protected abstract void closeUdpAndTcp();

	@Override
	public void errorHandle() {
		close();
	}

	@Override
	public void threadStopper() {
		close();
	}
}
