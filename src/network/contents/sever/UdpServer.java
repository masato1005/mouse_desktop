package network.contents.sever;

import Listener.NetworkListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UdpServer {
	private int portNumber;
	private NetworkListener netListener;
	DatagramSocket socket;

	public UdpServer(int portNumber, NetworkListener netListener) {
		this.portNumber = portNumber;
		this.netListener = netListener;
	}

	public void makeServer() throws IOException {
		//ポート番号のUDPソケットを生成
		socket = new DatagramSocket(portNumber);

		//
		byte[] buffer = new byte[1024];//受信データ用バッファ（最大で1024バイト）
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		waitMessage(socket, packet);
	}

	private void waitMessage(DatagramSocket socket, DatagramPacket packet) {
		//サーバーを常に動作させる
		netListener.searchNofitication();
		System.out.println("待機中...");
		while (true) {
			//クライアントからのUDPパケットを受信
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			//受信したバイト列を文字列に変換
			String msg = new String(packet.getData(), 0, packet.getLength());
			System.out.println("受信: " + msg);

			//受信した文字列がクライアントと同じだったら
			if (msg.equals("DISCOVER_SERVER")) {
				sendMessage(packet);
				break;
			}
			
			if(msg.equals("STOP")) {
				netListener.checkStopSever();
				break;
			}
		}
		socket.close();
	}

	private void sendMessage(DatagramPacket packet) {
		//返信メッセージをバイト列に変換
		byte[] response = "SERVER_HERE".getBytes();

		//返信パケットの生成
		DatagramPacket reply = new DatagramPacket(
				response, //データ
				response.length, //データの長さ
				packet.getAddress(), // クライアントのIP
				packet.getPort()//クライアントのポート番号
		);

		try {
			socket.send(reply);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		System.out.println("返信した");
		socket.close();
	}
	
	public void sendStopMassage() {
		byte[] massage = "STOP".getBytes();
		InetAddress address = null;
		try {
			address = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		DatagramPacket stopMassage = new DatagramPacket(
				massage, //データ
				massage.length, //データの長さ
				address, // クライアントのIP
				portNumber//クライアントのポート番号
		);
		try {
			socket.send(stopMassage);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}