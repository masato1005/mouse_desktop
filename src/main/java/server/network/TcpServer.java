package server.network;

import java.io.IOException;
import java.net.ServerSocket;

import common.Listener.NetworkListener;
import common.main.ErrorListener;
import common.network.Tcp;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class TcpServer extends Tcp {
	private ServerSocket server;

	public TcpServer(int portNumber, NetworkListener listener, ErrorListener errorCallback) {
		super(portNumber, listener, errorCallback);
	}

	public boolean makeServer() {
		boolean successMakeSocket = makeSocket();
		if (!successMakeSocket)
			return false;
		boolean successMakeIn = makeIn();
		if (!successMakeIn)
			return false;
		boolean successMakeOut = makeOut();
		return successMakeOut;
	}

	private boolean makeSocket() {
		server = null;
		try {
			server = new ServerSocket(portNumber);
		} catch (IOException e) {
			errorListener.happenError("ソケットの生成で不具合が発生しました");
			return false;
		}
		System.out.println("接続待機中...");

		socket = null;
		try {
			socket = server.accept();
		} catch (IOException e) {
			errorListener.happenError("接続が中断されました");
			return false;
		}
		System.out.println("接続されました");
		return true;
	}

	@Override
	public void close() {
		boolean closeFailed = false;
		try {
			if (server != null)
				server.close();
		} catch (IOException e) {
			closeFailed = true;
		} finally {
			super.close();
		}
		if (closeFailed) {
			System.err.println("TCP待受ソケットの終了処理に失敗しました");
		}
	}
}
