package server.network.listener;

import common.network.listener.ConnectionListener;
import common.network.listener.NetworkListener;

public interface ServerNetworkListener extends NetworkListener, ServerTcpListener, ServerUdpListener,
        ConnectionListener {
}
