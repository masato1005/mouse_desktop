package common.Listener.implemented;

import common.Event.NetworkEvent;
import common.EventType.NetworkEventType;
import common.Json.InputConvertedData;
import common.Listener.NetworkEventListener;
import common.Listener.NetworkListener;

public class ImplementedNetworkListener implements NetworkListener {
    private NetworkEventListener eventListener;

    public void setListener(NetworkEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void waitingClient() {
        eventListener.onNetworkEvent(new NetworkEvent(NetworkEventType.WAITING_CLIENT, null));
    }

    @Override
    public void checkNofitication() {
        eventListener.onNetworkEvent(new NetworkEvent(NetworkEventType.TIMEOUT, null));
    }

    @Override
    public void checkTimeout() {
        eventListener.onNetworkEvent(new NetworkEvent(NetworkEventType.TIMEOUT, null));
    }

    @Override
    public void successConnect() {
        System.out.println("success");
        eventListener.onNetworkEvent(new NetworkEvent(NetworkEventType.SUCCESSCONNECT, null));
    }

    @Override
    public void receiveData(InputConvertedData data) {
        eventListener.onNetworkEvent(new NetworkEvent(NetworkEventType.RECEIVEDATA, data));
    }

    @Override
    public void receiveError(){
        eventListener.onNetworkEvent(new NetworkEvent(NetworkEventType.ERROR, null));
    }
}
