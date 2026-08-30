package client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import common.Listener.NetworkListener;
import common.gui.contents.ErrorExitGui;
import common.network.Udp;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class UdpClient extends Udp {
	private String serverIP;
	private String clientIP;

	public UdpClient(int portNumber, NetworkListener listener) {
		super(portNumber, listener);
		getIpAddress();
	}

	private void getIpAddress() {
		try {
			InetAddress local = InetAddress.getLocalHost();
			this.clientIP = local.getHostAddress();
		} catch (UnknownHostException e) {
			new ErrorExitGui("ホストのIPアドレスを取得できませんでした");
		}

	}

	private void allowBroadcast() throws SocketException {
		try {
			socket.setBroadcast(true);
		} catch (SocketException e) {
			new ErrorExitGui("ブロードキャストの送信に失敗しました");
			throw e;
		}
	}

	private void setTimeout() throws IOException {
		try {
			socket.setSoTimeout(3000);
		} catch (IOException e) {
			new ErrorExitGui("タイムアウトの設定に失敗しました");
			throw e;
		}
	}

	@Override
	protected void receive(DatagramPacket receivePacket) throws IOException {
		try {
			socket.receive(receivePacket);
			serverIP = receivePacket.getAddress().getHostAddress();
			System.out.println("サーバー発見: " + serverIP);
		} catch (SocketTimeoutException e) {
			netListener.checkTimeout();
		} catch (IOException e) {
			new ErrorExitGui("メッセージの受信に失敗しました");
			throw e;
		}
	}

	public boolean makeConnection() {
		try {
			makeSocket();
			allowBroadcast();
			setTimeout();

			String msg = "DISCOVER_SERVER";
			byte[] sendData = makeMassageData(msg);
			String opponentName = "255.255.255.255";
			DatagramPacket sendPacket = makeSendPacket(sendData, InetAddress.getByName(opponentName));

			send(sendPacket);

			byte[] ReceiveBuffer = makeReceiveBuffer();
			DatagramPacket receivePacket = makeReceivePacket(ReceiveBuffer);

			receive(receivePacket);

			socket.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public String getServerIP() {
		return serverIP;
	}

	public String getClientIP() {
		return clientIP;
	}

}
