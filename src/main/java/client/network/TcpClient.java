package client.network;

import java.io.IOException;
import java.net.Socket;

import common.main.ErrorListener;
import common.network.Tcp;
import client.network.listener.ClientTcpListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class TcpClient extends Tcp {
	private final String serverIP;

	public TcpClient(int portNumber, ClientTcpListener listener, String serverIP, ErrorListener errorListener) {
		super(portNumber, listener, errorListener);
		this.serverIP = serverIP;
	}

	public boolean connect() {
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
		socket = null;
		try {
			socket = new Socket(serverIP, portNumber);
		} catch (IOException e) {
			errorListener.happenError("ソケットの生成で不具合が発生しました");
			return false;
		}
		System.out.println("接続完了");
		return true;
	}
}
