package client.network.listener;

import common.network.listener.UdpListener;

public interface ClientUdpListener extends UdpListener {
    void timeout();

    void checkNofitication();
}
