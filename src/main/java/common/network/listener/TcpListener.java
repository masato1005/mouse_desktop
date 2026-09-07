package common.network.listener;

import common.json.InputConvertedData;

public interface TcpListener {
    void receiveData(InputConvertedData data);
    void receiveError();
}
