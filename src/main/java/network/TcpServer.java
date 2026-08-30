package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

import Listener.NetworkListener;
import gui.contents.ErrorExitGui;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class TcpServer extends Tcp {
	private ServerSocket server;

	public TcpServer(int portNumber, NetworkListener listener) {
		super(portNumber, listener);
	}

	public void makeServer() {
		server = null;
		try {
			server = new ServerSocket(portNumber);
		} catch (IOException e) {
			new ErrorExitGui("ソケットの生成で不具合が発生しました");
		}
		System.out.println("接続待機中...");

		socket = null;
		try {
			socket = server.accept();
		} catch (IOException e) {
			new ErrorExitGui("接続が中断されました");
		}
		System.out.println("接続されました");

		try {
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			new ErrorExitGui("ソケットの受信機能の起動に失敗しました");
		}

		try {
			out = new PrintWriter(
					socket.getOutputStream(), true);
		} catch (IOException e) {
			new ErrorExitGui("ソケットの送信機能の起動に失敗しました");
		}
	}
}
