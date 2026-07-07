package Listener.implemented;

import Handler.NetworkHandler;
import Json.InputConvertedData;
import Listener.NetworkListener;

public class ImplementedNetworkListener implements NetworkListener {
    private NetworkHandler handler;

    public void setListener(NetworkHandler handler) {
        this.handler = handler;
    }

    @Override
    public void checkNofitication() {
        handler.timeout();
    }

    @Override
    public void searchNofitication() {
        handler.serch();
    }

    @Override
    public void checkStopSever() {
        handler.choiseSC();

    }

    @Override
    public void checkTimeout() {
        handler.timeout();
    }

    @Override
    public void successConnect() {
        System.out.println("success");
        handler.successConnect();
    }

    @Override
    public void receiveData(InputConvertedData data) {
        handler.receiveData(data);
    }
}
