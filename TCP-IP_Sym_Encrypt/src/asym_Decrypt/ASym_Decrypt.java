package asym_Decrypt;

import java.math.BigInteger;
import java.util.Random;

import SocketEncryption.ByteArrayConversions;
import SocketEncryption.KeyObject;

public class ASym_Decrypt {
	private BigInteger decryptKey;
	private BigInteger encryptKey;
	private BigInteger n = BigInteger.valueOf(1);
	private int PrimeNumberBitLength = 1024;
	
	public byte[] GetPublicKey () {
		KeyObject Key = new KeyObject();
		Key.SetEncryptValue(encryptKey);
		Key.SetNValue(n);
		
		return ByteArrayConversions.AnyTypeToByteArray(Key);
	}
	
	public byte [] Decrypt (BigInteger c) {
		// Decrypts c and converts decrypted message to byte array
		byte [] m = c.modPow(decryptKey, n).toByteArray();
		
		// Creates byte array that is 1 byte smaller than decrypted array
		byte [] temp = new byte[m.length - 1];
		
		// Copies decrypted array m to temp array but skipping the first bte of m
		// which was byte stuffed on the encryption side
		for (int i = 0; i < temp.length; i++)
			temp[i] = m[i + 1];
		
		// Returns decrypted message without the byte stuffed at the beginning of the cipher array
		return temp;
	}
	
	public ASym_Decrypt() {
		createKeys();
	}
	
	private void createKeys() {
		Random rand = new Random( System.currentTimeMillis() );
		BigInteger prime1 = new BigInteger(PrimeNumberBitLength, 20, rand);
		BigInteger prime2 = new BigInteger(PrimeNumberBitLength, 20, rand);
		BigInteger z = BigInteger.valueOf(1);
		
		// n = prime1
		n = n.multiply(prime1);
		// n = prime1 * prime2
		n = n.multiply(prime2);
		
		// z = prime1 - 1
		z = z.multiply( prime1.subtract( BigInteger.valueOf(1) ) );
		// z = (prime1 - 1) * (prime2 - 1)
		z = z.multiply( prime2.subtract( BigInteger.valueOf(1)) );

		encryptKey = new BigInteger(PrimeNumberBitLength * 2, 20, rand);
		decryptKey = encryptKey.modInverse(z);
	}
}