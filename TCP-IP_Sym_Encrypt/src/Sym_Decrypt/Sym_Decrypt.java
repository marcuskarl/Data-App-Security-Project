package Sym_Decrypt;

import java.util.Random;
import org.apache.commons.math3.primes.Primes;


public class Sym_Decrypt {
	private byte [] MyPublicKey = null;
	private long decryptKey = 3;
	private long encryptKey = 101;
	private long n = 0;
	
	public byte [] GetPublicKey () {
		return MyPublicKey;
	}
	
	public Sym_Decrypt() {
		Random rand = new Random();
		long prime1 = 0;
		long prime2 = 0;
		long z = 0;
		
		prime1 = Primes.nextPrime( rand.nextInt( 5000 ) + 1000 ); // Returns a random prime greater than 1000
		prime2 = Primes.nextPrime( rand.nextInt( 1000 ) + 8000 ); // Returns a random prime greater than 8000
		
		n = prime1 * prime2;
		z = (prime1 - 1) * (prime2 - 1);
		
		findKeys(z, rand);
		
		System.out.println(prime1);
		System.out.println(prime2);
		System.out.println(n);
		System.out.println(z);
		System.out.println(decryptKey);
		System.out.println(encryptKey);
	}
	
	private void findKeys (long z, Random rand) {
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