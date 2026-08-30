package network.contents.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import Listener.NetworkListener;
import gui.contents.ErrorExitGui;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class UdpClient {
	private final int portNumber;
	private String serverIP;
	private final String clientIP;
	private final NetworkListener netListener;
	private DatagramSocket socket = null;

	public UdpClient(int portNumber, NetworkListener netListener) {
		this.portNumber = portNumber;// ポート番号
		InetAddress local = null;
		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			new ErrorExitGui("ホストのIPアドレスを取得できませんでした");
		}
		this.clientIP = local.getHostAddress();
		this.netListener = netListener;
	}

	public void makeConnection() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			new ErrorExitGui("ソケットの生成で不具合が発生しました");
		}
		// ブロードキャスト送信を許可
		try {
			socket.setBroadcast(true);
		} catch (SocketException e) {
			new ErrorExitGui("ブロードキャストの送信に失敗しました");
		}

		// 三秒でタイムアウト
		try {
			socket.setSoTimeout(3000);
		} catch (IOException e) {
			new ErrorExitGui("タイムアウトしました");
		}

		// DISCOVER_SERVERをバイト列に変換
		byte[] sendData = "DISCOVER_SERVER".getBytes();

		// ブロードキャストアドレス
		DatagramPacket sendPacket = null;
		try {
			sendPacket = new DatagramPacket(
					sendData, // 送信データ
					sendData.length, // 送信データの長さ
					InetAddress.getByName("255.255.255.255"), // 宛先
					portNumber);
		} catch (UnknownHostException e) {
			new ErrorExitGui("ホストのIPアドレスを取得できませんでした");
		} // サーバーのポート番号

		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			new ErrorExitGui("メッセージの送信に失敗しました");
		} // UDPパケットを送信
		System.out.println("探索送信");

		// 返信待ち
		byte[] recvBuf = new byte[1024];// 受信用バッファを作成（最大1024バイト）
		DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

		try {
			socket.receive(receivePacket);
			serverIP = receivePacket.getAddress().getHostAddress();
			System.out.println("サーバー発見: " + serverIP);
		} catch (SocketTimeoutException e) {
			netListener.checkTimeout();
		} catch (IOException e) {
			new ErrorExitGui("メッセージの受信に失敗しました");
		}

		// リソースの解放
		socket.close();
	}

	public String getServerIP() {
		return serverIP;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void close() {
		socket.close();
	}
}
