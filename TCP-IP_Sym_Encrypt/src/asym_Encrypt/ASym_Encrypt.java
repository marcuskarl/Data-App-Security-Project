package asym_Encrypt;

import java.math.BigInteger;
import SocketEncryption.KeyObject;

public class ASym_Encrypt {
	private BigInteger OthersEncryptValue;
	private BigInteger OthersNValue;
	
	public int getOthersNBitLength () {
		return OthersNValue.bitLength();
	}
	
	public  boolean ReceiveKey (KeyObject Key) {
		if (Key != null) {
			OthersEncryptValue = Key.getEncryptValue();
			OthersNValue = Key.getNValue();
			
			return true;
		}
		return false;
	}
	
	public BigInteger Encrypt(byte [] data) {
		
		byte [] mData = new byte [data.length + 1];
		
		mData[0] = (byte) 1;
		
		for (int i = 1; i < mData.length; i++)
			mData[i] = data[i - 1];
		
		BigInteger m = new BigInteger(mData);
		// Converts the byte array to a BigInteger value and encrypts then returns the value as a byte array
		BigInteger c = m.modPow(OthersEncryptValue, OthersNValue);
		return c;
	}
	
	public BigInteger Encrypt(byte data) {
		
		byte [] mData = new byte [2];
		
		mData[0] = (byte) 1;
		mData[1] = data;
		
		BigInteger m = new BigInteger(mData);
		// Converts the byte array to a BigInteger value and encrypts then returns the value as a byte array
		BigInteger c = m.modPow(OthersEncryptValue, OthersNValue);
		return c;
	}
}