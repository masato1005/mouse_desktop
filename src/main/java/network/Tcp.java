package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Json.InputConvertedData;
import Listener.NetworkListener;
import gui.contents.ErrorExitGui;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class Tcp {
    protected final int portNumber;
    protected BufferedReader in;
    protected PrintWriter out;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected Socket socket = null;
    protected final NetworkListener listener;

    public Tcp(int portNumber, NetworkListener listener) {
        this.portNumber = portNumber;
        this.listener = listener;
    }

    protected void receive() {
        try {
            String json = in.readLine();
            if (json != null)
                System.out.println("受信JSON: " + json);
            convertJsonToData(json);
        } catch (IOException e) {
            new ErrorExitGui("Jsonの変換に失敗しました");
            listener.receiveError();
        }
    }

    private void convertJsonToData(String Json) throws JsonMappingException, JsonProcessingException {
        InputConvertedData data = mapper.readValue(Json, InputConvertedData.class);
        listener.receiveData(data);
    }

    public void send(String msg) {
        out.println(msg);
    }

    public void close() {
        try {
            if (socket != null)
                socket.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            new ErrorExitGui("終了処理に失敗しました");
        }
    }
}
