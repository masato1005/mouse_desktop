package Listener;

import Json.InputConvertedData;

public interface NetworkListener {

	void searchNofitication();

	void checkNofitication();

	void checkStopSever();

	void checkTimeout();

	void successConnect();

    void receiveData(InputConvertedData data);

    void receiveError();
}
