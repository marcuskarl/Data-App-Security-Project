package Sym_Decrypt;

import java.math.BigInteger;
import java.util.Random;

import SocketEncryption.ByteArrayConversions;
import SocketEncryption.KeyObject;

public class Sym_Decrypt {
	private BigInteger decryptKey;
	private BigInteger encryptKey;
	private BigInteger n = BigInteger.valueOf(1);
	private int PrimeNumberBitLength = 1024;
	
	public byte[] GetPublicKey () {
		KeyObject Key = new KeyObject();
		Key.SetEncryptValue(encryptKey);
		Key.SetNValue(n);
		
		return ByteArrayConversions.KeyObjectToByteArray(Key);
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
	
	public Sym_Decrypt() {
		System.out.print("Generating key...");
		createKeys();
		System.out.print("done.\n");
	}
	
	private void createKeys() {
		Random rand = new Random();
		BigInteger prime1 = BigInteger.probablePrime(PrimeNumberBitLength, rand);
		BigInteger prime2 = BigInteger.probablePrime(PrimeNumberBitLength, rand);
		BigInteger z = BigInteger.valueOf(1);
		
		// n = prime1
		n = n.multiply(prime1);
		// n = prime1 * prime2
		n = n.multiply(prime2);
		
		// z = prime1 - 1
		z = z.multiply( prime1.subtract( BigInteger.valueOf(1)) );
		// n = (prime1 - 1) * (prime2 - 1)
		z = z.multiply( prime2.subtract( BigInteger.valueOf(1)) );
		
		// Finds a prime for encryptKey that is z/2 < encryptKey < z
		encryptKey = z.divide( BigInteger.valueOf(2) ).nextProbablePrime();
		
		decryptKey = encryptKey.modInverse(z);
		
		/*
		System.out.println("\nPrime 1:" + prime1.bitLength() );
		System.out.println("Prime 2:" + prime2.bitLength() );
		System.out.println("n:" + n.bitLength() );
		System.out.println("z:" + z.bitLength() );
		System.out.println("dKey:" + decryptKey.bitLength() );
		System.out.println("eKey:" + encryptKey.bitLength() );
		*/
	}
}