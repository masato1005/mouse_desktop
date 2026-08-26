package network;

import java.io.IOException;

import EventType.AppType;
import EventType.DataType;
import Handler.NetworkHandler;
import Json.JsonConverter;
import Listener.implemented.ImplementedNetworkListener;
import gui.contents.ErrorExitGUI;
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
	private boolean stopServer = false;
	private boolean udpConnection = false;
	private boolean tcpConnection = false;

	private UdpServer udpS;
	private UdpClient udpC;
	private TcpServer tcpS;
	private TcpClient tcpC;

	private final ImplementedNetworkListener listener = new ImplementedNetworkListener();

	public NetworkManager(int portNumber) {
		this.portNumber = portNumber;
	}

	public void start() throws IOException {
		new Thread(() -> {
			switch (appType) {
				case SERVER -> {
                                    udpS = new UdpServer(portNumber, listener);
                                    try {
                                        udpS.makeServer();
                                    } catch (IOException e) {
										System.out.println("UDPサーバーの作成に失敗しました");
										System.out.println("アプリを終了します");
										new ErrorExitGUI("UDPサーバーの作成に失敗しました");
                                    }
                                    if (stopServer)
                                        break;
                                    udpConnection = true;
                                    tcpS = new TcpServer(portNumber, listener);
                                    tcpS.makeServer();
                                    
                                    listener.successConnect();
                                    tcpConnection = true;
                        }

				case CLIENT -> {
                                    if(udpC == null)udpC = new UdpClient(portNumber, listener);
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

	public void loop() throws IOException {
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
			String json = converter.dataConverter(DataType.SYSTEMEXIT, null);
			sendData(json);
			closeTcp();
		}
	}

	private void closeUdp() {
		switch(appType){
			case CLIENT -> udpC.close();
			case SERVER -> udpS.close();
		}
	}

	private void closeTcp(){
		switch(appType){
			case CLIENT -> tcpC.close();
			case SERVER -> tcpS.close();
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

	public void setListener(NetworkHandler handler) {
		listener.setListener(handler);
	}

}
