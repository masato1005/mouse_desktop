package client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import common.main.ErrorListener;
import common.network.Udp;
import common.network.listener.UdpListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class UdpClient extends Udp {
	private String serverIP;
	private String clientIP;
	private final TimeoutCallback timeoutCallback;

	public UdpClient(int portNumber, UdpListener listener, TimeoutCallback timeoutCallback,
			ErrorListener errorCallback) {
		super(portNumber, listener, errorCallback);
		this.timeoutCallback = timeoutCallback;
	}

	public boolean makeConnection() {
		try {
			getIpAddress();
			makeSocket();
			allowBroadcast();
			setTimeout();

			String sendMsg = "DISCOVER_SERVER";
			byte[] sendData = makeMassageData(sendMsg);
			String opponentName = "255.255.255.255";
			DatagramPacket sendPacket = makeSendPacket(sendData, InetAddress.getByName(opponentName));

			send(sendPacket);

			byte[] ReceiveBuffer = makeReceiveBuffer();
			DatagramPacket receivePacket = makeReceivePacket(ReceiveBuffer);
			receive(receivePacket);
			String msg = convertReceivePacketToString(receivePacket);
			String password = "SERVER_HERE";
			if (!checkConnectMassage(password,msg))
				return false;
			return true;

		} catch (IOException e) {
			return false;
		} finally {
			close();
		}
	}

	private void getIpAddress() throws UnknownHostException {
		try {
			InetAddress local = InetAddress.getLocalHost();
			this.clientIP = local.getHostAddress();
		} catch (UnknownHostException e) {
			errorListener.happenError("ホストのIPアドレスを取得できませんでした");
			throw e;
		}
	}

	private void allowBroadcast() throws SocketException {
		try {
			socket.setBroadcast(true);
		} catch (SocketException e) {
			errorListener.happenError("ブロードキャストの送信に失敗しました");
			throw e;
		}
	}

	private void setTimeout() throws IOException {
		try {
			socket.setSoTimeout(3000);
		} catch (IOException e) {
			errorListener.happenError("タイムアウトの設定に失敗しました");
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
			close();
			timeoutCallback.timeoutCallback();
			throw e;
		} catch (IOException e) {
			errorListener.happenError("メッセージの受信に失敗しました");
			throw e;
		}
	}

	public String getServerIP() {
		return serverIP;
	}

	public String getClientIP() {
		return clientIP;
	}

}
