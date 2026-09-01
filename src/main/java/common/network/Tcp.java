package common.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Json.InputConvertedData;
import common.Listener.NetworkListener;
import common.main.ErrorListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class Tcp {
    protected final int portNumber;
    protected BufferedReader in;
    protected PrintWriter out;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected Socket socket = null;
    protected final NetworkListener listener;
    protected final ErrorListener errorListener;

    public Tcp(int portNumber, NetworkListener listener, ErrorListener errorListener) {
        this.portNumber = portNumber;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    public void receive() {
        try {
            while (true) {
                String json = in.readLine();
                if (json == null) {
                    errorListener.happenError("通信が切断されました");
                    listener.receiveError();
                    break;
                }
                System.out.println("受信JSON: " + json);
                convertJsonToData(json);
            }
        } catch (IOException e) {
            errorListener.happenError("Jsonの変換に失敗しました");
            listener.receiveError();
        }
    }

    private void convertJsonToData(String Json) throws JsonMappingException, JsonProcessingException {
        InputConvertedData data = mapper.readValue(Json, InputConvertedData.class);
        listener.receiveData(data);
    }

    public boolean send(String msg) {
        out.println(msg);
        if (out.checkError()) {
            errorListener.happenError("メッセージの送信に失敗しました");
            return false;
        }
        return true;
    }

    public void close() {
        boolean closeFailed = false;
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            closeFailed = true;
        }
        try {
            if (in != null)
                in.close();
        } catch (IOException e) {
            closeFailed = true;
        }
        if (out != null)
            out.close();
        if (closeFailed) {
            System.err.println("TCPの終了処理に失敗しました");
        }
    }

    protected boolean makeIn() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            errorListener.happenError("受信機能の起動に失敗しました");
            return false;
        }
    }

    protected boolean makeOut() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            errorListener.happenError("送信機能の起動に失敗しました");
            return false;
        }
    }
}
