package Handler;

import Event.NetworkEvent;
import EventType.NetworkEventType;
import Json.InputConvertedData;
import Listener.NetworkEventListener;

public class NetworkHandler {
	private NetworkEventListener listener;

	public void setEventListener(NetworkEventListener listener) {
		this.listener = listener;
	}

	public void timeout() {
		listener.onNetworkEvent(new NetworkEvent(NetworkEventType.TIMEOUT, null));
	}

	public void serch() {
		listener.onNetworkEvent(new NetworkEvent(NetworkEventType.SEARCH, null));
	}

	public void choiseSC() {
		listener.onNetworkEvent(new NetworkEvent(NetworkEventType.CHOICESC, null));
	}

	public void successConnect() {
		listener.onNetworkEvent(new NetworkEvent(NetworkEventType.SUCCESSCONNECT, null));

	}

    public void receiveData(InputConvertedData data) {
        listener.onNetworkEvent(new NetworkEvent(NetworkEventType.RECEIVEDATA, data));
    }

    public void receiveError() {
		listener.onNetworkEvent(new NetworkEvent(NetworkEventType.ERROR,null));
    }
}
