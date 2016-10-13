package SocketEncryption;

import java.io.Serializable;

public class EncryptionObject implements Serializable {
	// Variables are only given a single character to minimize serialized size for encryption block
	// Segments are used for messages greater than the given block size. In this case the message
	// is broken up into multiple segments for recompiling after transmission.
	private static final long serialVersionUID = 01L;
	private boolean k = false;	// Flags if object has public key as msg
	private byte [] m = null;	// The message being sent
	private int s = 0;			// The segment number of this object
	private int t = 0;			// The total number of segments for message
	private long z = 0;			// The total byte size of all segments combined
	
	public void setTotalByteSizeOfAllSegments (long totalSize) {
		z = totalSize;
	}
	
	public long getTotalByteSizeOfAllSegments () {
		return z;
	}
	
	public void setSegmentNum (int x) {
		s = x;
	}
	
	public int getSegmentNum () {
		return s;
	}
	
	public void setMaxSegments (int x) {
		t = x;
	}
	
	public int getMaxSegments () {
		return t;
	}
	
	public boolean getKey () {
		return k;
	}
	
	public void setKey (boolean x) {
		k = x;
	}
	
	public byte [] getMsg () {
		return m;
	}
	
	public void setMsg (byte [] x) {
		m = x;
	}
}