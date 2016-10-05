package SocketEncryption;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteArrayConversions {
	public static byte [] LongToByteArray (long x) throws IOException {
		return ByteBuffer.allocate(8).putLong(x).array();
	}
	
	public static long ByteArrayToLong (byte [] x) {
		return ByteBuffer.wrap(x).getLong();
	}
	
	
}
