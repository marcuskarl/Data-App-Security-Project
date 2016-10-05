package Sym_Decrypt;

import java.io.IOException;
import java.util.Random;
import org.apache.commons.math3.primes.Primes;

import SocketEncryption.ByteArrayConversions;


public class Sym_Decrypt {
	private byte [] MyPublicKey = new byte [16];
	private long decryptKey = 3;
	private long encryptKey = 101;
	private long n = 0;
	
	public byte [] GetPublicKey () {
		return MyPublicKey;
	}
	
	public Sym_Decrypt() {
		System.out.print("Generating key");
		
		Random rand = new Random();
		long prime1 = 0;
		long prime2 = 0;
		long z = 0;
		
		prime1 = Primes.nextPrime( rand.nextInt( 5000 ) + 1000 ); // Returns a random prime greater than 1000
		prime2 = Primes.nextPrime( rand.nextInt( 1000 ) + 8000 ); // Returns a random prime greater than 8000
		
		n = prime1 * prime2;
		z = (prime1 - 1) * (prime2 - 1);
		
		findKeys(z, rand);
		
		byte [] eKey;
		byte [] nKey;
		
		try {
			eKey = ByteArrayConversions.LongToByteArray(encryptKey);
			nKey = ByteArrayConversions.LongToByteArray(n);
			
			for (int i = 0; i < 8; i ++)
				MyPublicKey[i] = eKey[i];
			
			for (int i = 0; i < 8; i ++)
				MyPublicKey[i + 8] = nKey[i];
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\nPrime 1:" + prime1);
		System.out.println("Prime 2:" + prime2);
		System.out.println("n:" + n);
		System.out.println("z:" + z);
		System.out.println("dKey:" + decryptKey);
		System.out.println("eKey:" + encryptKey);
		
		System.out.print("Byte array is ");

		for (int i = 0; i < 16; i ++)
			System.out.print(MyPublicKey[i] + "  ");
		
		byte [] temp = new byte[8];
		
		for (int i = 0; i < 8; i ++)
			temp[i] = MyPublicKey[i];
		
		System.out.println("\n" + ByteArrayConversions.ByteArrayToLong(temp));
		
		for (int i = 0; i < 8; i ++)
			temp[i] = MyPublicKey[i + 8];
		
		System.out.println(ByteArrayConversions.ByteArrayToLong(temp));
		
		
	}
	
	private void findKeys (long z, Random rand) {
		System.out.print(".");
		
		// Finds a prime for the encrypt key that is 100 < encryptKey < z
		do {
			encryptKey = Primes.nextPrime( 100 + rand.nextInt( (int)Math.sqrt( z ) ) );
		} while ( encryptKey % z == 0 );
		
		decryptKey = 101;
		
		while ( !( (encryptKey * decryptKey) % z == 1) ) {
			//System.out.println(decryptKey);
			decryptKey = Primes.nextPrime( (int) decryptKey + 1 );
			
			// If decryptKey prime value is greater than z, 
			// a new value will be found for the encryption key and the process starts over
			if (decryptKey > z)
				findKeys(z, rand);
		}
		
	}
	
}