package network.contents.client;

import Listener.NetworkListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UdpClient {
	private int portNumber;
	private String serverIP;
	private String clientIP;
	private NetworkListener netListener;

	public UdpClient(int portNumber, NetworkListener netListener) {
		this.portNumber = portNumber;// ポート番号
		InetAddress local = null;
		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.clientIP = local.getHostAddress();
		this.netListener = netListener;
	}

	public void makeConnection() {
		//UDP送信用のソケットを生成
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		//ブロードキャスト送信を許可
		try {
			socket.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		//三秒でタイムアウト
		try {
			socket.setSoTimeout(3000);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//DISCOVER_SERVERをバイト列に変換
		byte[] sendData = "DISCOVER_SERVER".getBytes();

		// ブロードキャストアドレス
		DatagramPacket sendPacket = null;
		try {
			sendPacket = new DatagramPacket(
					sendData, //送信データ
					sendData.length, //送信データの長さ
					InetAddress.getByName("255.255.255.255"), //宛先
					portNumber);
		} catch (UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} //サーバーのポート番号

		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} //UDPパケットを送信
		System.out.println("探索送信");

		// 返信待ち
		byte[] recvBuf = new byte[1024];//受信用バッファを作成（最大1024バイト）
		DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

		try {
			socket.receive(receivePacket);
			serverIP = receivePacket.getAddress().getHostAddress();
			System.out.println("サーバー発見: " + serverIP);
		} catch (SocketTimeoutException e) {
			netListener.checkTimeout();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//リソースの解放
		socket.close();
	}

	public String getServerIP() {
		return serverIP;
	}

	public String getClientIP() {
		return clientIP;
	}
}
