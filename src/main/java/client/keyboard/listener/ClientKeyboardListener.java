package client.keyboard.listener;

import common.eventtype.DataType;
import common.keyboard.listener.KeyboardListener;

public interface ClientKeyboardListener extends KeyboardListener{
    public void happenKeyboardEvent(DataType dataType, Object data);
}
