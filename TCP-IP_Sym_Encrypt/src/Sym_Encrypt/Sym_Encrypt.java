package Sym_Encrypt;

import java.math.BigInteger;

import SocketEncryption.ByteArrayConversions;

public class Sym_Encrypt {
	private long OthersEncryptValue = 0;
	private long OthersNValue = 0;
	
	
	
	public boolean ReceiveKey (byte [] PK) {
		if (PK != null) {
			byte [] temp = new byte[8];
			
			for (int i = 0; i < temp.length; i ++)
				temp[i] = PK[i];
			
			OthersEncryptValue = ByteArrayConversions.ByteArrayToLong(temp);
			
			for (int i = 0; i < temp.length; i ++)
				temp[i] = PK[i + 8];
			
			OthersNValue = ByteArrayConversions.ByteArrayToLong(temp);
			
			return true;
		}
		else
			return false;
	}
	
	public BigInteger Encrypt(byte [] data) {
		BigInteger m = new BigInteger(data);
		BigInteger e = BigInteger.valueOf(OthersEncryptValue);
		BigInteger n = BigInteger.valueOf(OthersNValue);;
		
		System.out.println("m is: " + m);
				
		System.out.println("Encrypt value of: " + m.modPow(e, n));
		
		return m.modPow(e, n);
	}
	
}
