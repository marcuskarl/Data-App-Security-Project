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
		
		System.out.println("n bit length: " + n.bitLength() + ",e bit length: " + encryptKey.bitLength() + ",d bit length: " + decryptKey.bitLength());
	}
	
	private void createKeys() {
		Random rand = new Random( System.currentTimeMillis() );
		BigInteger prime1 = new BigInteger(PrimeNumberBitLength, 20, rand);
		BigInteger prime2 = new BigInteger(PrimeNumberBitLength, 20, rand);
		BigInteger z = BigInteger.valueOf(1);
		
		
		System.out.print("found primes...");
		
		// n = prime1
		n = n.multiply(prime1);
		// n = prime1 * prime2
		n = n.multiply(prime2);
		
		// z = prime1 - 1
		z = z.multiply( prime1.subtract( BigInteger.valueOf(1) ) );
		// z = (prime1 - 1) * (prime2 - 1)
		z = z.multiply( prime2.subtract( BigInteger.valueOf(1)) );
		
		
		/*
		// Finds a prime for encryptKey that is z/2 < encryptKey < z
		// Since encryptKey > z/2, encryptKey will never be a factor of z
		encryptKey = z.divide( BigInteger.valueOf(2) ).nextProbablePrime();
		
		// decryptKey is set as modular inverse of encryptKey mod z
		decryptKey = encryptKey.modInverse(z);
		*/
		
		
		encryptKey = new BigInteger(PrimeNumberBitLength * 2, 20, rand);
		
		decryptKey = encryptKey.modInverse(z);
		/*
		while ( !decryptKey.isProbablePrime(10) || (0 == encryptKey.mod(z).intValue() )	|| !encryptKey.isProbablePrime(10) ) {
			encryptKey = encryptKey.nextProbablePrime();
			decryptKey = encryptKey.modInverse(z);
			System.out.print(".");
		}
		*/
		
	}
}