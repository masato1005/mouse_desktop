package server.network;

import common.network.NetworkManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ServerNetworkManager extends NetworkManager {
    private UdpServer udp;
    private TcpServer tcp;

    public ServerNetworkManager(int portNumber) {
        super(portNumber);
    }

    @Override
    public void start() {
        addTask(this::makeServer);
    }

    private void makeServer() {
        boolean udpSuccessConnect = makeUdpServe();
        if (!udpSuccessConnect)
            return;

        boolean tcpSuccessConnect = makeTcpServer();
        if (!tcpSuccessConnect)
            return;

        listener.successConnect();
        addTask(tcp::receive);
    }

    private boolean makeUdpServe() {
        udp = new UdpServer(portNumber, listener, this);
        return udp.makeServer();
    }

    private boolean makeTcpServer() {
        tcp = new TcpServer(portNumber, listener, this);
        return tcp.makeServer();
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
    protected void allClose() {
        if (udp != null)
            udp.close();
        if (tcp != null)
            tcp.close();
    }
}
