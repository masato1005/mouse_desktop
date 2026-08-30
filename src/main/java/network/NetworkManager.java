package network;

import EventType.AppType;
import EventType.DataType;
import Json.JsonConverter;
import Listener.NetworkEventListener;
import Listener.implemented.ImplementedNetworkListener;
import network.contents.client.TcpClient;
import network.contents.client.UdpClient;
import network.contents.server.TcpServer;
import network.contents.server.UdpServer;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class NetworkManager {
	private final int portNumber;
	private AppType appType;
	private String serverIP;
	private boolean timeout = false;
	private boolean udpConnection = false;
	private boolean tcpConnection = false;

	private UdpServer udpS;
	private UdpClient udpC;
	private TcpServer tcpS;
	private TcpClient tcpC;
	private Thread networkThread;

	private final ImplementedNetworkListener listener = new ImplementedNetworkListener();

	public NetworkManager(int portNumber) {
		this.portNumber = portNumber;
	}

	public void setListener(NetworkEventListener eventListener) {
		listener.setListener(eventListener);
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

	public void start() {
		if(appType == null)
			return;
		new Thread(() -> {
			switch (appType) {
				case SERVER -> {
					boolean udpConnectSuccess = makeUdpServe();
					if (!udpConnectSuccess)
						break;

					makeTcpServer();

					listener.successConnect();
					tcpConnection = true;
				}

				case CLIENT -> {
					if (udpC == null)
						udpC = new UdpClient(portNumber, listener);
					udpC.makeConnection();

					if (timeout) {
						System.out.println("接続失敗");
					} else {
						udpConnection = true;
						serverIP = udpC.getServerIP();
						tcpC = new TcpClient(serverIP, portNumber, listener);
						tcpC.connect();
						listener.successConnect();
						tcpConnection = true;
					}
				}
			}
		}).start();
	}

	private boolean makeUdpServe() {
		udpS = new UdpServer(portNumber, listener);
		return udpS.makeServer();
	}

	private void makeTcpServer() {
		tcpS = new TcpServer(portNumber, listener);
		tcpS.makeServer();
	}

	public void loop() {
		switch (appType) {
			case SERVER -> tcpS.loop();
			case CLIENT -> tcpC.loop();
		}

	}

	public void sendData(Object data) {
		if (!(data instanceof String)) {
			System.out.println("dataの内容が適切な形になっていません");
		} else {
			String json = data.toString();
			switch (appType) {
				case SERVER -> tcpS.send(json);
				case CLIENT -> tcpC.send(json);
			}
		}
	}

	public void stopUdpServer() {
		udpS.setRunning(false);
		udpS.close();
	}

	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}

	public void systemExit() {
		if (udpConnection == false & tcpConnection == false) {
			return;
		}
		if (udpConnection == true & tcpConnection == false) {
			closeUdp();
			return;
		}
		if (tcpConnection == true) {
			JsonConverter converter = new JsonConverter();
			String json = converter.dataConverter(DataType.SYSTEM_EXIT, null);
			sendData(json);
			closeTcp();
		}
	}

	private void closeUdp() {
		switch (appType) {
			case CLIENT -> udpC.close();
			case SERVER -> udpS.close();
		}
	}

	private void closeTcp() {
		switch (appType) {
			case CLIENT -> tcpC.close();
			case SERVER -> tcpS.close();
		}
	}

}
