package Client_Test;

import java.io.IOException;

import SocketEncryption.ByteArrayConversions;
import SocketEncryption.SocketEncryption;

public class Client_Test {

	public static void main(String[] args) {
		
		SocketEncryption socket = new SocketEncryption();
		
		long startingByte = 987654321;
		
		byte[] start;
		try {
			start = ByteArrayConversions.LongToByteArray(startingByte);

			System.out.println(start.length);
			
			System.out.println(ByteArrayConversions.ByteArrayToLong(start));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
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