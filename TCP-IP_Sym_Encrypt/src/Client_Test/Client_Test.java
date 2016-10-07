package Client_Test;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Random;

import SocketEncryption.SocketEncryption;
import Sym_Decrypt.Sym_Decrypt;
import Sym_Encrypt.Sym_Encrypt;

public class Client_Test {

	public static void main(String[] args) {
		
		SocketEncryption socket = new SocketEncryption();
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		/*
		boolean problems = false;
		
		
		byte [] t = new byte[4];
		
		t[0] = (byte) 127;
		t[1] = (byte) 255;
		t[2] = (byte) 255;
		t[3] = (byte) 255;
		
		System.out.println("Test byte array: " + new BigInteger(t));
		
		//for (int j = 0; j < 999; j++) {
			Sym_Decrypt x = new Sym_Decrypt();
			Sym_Encrypt y = new Sym_Encrypt();
			
			
			
			y.ReceiveKey(x.GetPublicKey());
			
			Random rand = new Random();
			
			byte [] m = new byte[255];
			
			for (int j = 0; j < 1; j++) {
				
				for (int i = 0; i < m.length; i++)
					//m[i] = (byte) 255;
					m[i] = (byte) rand.nextInt(255);
				
				BigInteger c = y.Encrypt(m);
				//System.out.println("c bit length: " + c.bitLength() + ", m bit length: " + new BigInteger(m).bitLength());
				byte [] testM = x.Decrypt(c);
				
				if (m.length != testM.length) {
					System.out.println("Decrypted message length does not match original");
					System.out.println("m:     " + new BigInteger(m) );
					System.out.println("testM: " + new BigInteger(testM) );
					
					
					for (int k = 0; k < 100; k++) {
						for (int i = 0; i < m.length; i++)
							m[i] = (byte) rand.nextInt(256);
						
						c = y.Encrypt(m);
						//System.out.println("c bit length: " + c.bitLength() + ", m bit length: " + new BigInteger(m).bitLength());
						testM = x.Decrypt(c);
						
						if (m.length != testM.length) {
							System.out.println("Decrypted message length does not match original");
							System.out.println("m:     " + new BigInteger(m) );
							System.out.println("testM: " + new BigInteger(testM) );
						}
					}
					return;
					
				}
				
				for (int i = 0; i < m.length && i < testM.length; i++) {
					if (m[i] != testM[i]) {
						problems = true;
						//System.out.println("Difference found at element " + i);
					}
				}
				
				if (!problems)
					System.out.print(".");
		}
		
		System.out.println("\nProblems found: " + problems);
		
		*/
	}
	
}