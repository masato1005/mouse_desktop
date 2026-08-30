package server.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import common.Listener.NetworkListener;
import common.gui.contents.ErrorExitGui;
import common.network.Udp;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class UdpServer extends Udp {

	private boolean running = false;

	public UdpServer(int portNumber, NetworkListener listener) {
		super(portNumber, listener);
	}

	@Override
	protected void receive(DatagramPacket receivePacket) throws IOException {
		try {
			socket.receive(receivePacket);
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

	private void waitingClient() {
		running = true;
		netListener.waitingClient();
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

				if (checkConnectMassage(msg)) {
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

	public void setRunning(boolean running) {
		this.running = running;
	}
}
