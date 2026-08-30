package network.contents.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Listener.NetworkListener;
import gui.contents.ErrorExitGui;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class UdpServer {
	private final int portNumber;
	private final NetworkListener netListener;
	private boolean running = false;
	DatagramSocket socket;

	public UdpServer(int portNumber, NetworkListener netListener) {
		this.portNumber = portNumber;
		this.netListener = netListener;
	}

	public boolean makeServer() {
		try {
			makeSocket();
			DatagramPacket packet = makePacket();
			return waitMessage(packet);
		} catch (SocketException e) {
			return false;
		} finally {
			close();
		}

	}

	private void makeSocket() throws SocketException {
		try {
			socket = new DatagramSocket(portNumber);
		} catch (SocketException e) {
			new ErrorExitGui("ソケット生成に失敗しました");
			throw e;
		}
	}

	private DatagramPacket makePacket() {
		byte[] buffer = new byte[1024];
		return new DatagramPacket(buffer, buffer.length);
	}

	private boolean waitMessage(DatagramPacket packet) {
		try {
			if (socket == null)
				return false;

			waitingClient();

			while (running) {
				receive(packet);

				String msg = convertReceivePacketToString(packet);

				if (checkConnectMassage(msg)) {
					sendConnectMessage(packet);
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
		netListener.waitingClient();
		System.out.println("待機中...");
	}

	private void receive(DatagramPacket packet) throws IOException {
		try {
			socket.receive(packet);
		} catch (IOException e) {
			if (running)
				new ErrorExitGui("メッセージの取得に失敗しました");
			throw e;
		}
	}

	private String convertReceivePacketToString(DatagramPacket packet) {
		String msg = new String(packet.getData(), 0, packet.getLength());
		System.out.println("受信: " + msg);
		return msg;
	}

	private Boolean checkConnectMassage(String msg) {
		return msg.equals("DISCOVER_SERVER");
	}

	private void sendConnectMessage(DatagramPacket packet) throws IOException {
		byte[] response = "SERVER_HERE".getBytes();

		DatagramPacket reply = new DatagramPacket(
				response, // データ
				response.length, // データの長さ
				packet.getAddress(), // クライアントのIP
				packet.getPort()// クライアントのポート番号
		);

		try {
			socket.send(reply);
		} catch (IOException e) {
			new ErrorExitGui("送信に失敗しました");
			throw e;
		}
		System.out.println("返信した");
		close();
	}

	public boolean isRunning() {
		return running;
	}

	public void close() {
		if (socket != null) {
			running = false;
			socket.close();
		}

	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
