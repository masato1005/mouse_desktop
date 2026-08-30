package Listener;

import Json.InputConvertedData;

public interface NetworkListener {

	void waitingClient();

	void checkNofitication();

	void checkTimeout();

	void successConnect();

    void receiveData(InputConvertedData data);

    void receiveError();
}
