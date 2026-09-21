package common.network;

import java.util.concurrent.ArrayBlockingQueue;
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
public abstract class NetworkManager implements ErrorHandle {
	protected final int portNumber;

	private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
	private volatile boolean threadRunning = false;
	private volatile boolean acceptAddTask = false;
	private static final int SEND_QUEUE_CAPACITY = 512;

	public record OutboundData(
			DataType dataType,
			Object data) {
	}

	private final BlockingQueue<OutboundData> sendQueue = new ArrayBlockingQueue<>(SEND_QUEUE_CAPACITY);
	private volatile boolean sendThreadRunning = false;
	private volatile boolean acceptSend = false;

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
			} catch (RuntimeException e) {
				errorListener.happenError("ネットワーク処理中にエラーが発生しました");
			}
		}
	});

	private final Thread sendThread = new Thread(() -> {
		while (sendThreadRunning) {
			try {
				OutboundData outbound = sendQueue.take();
				try {
					String sendData = JsonConverter.toJson(outbound.dataType(), outbound.data());
					send(sendData);
					if (!sendThreadRunning)
						break;
				} catch (JsonProcessingException e) {
					errorListener.happenError("JSON変換に失敗しました");
					break;
				}

			} catch (InterruptedException e) {
				break;
			}
		}
	});

	public NetworkManager(int portNumber) {
		this.portNumber = portNumber;
		threadStart();
	}

	public void startSendThread() {
		sendThreadRunning = true;
		acceptSend = true;
		sendThread.start();
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

	public void addSendQueue(DataType dataType, Object data) {
		if (acceptSend == false || sendThreadRunning == false)
			return;

		boolean addSuccess = sendQueue.offer(new OutboundData(dataType, data));
		if (!addSuccess)
			errorListener.happenError("送信キューが上限に達しました");

	}

	public void touchWall(MouseData mouseData) {
		addSendQueue(DataType.MOUSE, mouseData);
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

		sendThreadRunning = false;
		acceptSend = false;
		sendQueue.clear();
		sendThread.interrupt();

	}

	protected abstract void closeUdpAndTcp();

	@Override
	public void errorHandle() {
		close();
	}

}
