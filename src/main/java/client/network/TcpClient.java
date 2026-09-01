package client.network;

import java.io.IOException;
import java.net.Socket;

import common.Listener.NetworkListener;
import common.gui.contents.ErrorExitGui;
import common.network.ErrorCallback;
import common.network.Tcp;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class TcpClient extends Tcp {
	private final String serverIP;

	public TcpClient(int portNumber, NetworkListener listener, String serverIP, ErrorCallback errorCallback) {
		super(portNumber, listener, errorCallback);
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
			errorCallback.happenError();
			new ErrorExitGui("ソケットの生成で不具合が発生しました");
			return false;
		}
		System.out.println("接続完了");
		return true;
	}
}
