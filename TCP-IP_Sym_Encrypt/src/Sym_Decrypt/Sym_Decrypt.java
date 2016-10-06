package Sym_Decrypt;

import java.math.BigInteger;
import java.util.Random;

public class Sym_Decrypt {
	private BigInteger [] MyPublicKey = new BigInteger[2];
	private BigInteger decryptKey;
	private BigInteger encryptKey;
	private BigInteger n = BigInteger.valueOf(1);
	private int PrimeNumberBitLength = 1024;
	
	public BigInteger [] GetPublicKey () {
		return MyPublicKey;
	}
	
	public byte [] Decrypt (BigInteger c) {
		byte [] m = c.modPow(decryptKey, n).toByteArray();
		byte [] temp = new byte[m.length - 1];
		
		for (int i = 0; i < temp.length; i++)
			temp[i] = m[i + 1];
		
		return temp;
	}
	
	public Sym_Decrypt() {
		System.out.print("Generating key...");
		
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
		
		MyPublicKey[0] = encryptKey;
		MyPublicKey[1] = n;
		
		System.out.print("done.\n");
		
		/*
		System.out.println("\nPrime 1:" + prime1);
		System.out.println("Prime 2:" + prime2);
		System.out.println("n:" + n);
		System.out.println("z:" + z);
		System.out.println("dKey:" + decryptKey);
		System.out.println("eKey:" + encryptKey);		
		System.out.println("n is:" + n);
		*/
	}
}