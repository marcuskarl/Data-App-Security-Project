package SocketEncryption;

import java.io.Serializable;
import java.math.BigInteger;

public class EncryptionObject implements Serializable {
	private static final long serialVersionUID = 01L;
	private boolean isKey = false;
	private BigInteger [] msg = null;
	private int [] segments = new int[3];
	
	public void setSegmentNum (int x) {
		segments[0] = x;
	}
	
	public void setMaxSegments (int x){
		segments[1] = x;
	}
	
	public void setCurrentSegmentLength (int x) {
		segments[2] = x;
	}
	
	public boolean getKey () {
		return isKey;
	}
	
	public void setKey (boolean x) {
		isKey = x;
	}
	
	public BigInteger [] getMsg () {
		return msg;
	}
	
	public void setMsg (BigInteger [] x) {
		msg = x;
	}
}