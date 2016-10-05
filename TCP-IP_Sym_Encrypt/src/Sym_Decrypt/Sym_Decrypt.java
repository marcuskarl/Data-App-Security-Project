package Sym_Decrypt;

import java.util.Random;
import org.apache.commons.math3.primes.Primes;


public class Sym_Decrypt {
	private byte [] MyPublicKey = null;
	private long decryptKey = 11;
	private long n = 0;
	
	public byte [] GetPublicKey () {
		return MyPublicKey;
	}
	
	public Sym_Decrypt() {
		Random rand = new Random();
		long prime1 = 0;
		long prime2 = 0;
		long z = 0;
		long encryptKey = 4;
		
		prime1 = Primes.nextPrime( rand.nextInt( 5000 ) + 1000 ); // Returns a random prime greater than 1000
		prime2 = Primes.nextPrime( rand.nextInt( 1000 ) + 8000 ); // Returns a random prime greater than 8000
		
		n = prime1 * prime2;
		z = (prime1 - 1) * (prime2 - 1);
		
		// Finds a prime for the encrypt key that is 100 < encryptKey < z
		while ( !Primes.isPrime( (int) encryptKey ) && (encryptKey < z) && (encryptKey % z != 0) ) {
			encryptKey = Primes.nextPrime( 100 + rand.nextInt( (int) z ) );
		}
		
		while ( !( (encryptKey * decryptKey) % z == 1) )
			decryptKey = Primes.nextPrime( (int) decryptKey + 1 );
		
		
		decryptKey = Primes.nextPrime( rand.nextInt( 1000 ) + 100 ); // Returns random prime greater than 100
		
	}
	
}