package client.keyboard.listener;

import common.eventType.DataType;
import common.keyboard.listener.KeyboardListener;

public interface ClientKeyboardListener extends KeyboardListener{
    public void happenKeyboardEvent(DataType dataType, Object data);
}
