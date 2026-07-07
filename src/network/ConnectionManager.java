package network;

import java.io.IOException;

import EventType.AppType;
import Handler.NetworkHandler;
import Listener.implemented.ImplementedNetworkListener;
import network.contents.client.TcpClient;
import network.contents.client.UdpClient;
import network.contents.sever.TcpServer;
import network.contents.sever.UdpServer;

public class ConnectionManager {
	private int portNumber;
	private AppType appType;
	private String serverIP;
	private boolean timeout = false;
	private boolean stopServer = false;

	private UdpServer udpS;
	private UdpClient udpC;
	private TcpServer tcpS;
	private TcpClient tcpC;

	private ImplementedNetworkListener listener = new ImplementedNetworkListener();

	public ConnectionManager(int portNumber) {
		this.portNumber = portNumber;
	}

	public void start() throws IOException {
		new Thread(() -> {
			switch (appType) {
			case SERVER:
				udpS = new UdpServer(portNumber, listener);
				try {
					udpS.makeServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (stopServer)
					break;
				tcpS = new TcpServer(portNumber, listener);
				tcpS.makeServer();
				
				listener.successConnect();
				break;

			case CLIENT:
				udpC = new UdpClient(portNumber, listener);
				udpC.makeConnection();

				if (timeout) {
					System.out.println("接続失敗");
				} else {
					serverIP = udpC.getServerIP();
					tcpC = new TcpClient(serverIP, portNumber,listener);
					tcpC.connect();
					listener.successConnect();
				}
				break;
			}
		}).start();
	}

	public void loop() throws IOException {
		switch (appType) {
		case SERVER:
			tcpS.loop();
			break;

		case CLIENT:
			tcpC.loop();
			break;
		}

	}

	public void sendData(Object data) {
		if (!(data instanceof String)) {
			System.out.println("dataの内容が適切な形になっていません");
		} else {
			String json = data.toString();
			switch (appType) {
			case SERVER:
				tcpS.send(json);
				break;
			case CLIENT:
				tcpC.send(json);
				break;
			}
		}

	}

	public void stopUdpServer() {
		udpS.sendStopMassage();
		setStopServer(true);
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

	public void setStopServer(boolean stopServer) {
		this.stopServer = stopServer;
	}

	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}

	//リスナー
	public void setListener(NetworkHandler handler){
		listener.setListener(handler);
	}

	
}
