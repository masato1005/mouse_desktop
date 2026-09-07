package client.network;

import common.network.NetworkManager;
import client.network.listener.ClientNetworkListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ClientNetworkManager extends NetworkManager implements TimeoutCallback {
    private UdpClient udp;
    private TcpClient tcp;
    private String serverIP;

    public ClientNetworkManager(int portNumber) {
        super(portNumber);
    }

    @Override
    public void start() {
        addTask(this::makeClient);
    }

    private void makeClient() {
        boolean udpSuccessConnect = makeUdpClient();
        if (!udpSuccessConnect)
            return;

        serverIP = udp.getServerIP();

        boolean tcpSuccessConnect = makeTcpClient();
        if(!tcpSuccessConnect)
            return;

        listener.successConnect();
        addTask(tcp::receive);

    }

    private boolean makeUdpClient() {
        udp = new UdpClient(portNumber, clientListener(), this, errorListener);
        return udp.makeConnection();
    }

    private boolean makeTcpClient() {
        tcp = new TcpClient(portNumber, clientListener(), serverIP,errorListener);
        return tcp.connect();
    }

    @Override
    protected void send(String json) {
        tcp.send(json);
    }

    @Override
    protected void closeUdpAndTcp() {
        if (udp != null)
            udp.close();
        if (tcp != null)
            tcp.close();
    }

    @Override
    public void timeoutCallback() {
        System.out.println("接続失敗");
        clientListener().timeout();
    }

    private ClientNetworkListener clientListener() {
        return (ClientNetworkListener) listener;
    }
}
