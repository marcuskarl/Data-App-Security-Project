package Client_Test;

import java.io.IOException;

import SocketEncryption.SocketEncryption;

public class Client_Test {

	public static void main(String[] args) {
		
		SocketEncryption socket = new SocketEncryption();
		
		try {
			socket.SwapPublicKeys();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}