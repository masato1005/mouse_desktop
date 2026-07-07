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

public class TcpClient {
	private final String serverIP;
	private final int portNumber;
	private BufferedReader in;
	private PrintWriter out;
	private final ObjectMapper mapper = new ObjectMapper();

	private final NetworkListener listener;

	public TcpClient(String serverIP, int portNumber,NetworkListener listener) {
		this.serverIP = serverIP;
		this.portNumber = portNumber;
		this.listener = listener;
	}

	public void connect() {
		Socket socket = null;
		try {
			socket = new Socket(serverIP, portNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("接続完了");

		try {
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			out = new PrintWriter(
					socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loop() {
		receive();
	}

	private void receive() {
		try {
			String Json = in.readLine();
			if (Json != null)
				ConvertJsonToData(Json);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void ConvertJsonToData(String Json) throws JsonMappingException, JsonProcessingException {
		InputConvertedData data = mapper.readValue(Json, InputConvertedData.class);
		listener.receiveData(data);
	}

	public void send(String msg) {
		out.println(msg);
	}
}
