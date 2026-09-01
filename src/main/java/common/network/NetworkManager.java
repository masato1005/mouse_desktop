package common.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import common.Listener.NetworkEventListener;
import common.Listener.implemented.ImplementedNetworkListener;
import common.main.ErrorHandle;
import common.main.ErrorListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class NetworkManager implements ErrorHandle, ThreadStopper {
	protected final int portNumber;

	private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
	private boolean threadRunning = false;
	private boolean acceptAddTask = false;

	protected ErrorListener errorListener;

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

	public void setErrorListener(ErrorListener errorListener) {
		this.errorListener = errorListener;
	}

	protected final ImplementedNetworkListener listener = new ImplementedNetworkListener();

	public NetworkManager(int portNumber) {
		this.portNumber = portNumber;
		threadStart();
	}

	private void threadStart() {
		threadRunning = true;
		acceptAddTask = true;
		networkThread.start();
	}

	private void threadStop() {
		threadRunning = false;
		acceptAddTask = false;
		tasks.clear();
		networkThread.interrupt();
	}

	public void setEventListener(NetworkEventListener eventListener) {
		listener.setListener(eventListener);
	}

	public void start() {
	}

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

	protected void send(String json) {
	}

	public void systemExit() {
		close();
	}

	public final void close() {
		threadStop();
		closeUdpAndTcp();
	}

	protected void closeUdpAndTcp() {
	}

	@Override
	public void errorHandle() {
		close();
	}

	@Override
	public void threadStopper() {
		close();
	}
}
