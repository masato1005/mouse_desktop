package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Listener.NetworkListener;
import gui.contents.ErrorExitGui;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class TcpClient extends Tcp {
	private final String serverIP;

	public TcpClient(int portNumber, NetworkListener listener, String serverIP) {
		super(portNumber, listener);
		this.serverIP = serverIP;
	}

	public void connect() {
		socket = null;
		try {
			socket = new Socket(serverIP, portNumber);
		} catch (IOException e) {
			new ErrorExitGui("ソケットの生成で不具合が発生しました");
		}
		System.out.println("接続完了");

		try {
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			new ErrorExitGui("受信機能の起動に失敗しました");
		}

		try {
			out = new PrintWriter(
					socket.getOutputStream(), true);
		} catch (IOException e) {
			new ErrorExitGui("送信機能の起動に失敗しました");
		}
	}
}
