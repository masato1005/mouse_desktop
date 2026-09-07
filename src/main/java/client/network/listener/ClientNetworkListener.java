package client.network.listener;

import common.network.listener.ConnectionListener;
import common.network.listener.NetworkListener;

public interface ClientNetworkListener extends NetworkListener, ClientTcpListener, ClientUdpListener,
        ConnectionListener {
}
