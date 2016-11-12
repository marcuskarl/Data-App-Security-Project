package asym_Encrypt;

import java.math.BigInteger;
import SocketEncryption.ByteArrayConversions;
import SocketEncryption.KeyObject;

public class ASym_Encrypt {
	private BigInteger OthersEncryptValue;
	private BigInteger OthersNValue;
	
	public BigInteger getOthersNValue () {
		return OthersNValue;
	}
	
	public  boolean ReceiveKey (byte [] x) {
		if (x != null) {
			KeyObject Key = ByteArrayConversions.ByteArrayToKeyObject(x);
			
			OthersEncryptValue = Key.getEncryptValue();
			OthersNValue = Key.getNValue();
			
			System.out.println(Thread.currentThread().getId() + ":Others n value: " + OthersNValue);
			System.out.println(Thread.currentThread().getId() + ":Others e value: " + OthersEncryptValue);
			
			return true;
		}
		return false;
	}
	
	public byte [] Encrypt(byte [] data) {
		
		byte [] mData = new byte [data.length + 1];
		
		mData[0] = (byte) 1;
		
		for (int i = 1; i < mData.length; i++)
			mData[i] = data[i - 1];
		
		BigInteger m = new BigInteger(mData);
		// Converts the byte array to a BigInteger value and encrypts then returns the value as a byte array
		BigInteger c = m.modPow(OthersEncryptValue, OthersNValue);
		return c.toByteArray();
	}
}