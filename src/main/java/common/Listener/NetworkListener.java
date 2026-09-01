package common.Listener;

import common.Json.InputConvertedData;

public interface NetworkListener {

	void waitingClient();

	void checkNofitication();

	void timeout();

	void successConnect();

    void receiveData(InputConvertedData data);

    void receiveError();
}
