package network.contents.sever;

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

public class TcpServer {
	private int portNumber;
	private BufferedReader in;
	private PrintWriter out;
	private ObjectMapper mapper = new ObjectMapper();
	private Socket socket = null;
	private ServerSocket server;

	private NetworkListener listener;

	public TcpServer(int portNumber, NetworkListener listener) {
		this.portNumber = portNumber;
		this.listener = listener;
	}

	public void makeServer() {
		server = null;
		try {
			server = new ServerSocket(portNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("接続待機中...");

		socket = null;
		try {
			socket = server.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("接続されました");

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
