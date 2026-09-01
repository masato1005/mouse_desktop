package server.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import common.Listener.NetworkListener;
import common.gui.contents.ErrorExitGui;
import common.network.ErrorCallback;
import common.network.Udp;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class UdpServer extends Udp {

	private volatile boolean running = false;

	public UdpServer(int portNumber, NetworkListener listener, ErrorCallback errorCallback) {
		super(portNumber, listener, errorCallback);
	}

	@Override
	protected void receive(DatagramPacket receivePacket) throws IOException {
		try {
			socket.receive(receivePacket);
		} catch (IOException e) {
			boolean wasRunning = running;
			if (wasRunning)
				errorCallback.happenError();
			else
				close();
			if (wasRunning)
				new ErrorExitGui("メッセージの取得に失敗しました");
			throw e;
		}
	}

	private void waitingClient() {
		running = true;
		listener.waitingClient();
		System.out.println("待機中...");
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
				if (checkConnectMassage(password,msg)) {
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

	public boolean isRunning() {
		return running;
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
