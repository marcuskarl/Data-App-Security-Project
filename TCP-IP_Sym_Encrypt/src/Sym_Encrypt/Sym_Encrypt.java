package Sym_Encrypt;

import java.math.BigInteger;
import java.util.Random;

public class Sym_Encrypt {
	private BigInteger OthersEncryptValue;
	private BigInteger OthersNValue;
	
	
	
	public boolean ReceiveKey (BigInteger[] x) {
		if (x != null) {
			OthersEncryptValue = x[0];
			OthersNValue = x[1];
			
			return true;
		}
		else
			return false;
	}
	
	public BigInteger Encrypt(byte [] data) {
		Random rand = new Random();
		// Creates new byte array to stuff first element with a value less than 128
		// This will prevent the BigInteger value from ever being negative (i.e. MSB = 1)
		// By ensuring the first bit it always 0, if the BigInteger value is negative
		// The message encryption and decryption is thrown off to negative value
		byte [] temp = new byte [data.length + 1];
		temp[0] = (byte) rand.nextInt(128);
		for (int i = 1; i < temp.length; i++)
			temp[i] = data[i - 1];
		
		// Converts the byte array to a BigInteger value
		BigInteger m = new BigInteger(temp);
		
		return m.modPow(OthersEncryptValue, OthersNValue);
	}
	
}
