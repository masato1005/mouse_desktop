package network.contents.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Json.InputConvertedData;
import Listener.NetworkListener;
import gui.contents.ErrorExitGui;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class TcpServer {
	private final int portNumber;
	private BufferedReader in;
	private PrintWriter out;
	private final ObjectMapper mapper = new ObjectMapper();
	private Socket socket = null;
	private ServerSocket server;

	private final NetworkListener listener;

	public TcpServer(int portNumber, NetworkListener listener) {
		this.portNumber = portNumber;
		this.listener = listener;
	}

	public void makeServer() {
		server = null;
		try {
			server = new ServerSocket(portNumber);
		} catch (IOException e) {
			new ErrorExitGui("ソケットの生成で不具合が発生しました");
		}
		System.out.println("接続待機中...");

		socket = null;
		try {
			socket = server.accept();
		} catch (IOException e) {
			new ErrorExitGui("接続が中断されました");
		}
		System.out.println("接続されました");

		try {
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			new ErrorExitGui("ソケットの受信機能の起動に失敗しました");
		}

		try {
			out = new PrintWriter(
					socket.getOutputStream(), true);
		} catch (IOException e) {
			new ErrorExitGui("ソケットの送信機能の起動に失敗しました");
		}
	}

	public void loop() {
		receive();
	}

	private void receive() {
		try {
			String Json = in.readLine();
			if (Json != null)
				convertJsonToData(Json);
			
		}catch(IOException e) {
			new ErrorExitGui("メッセージの受信に失敗しました");
		}
	}

	private void convertJsonToData(String Json) throws JsonMappingException, JsonProcessingException {
		listener.receiveData(mapper.readValue(Json, InputConvertedData.class));
	}

	public void send(String msg) {
		out.println(msg);
	}

    public void close() {
		try {
			server.close();
			socket.close();
			in.close();
			out.close();
		} catch (IOException e) {
			new ErrorExitGui("終了処理で失敗しました");
		}
	}
}
