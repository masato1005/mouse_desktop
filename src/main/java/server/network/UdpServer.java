package server.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import common.main.ErrorListener;
import common.network.Udp;
import server.network.listener.ServerUdpListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class UdpServer extends Udp {
	private final ServerUdpListener listener;
	private volatile boolean running = false;

	public UdpServer(int portNumber, ServerUdpListener listener, ErrorListener errorCallback) {
		super(portNumber, listener, errorCallback);
		this.listener = listener;
	}

	public boolean makeServer() {
		try {
			makeSocket();
			byte[] receiveBuffer = makeReceiveBuffer();
			DatagramPacket receivePacket = makeReceivePacket(receiveBuffer);
			return waitMessage(receivePacket);
		} catch (SocketException e) {
			return false;
		} finally {
			close();
		}
	}

	private boolean waitMessage(DatagramPacket receivePacket) {
		try {
			if (socket == null)
				return false;

			waitingClient();

			while (running) {
				receive(receivePacket);

				String msg = convertReceivePacketToString(receivePacket);
				String password = "DISCOVER_SERVER";
				if (checkConnectMassage(password, msg)) {
					byte[] sendData = makeMassageData("SERVER_HERE");
					DatagramPacket sendPacket = makeSendPacket(sendData, receivePacket.getAddress());
					send(sendPacket);
					close();
					return true;
				}
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	private void waitingClient() {
		running = true;
		listener.waitingClient();
		System.out.println("待機中...");
	}

	@Override
	protected void receive(DatagramPacket receivePacket) throws IOException {
		try {
			socket.receive(receivePacket);
		} catch (IOException e) {
			if (!running)
				return;
			errorListener.happenError("メッセージの取得に失敗しました");
			throw e;
		}
	}

	@Override
	public void close() {
		running = false;
		super.close();
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
