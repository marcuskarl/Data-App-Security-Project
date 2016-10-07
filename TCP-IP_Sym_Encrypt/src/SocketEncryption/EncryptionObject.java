package SocketEncryption;

import java.io.Serializable;

public class EncryptionObject implements Serializable {
	private static final long serialVersionUID = 01L;
	private boolean isKey = false;
	private byte [] msg = null;
	private int [] segments = new int[2];
	private long totalByteSizeOfAllSegments = 0;
	
	public void setTotalByteSizeOfAllSegments (long totalSize) {
		totalByteSizeOfAllSegments = totalSize;
	}
	
	public long getTotalByteSizeOfAllSegments () {
		return totalByteSizeOfAllSegments;
	}
	
	public void setSegmentNum (int x) {
		segments[0] = x;
	}
	
	public int getSegmentNum () {
		return segments[0];
	}
	
	public void setMaxSegments (int x) {
		segments[1] = x;
	}
	
	public int getMaxSegments () {
		return segments[1];
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