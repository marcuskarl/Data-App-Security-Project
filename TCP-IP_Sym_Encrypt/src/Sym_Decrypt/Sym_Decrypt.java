package Sym_Decrypt;

import java.util.Random;

public class Sym_Decrypt {
	private byte [] MyPublicKey = null;
	
	public byte [] GetPublicKey () {
		return MyPublicKey;
	}
	
	private Random rand = new Random();
	private long prime1 = 0;
	private long prime2 = 0;
	private long n = 0;
	private long z = 0;
	
	
}