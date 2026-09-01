package common.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import common.Listener.NetworkEventListener;
import common.Listener.implemented.ImplementedNetworkListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class NetworkManager implements ErrorCallback, NetworkThreadStopper {
	protected final int portNumber;

	private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
	private final AtomicBoolean closed = new AtomicBoolean();
	private volatile boolean threadRunning = false;
	private final Thread networkThread = new Thread(() -> {
		while (threadRunning) {
			try {
				Runnable task = tasks.take();
				task.run();
			} catch (InterruptedException e) {
				threadStop();
			}
		}
	});

	private void exitThread() throws InterruptedException {
		throw new InterruptedException();
	}

	protected final ImplementedNetworkListener listener = new ImplementedNetworkListener();

	public NetworkManager(int portNumber) {
		this.portNumber = portNumber;
		threadStart();
	}

	private void threadStart() {
		threadRunning = true;
		networkThread.start();
	}

	private void threadStop() {
		threadRunning = false;
		tasks.clear();
		addTask(this::threadStart);
		networkThread.interrupt();
	}

	public void setListener(NetworkEventListener eventListener) {
		listener.setListener(eventListener);
	}

	public void start() {
	}

	protected synchronized void addTask(Runnable task) {
		if (!closed.get() && threadRunning)
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
		allClose();
	}

	protected void allClose() {
	}

	@Override
	public void happenError() {
		close();
	}

	@Override
	public void networkThreadStopper() {
		close();
	}
}
