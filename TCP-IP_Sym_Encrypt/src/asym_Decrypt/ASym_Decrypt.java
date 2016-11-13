package asym_Decrypt;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import SocketEncryption.KeyObject;

public class ASym_Decrypt {
	private BigInteger decryptKey;
	private BigInteger encryptKey;
	private BigInteger n = BigInteger.valueOf(1);
	
	public ASym_Decrypt(int x) throws Exception {
		if (x < 128)
			throw new Exception("Key bit length is less than 128, given " + x);
		
		int PrimeNumberBitLength = x/2;
		
		Random rand = new Random();
		BigInteger prime1 = new BigInteger(PrimeNumberBitLength, 20, rand);
		BigInteger prime2 = new BigInteger(PrimeNumberBitLength, 20, rand);
		BigInteger z = BigInteger.valueOf(1);
		
		// n = prime1 * prime2
		n = prime1.multiply(prime2);
		// z = (prime1 - 1) * (prime2 - 1)
		z = prime1.subtract( BigInteger.valueOf(1) ).multiply( prime2.subtract( BigInteger.valueOf(1) ) );
		
		encryptKey = new BigInteger(PrimeNumberBitLength, 20, rand);
		decryptKey = encryptKey.modInverse(z);
	}
	
	public KeyObject GetPublicKey () {
		KeyObject Key = new KeyObject();
		Key.SetEncryptValue(encryptKey);
		Key.SetNValue(n);
		
		return Key;
	}
	
	public int getNBitLength () {
		return n.bitLength();
	}
	
	public byte [] Decrypt (BigInteger c) {
		// Decrypts c and converts decrypted message to byte array
		BigInteger m = c.modPow(decryptKey, n);
		
		byte [] mData = m.toByteArray();
		
		return Arrays.copyOfRange(mData, 1, mData.length);
	}
	
	public byte DecryptSingleByte (BigInteger c) {
		// Decrypts c and converts decrypted message to byte array
		BigInteger m = c.modPow(decryptKey, n);
		
		byte [] mData = m.toByteArray();
		
		return mData[1];
	}
}