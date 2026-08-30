package network;

import Listener.NetworkEventListener;
import Listener.implemented.ImplementedNetworkListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class NetworkManager {
	protected final int portNumber;
	protected String serverIP;
	protected boolean timeout = false;
	protected boolean udpConnection = false;
	boolean tcpConnection = false;

	protected Thread networkThread;

	protected final ImplementedNetworkListener listener = new ImplementedNetworkListener();

	public NetworkManager(int portNumber) {
		this.portNumber = portNumber;
	}

	public void setListener(NetworkEventListener eventListener) {
		listener.setListener(eventListener);
	}

	public void start() {
	}

	public void sendData(Object data) {
		if (!(data instanceof String)) {
			System.out.println("dataの内容が適切な形になっていません");
		} else {
			String json = data.toString();
			send(json);
		}
	}

	protected void send(String json) {
	}

	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}

	public void systemExit() {
    }

	protected void allClose(Udp udp, Tcp tcp) {
		if (udp != null)
			udp.close();
		if (tcp != null)
			tcp.close();
	}
}
