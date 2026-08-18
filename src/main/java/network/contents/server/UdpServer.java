package network.contents.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Listener.NetworkListener;
import gui.contents.ErrorExitGUI;

public class UdpServer {
	private final int portNumber;
	private final NetworkListener netListener;
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
            try (socket) {
                //サーバーを常に動作させる
                netListener.searchNofitication();
                System.out.println("待機中...");
                while (true) {
                    //クライアントからのUDPパケットを受信
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        new ErrorExitGUI("メッセージの取得に失敗しました");
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
            }
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
			new ErrorExitGUI("送信に失敗しました");
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
			new ErrorExitGUI("終了処理の");
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
			new ErrorExitGUI("メッセージの送信に失敗しました");
		}
	}

    public void close() {
		socket.close();
    }
}