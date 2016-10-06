package Client_Test;

import java.math.BigInteger;
import java.util.Random;

import Sym_Decrypt.Sym_Decrypt;
import Sym_Encrypt.Sym_Encrypt;

public class Client_Test {

	public static void main(String[] args) {

		Sym_Decrypt x = new Sym_Decrypt();
		Sym_Encrypt y = new Sym_Encrypt();
		
		y.ReceiveKey(x.GetPublicKey());
		
		Random rand = new Random();
		
		byte [] m = new byte[512];
		
		for (int i = 0; i < m.length; i++) {
			m[i] = (byte) rand.nextInt(256);
		}
		
		System.out.println("Encrypting m..");
		BigInteger c = y.Encrypt(m);
		
		byte [] testM = x.Decrypt(c);
		
		for (int i = 0; i < m.length; i++) {
			if (m[i] != testM[i]) {
				System.out.println("Difference found at element " + i);
			}
		}
		
	}

}
