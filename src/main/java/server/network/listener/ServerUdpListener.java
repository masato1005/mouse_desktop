package server.network.listener;

import common.network.listener.UdpListener;

public interface ServerUdpListener extends UdpListener {
    void waitingClient();
}
