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
	
	public static <T> byte [] AnyTypeToByteArray (T x) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(x);
			oos.close();
			
			return bos.toByteArray();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static <T> byte [] AnyTypeToByteArray (T [] x) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(x);
			oos.close();
			
			return bos.toByteArray();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T ByteArrayToAnyType (byte [] x) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(x);
	        ObjectInputStream ois;
			ois = new ObjectInputStream(bis);
			bis.close();
			
			return (T) ois.readObject();
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
