package client.network;

import common.network.NetworkManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ClientNetworkManager extends NetworkManager {
    private UdpClient udp;
    private TcpClient tcp;

    public ClientNetworkManager(int portNumber) {
        super(portNumber);
    }

    @Override
    public void start() {
        if (udp == null)
            udp = new UdpClient(portNumber, listener);
        udp.makeConnection();

        if (timeout) {
            System.out.println("接続失敗");
        } else {
            udpConnection = true;
            serverIP = udp.getServerIP();
            tcp = new TcpClient(portNumber, listener, serverIP);
            tcp.connect();
            listener.successConnect();
            tcpConnection = true;
        }
    }

    @Override
    protected void send(String json) {
        tcp.send(json);
    }

    @Override
    public void systemExit() {
        allClose(udp,tcp);
    }
}
