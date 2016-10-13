package Client_Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import SocketEncryption.ByteArrayConversions;
import SocketEncryption.EncryptionObject;
import SocketEncryption.SocketEncryption;

public class Client_Test {

	public static void main(String[] args) {
		
		SocketEncryption socket = new SocketEncryption();
		
		byte [] y = new byte [135];
		
		for (int i = 0; i < y.length; i++)
			y[i] = (byte) 5;
		
		try {
			socket.SwapPublicKeys();
			// socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EncryptionObject x = new EncryptionObject();
		
		x.setMsg(y);
		x.setMaxSegments(1);
		x.setKey(false);
		x.setSegmentNum(1);
		x.setTotalByteSizeOfAllSegments(y.length);
		
		BigInteger c = socket.Encrypt.Encrypt( ByteArrayConversions.AnyTypeToByteArray(x) );
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			 
			oos.writeObject(x);
			oos.close();
			System.out.println( baos.size() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}