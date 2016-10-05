package SocketEncryption;

import java.io.Serializable;

public class EncryptionObject implements Serializable {
	private static final long serialVersionUID = 01L;
	private boolean isKey = false;
	private byte [] msg = null;
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
	
	public byte [] getMsg () {
		return msg;
	}
	
	public void setMsg (byte [] x) {
		msg = x;
	}
}