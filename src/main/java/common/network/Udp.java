package common.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import common.Listener.NetworkListener;
import common.main.ErrorListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class Udp {
    protected final int portNumber;
    protected final NetworkListener listener;
    protected final ErrorListener errorListener;
    protected DatagramSocket socket;

    public Udp(int portNumber, NetworkListener netListener, ErrorListener errorCallback) {
        this.portNumber = portNumber;
        this.listener = netListener;
        this.errorListener = errorCallback;
    }

    protected void makeSocket() throws SocketException {
        try {
            socket = new DatagramSocket(portNumber);
        } catch (SocketException e) {
            errorListener.happenError("ソケット生成に失敗しました");
            throw e;
        }
    }

    protected DatagramPacket makeSendPacket(byte[] sendData, InetAddress opponentAddress) {
        return new DatagramPacket(
                sendData,
                sendData.length,
                opponentAddress,
                portNumber);
    }

    protected void send(DatagramPacket sendPacket) throws IOException {
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            errorListener.happenError("メッセージの送信に失敗しました");
            throw e;
        }
        System.out.println("探索送信");
    }

    protected byte[] makeMassageData(String msg) {
        return msg.getBytes();
    }

    protected byte[] makeReceiveBuffer() {
        return new byte[1024];
    }

    protected DatagramPacket makeReceivePacket(byte[] receiveBuffer) {
        return new DatagramPacket(receiveBuffer, receiveBuffer.length);
    }

    protected void receive(DatagramPacket receivePacket) throws IOException {
    }

    public void close() {
        if (socket != null)
            socket.close();
    }

    protected String convertReceivePacketToString(DatagramPacket packet) {
		String msg = new String(packet.getData(), 0, packet.getLength());
		System.out.println("受信: " + msg);
		return msg;
	}

	protected Boolean checkConnectMassage(String password,String msg) {
		return msg.equals(password);
	}

}
