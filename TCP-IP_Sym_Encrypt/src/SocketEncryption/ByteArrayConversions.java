package SocketEncryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class ByteArrayConversions {
	public static byte [] LongToByteArray (long x) throws IOException {
		return ByteBuffer.allocate(8).putLong(x).array();
	}
	
	public static long ByteArrayToLong (byte [] x) {
		return ByteBuffer.wrap(x).getLong();
	}
	
	public static byte [] KeyObjectToByteArray (KeyObject x) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(x);
			return bos.toByteArray();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static KeyObject ByteArrayToKeyObject (byte[] x) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(x);
	        ObjectInputStream ois;
			ois = new ObjectInputStream(bis);
			return (KeyObject)ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}
