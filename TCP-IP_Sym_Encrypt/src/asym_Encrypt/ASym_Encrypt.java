package asym_Encrypt;

import java.math.BigInteger;
import SocketEncryption.ByteArrayConversions;
import SocketEncryption.KeyObject;

public class ASym_Encrypt {
	private BigInteger OthersEncryptValue;
	private BigInteger OthersNValue;
	
	public BigInteger getOthersNValue() {
		return OthersNValue;
	}
	
	public boolean ReceiveKey (byte [] x) {
		if (x != null) {
			KeyObject Key = ByteArrayConversions.ByteArrayToAnyType(x);
			OthersEncryptValue = Key.getEncryptValue();
			OthersNValue = Key.getNValue();
			return true;
		}
		return false;
	}
	
	public BigInteger Encrypt(byte [] data) {
		// Creates new byte array to stuff first element with a value 0 < x <  128
		// This will prevent the BigInteger value from ever being negative (i.e. MSB = 1)
		// after converting to two's complement
		// If the BigInteger value is negative the message encryption and decryption 
		// has errors thrown due to negative values in the modulus operation
		//byte [] temp = new byte [data.length + 1];
		//temp[0] = 1;
		//for (int i = 1; i < temp.length; i++)
		//	temp[i] = data[i - 1];
		
		// Converts the byte array to a BigInteger value
		BigInteger m = new BigInteger(data);
		
		return m.modPow(OthersEncryptValue, OthersNValue);
	}
	
}