package Listener;

import Json.InputConvertedData;

public interface NetworkListener {

	void serchNofitication();

	void checkNofitication();

	void checkStopSever();

	void checkTimeout();

	void successConnect();

    void receiveData(InputConvertedData data);
}
