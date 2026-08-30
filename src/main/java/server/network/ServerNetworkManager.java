package server.network;

import common.network.NetworkManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ServerNetworkManager extends NetworkManager {
    UdpServer udp;
    TcpServer tcp;

    public ServerNetworkManager(int portNumber) {
        super(portNumber);
    }

    @Override
    public void start() {
        makeServer();
    }

    private void makeServer() {
        new Thread(() -> {
            boolean udpConnectSuccess = makeUdpServe();
            if (!udpConnectSuccess)
                return;

            makeTcpServer();

            listener.successConnect();
            tcpConnection = true;
        }).start();
    }

    private boolean makeUdpServe() {
        udp = new UdpServer(portNumber, listener);
        return udp.makeServer();
    }

    private void makeTcpServer() {
        tcp = new TcpServer(portNumber, listener);
        tcp.makeServer();
    }

    public void stopUdpServer() {
        udp.setRunning(false);
        udp.close();
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
