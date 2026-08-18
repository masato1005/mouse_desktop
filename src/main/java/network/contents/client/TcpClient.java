package network.contents.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Json.InputConvertedData;
import Listener.NetworkListener;
import gui.contents.ErrorExitGUI;

public class TcpClient {
	private final String serverIP;
	private final int portNumber;
	private BufferedReader in;
	private PrintWriter out;
	private final ObjectMapper mapper = new ObjectMapper();
	private Socket socket = null;

	private final NetworkListener listener;

	public TcpClient(String serverIP, int portNumber, NetworkListener listener) {
		this.serverIP = serverIP;
		this.portNumber = portNumber;
		this.listener = listener;
	}

	public void connect() {
		socket = null;
		try {
			socket = new Socket(serverIP, portNumber);
		} catch (IOException e) {
			new ErrorExitGUI("ソケットの生成で不具合が発生しました");
		}
		System.out.println("接続完了");

		try {
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			new ErrorExitGUI("受信機能の起動に失敗しました");
		}

		try {
			out = new PrintWriter(
					socket.getOutputStream(), true);
		} catch (IOException e) {
			new ErrorExitGUI("送信機能の起動に失敗しました");
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
		} catch (IOException e) {
			new ErrorExitGUI("Jsonの変換に失敗しました");
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
			socket.close();
			in.close();
			out.close();
		} catch (IOException e) {
			new ErrorExitGUI("終了処理に失敗しました");
		}
	}
}
