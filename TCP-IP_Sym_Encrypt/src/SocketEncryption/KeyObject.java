package SocketEncryption;

import java.io.Serializable;
import java.math.BigInteger;

public class KeyObject implements Serializable {
	private static final long serialVersionUID = 01L;
	private BigInteger EncryptValue = null;
	private BigInteger NValue = null;
	
	public void SetEncryptValue(BigInteger x) {
		EncryptValue = x;
	}
	
	public void SetNValue(BigInteger x) {
		NValue = x;
	}
	
	public BigInteger getEncryptValue() {
		return EncryptValue;
	}
	
	public BigInteger getNValue() {
		return NValue;
	}
}