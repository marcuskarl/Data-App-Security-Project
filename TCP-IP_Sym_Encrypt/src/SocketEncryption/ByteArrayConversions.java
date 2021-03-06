package SocketEncryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class ByteArrayConversions {
	public static byte [] IntToByteArray (int x) throws IOException {
		return ByteBuffer.allocate(Integer.BYTES).putInt(x).array();
	}
	
	public static int ByteArrayToInt (byte [] x) {
		return ByteBuffer.wrap(x).getInt();
	}
	
	public static KeyObject ByteArrayToKeyObject (byte [] x) {
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(x);
			ois = new ObjectInputStream(bis);
	        
			return (KeyObject) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (bis != null)
					bis.close();
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
    }
	
	public static byte [] KeyObjectToByteArray (KeyObject x) {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(x);
			
			return bos.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (bos != null)
					bos.close();
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static byte [] ObjectToByteArray (Object x) throws IOException {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(x);
			oos.flush();
			
			return bos.toByteArray();
		} finally {
			if (bos != null)
				bos.close();
			if (oos != null)
				oos.close();
		}
	}
	
	public static Object ByteArrayToObject (byte [] x) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(x);
			ois = new ObjectInputStream(bis);
	        
			return ois.readObject();
		} finally {
			if (bis != null)
				bis.close();
			if (ois != null)
				ois.close();
		}
    }
}
