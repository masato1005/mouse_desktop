package client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import common.main.ErrorListener;
import common.network.Udp;
import common.network.listener.UdpListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class UdpClient extends Udp {
	private String serverIP;
	private final TimeoutCallback timeoutCallback;

	public UdpClient(int portNumber, UdpListener listener, TimeoutCallback timeoutCallback,
			ErrorListener errorCallback) {
		super(portNumber, listener, errorCallback);
		this.timeoutCallback = timeoutCallback;
	}

	public boolean makeConnection() {
		try {
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
			while (true) {
				receive(receivePacket);
				String msg = convertReceivePacketToString(receivePacket);
				String password = "SERVER_HERE";
				if (checkConnectMassage(password, msg))
					return true;
			}
		} catch (IOException e) {
			return false;
		} finally {
			close();
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

}
