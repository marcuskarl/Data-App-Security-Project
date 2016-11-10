package SocketEncryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;

import asym_Decrypt.ASym_Decrypt;
import asym_Encrypt.ASym_Encrypt;

// Class extends Socket in order to override read and write methods from inputstream and outputstream
// The methods are overridden in order to provide a seam less opportunity to user for encryption and decryption
// of data being transmitted
public class SocketEncryption extends Socket {
	
	// Creates and initializes InputStream and OutputStream objects
	private OStream oS = new OStream();
	private IStream iS = new IStream();
	private OOStream ooS = null;
	private OIStream oiS = null;
	private ASym_Encrypt Encrypt = new ASym_Encrypt();
	private ASym_Decrypt Decrypt = new ASym_Decrypt();
	private boolean swappedKeys = false;
	private int messageBlockByteArraySize = 135; // Message block size of 135 lets the EncryptionObject 
												 // size be 254 bytes after serialization
	
	public InputStream getInputStream() throws IOException {
		iS = (IStream) super.getInputStream();
		
		return iS;
	}
	
	public OutputStream getOutputStream() throws IOException {
		oS = (OStream) super.getOutputStream();
		
		return oS;
	}
	
	public ObjectOutputStream getObjectOutputStream () throws IOException {
		ooS = new OOStream( super.getOutputStream() );
		return ooS.getOOStream();
	}
	
	public ObjectInputStream getObjectInputStream () throws IOException {
		oiS = new OIStream( super.getInputStream() );
		return oiS.getOIStream();
	}
	
	public boolean SwapPublicKeys () throws Exception{
		if ( this.isConnected() && !swappedKeys ) {
			
			// Send signature message
			// Verify signature of remote user
			
			
			/*
			EncryptionObject OutGoingKey = new EncryptionObject();
			EncryptionObject IncomingKey = null;
			OutGoingKey.setKey( true );
			OutGoingKey.setMsg( Decrypt.GetPublicKey() );
			
			
			*/
			
			byte [] key = Decrypt.GetPublicKey();
			
			byte [] outMsgWithKey = new byte [ 9 + key.length ];
			byte [] sizeOfKey = ByteArrayConversions.LongToByteArray((long) key.length);
			
			outMsgWithKey[0] = (byte) 0;
			
			for (int i = 0; i < sizeOfKey.length; i++)
				outMsgWithKey[i + 1] = sizeOfKey[i];
			
			for (int i = 0; i < outMsgWithKey.length; i++)
				outMsgWithKey[i + 9] = key[i]; 
			
			// test
			getOutputStream();
			getInputStream();
			ObjectOutputStream oos = new ObjectOutputStream( oS );
			ObjectInputStream ois = new ObjectInputStream( iS );
			
			oos.writeObject( outMsgWithKey );
			
			
			//
			IncomingKey = (EncryptionObject) ois.readObject();
			
			IncomingKey = OutGoingKey;
			
			oos.close();
			ois.close();
			
			if ( IncomingKey.getKey() == true ) {
				Encrypt.ReceiveKey( IncomingKey.getMsg() );
				
				// Send/Receive test message
				swappedKeys = true;
				return true;
			}
		}
		return false;
	}
	
	/*
	private byte [] ParseMessage(byte[] b, long offSet, long l) {
		byte [] arr = new byte[(int) l];
		for (int i = 0; i < l; i++)
			arr[i] = b[(int) (offSet + i)];
		
		return arr;
	}
	*/
	
	private void BuildMessage(byte[] x, byte [] sourceArray, long startingByte, long totalSize, int msgSize) {
		Random rand = new Random();
		for (int i = 0; i < msgSize; i++) {
			if (i == 0){
				try {
					byte[] v = ByteArrayConversions.LongToByteArray(totalSize);
					
					x[++i] = v[0];
					x[++i] = v[1];
					x[++i] = v[2];
					x[++i] = v[3];
					x[++i] = v[4];
					x[++i] = v[5];
					x[++i] = v[6];
					x[++i] = v[7];
					
					// i will be 8 now and increment to 17 at end of for loop to begin coping message over
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if (startingByte < totalSize)
				x[i] = sourceArray[(int) startingByte++];
			else
				x[i] = (byte) rand.nextInt(255);
		}
	}
	
	private void WriteMessage (byte [] x, boolean isKey) throws IOException {
		long startingByte = 0;
		long totalSize = x.length;
		int msgSize = Encrypt.getOthersNValue().bitCount() / 8;			
		byte [] msgToSend;
		
		// do while will break array into messageByteArraySize chunks and send them
		// loop will exit when all bytes are sent
		do {
			msgToSend = new byte [ msgSize ];
			
			if (isKey) 
				msgToSend[0] = (byte) 0;
			else
				msgToSend[0] = (byte) 1;
			
			BuildMessage(msgToSend, x, startingByte, totalSize, msgSize);
			BigInteger c = Encrypt.Encrypt( msgToSend );
			ooS.writeObject(c);
		} while(startingByte < totalSize);
	}
	
	private void ParseMessage (byte[] sourceArray, byte [] destArray,long startByte, long totalSize) {
		for (int i = 8; i < sourceArray.length; i++)
			if (startByte < totalSize)
				destArray[(int) startByte++] = sourceArray[i];
	}
	
	private <T> byte [] ReadMessage() {
		try {
			BigInteger c = (BigInteger) oiS.readObject();
			byte [] newMSG = Decrypt.Decrypt(c);
			byte [] tSize = new byte [Long.BYTES];
			
			for (int i = 0; i < tSize.length; i++)
				tSize[i] = newMSG[i + 1];
			
			long startingByte = 0;
			long totalSize = ByteArrayConversions.ByteArrayToLong(tSize);
			byte [] msgRcvd = new byte [(int) totalSize];
			
			ParseMessage(newMSG, msgRcvd, startingByte, totalSize);
			
			while (startingByte < totalSize) {
				c = (BigInteger) oiS.readObject();
				newMSG = Decrypt.Decrypt(c);
				ParseMessage(newMSG, msgRcvd, startingByte, totalSize);
			}
			
			return msgRcvd;
			
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*private void WriteMessage (byte [] x, boolean isKey) throws IOException {
		if (x.length > messageBlockByteArraySize) {
			int segNum = 1;
			int totalSeg;
			long offSet = 0;
			byte [] array = null;
			
			//  If construct determines the total number segments the message needs to be broken up into
			double testValue = x.length/messageBlockByteArraySize;
			if (testValue != Math.floor(testValue))
				totalSeg = (int) (Math.floor(testValue) + 1);
			else
				totalSeg = (int) testValue;
			
			// do while will break array into messageByteArraySize chunks and send them
			// loop will exit when the last chunk of the byte array remaining is smaller than
			// the messageByteArraySize size
			do {
				array = ParseMessage(x, offSet, messageBlockByteArraySize);
				EncryptionObject m = CreateEncryptionObject(array, isKey, segNum++, totalSeg, x.length);
				BigInteger c = Encrypt.Encrypt( ByteArrayConversions.AnyTypeToByteArray(m) );
				ooS.writeObject(c);
				offSet += messageBlockByteArraySize;
			} while(offSet <= (x.length - messageBlockByteArraySize));
			
			// Sends final chunk of data remaining in the byte array
			if (offSet != x.length) {
				array = ParseMessage(x, offSet, x.length - offSet);
				EncryptionObject m = CreateEncryptionObject(array, isKey, segNum++, totalSeg, x.length);
				BigInteger c = Encrypt.Encrypt( ByteArrayConversions.AnyTypeToByteArray(m) );
				ooS.writeObject(c);
			}	
		}
		else { // If the message was smaller than the messageBlockByteArraySize size, it is sent
			EncryptionObject m = CreateEncryptionObject(x, isKey, 1, 1, x.length);
			BigInteger c = Encrypt.Encrypt( ByteArrayConversions.AnyTypeToByteArray(m) );
			ooS.writeObject(c);
		}
	}*/
	
	/*private <T> byte [] ReadMessage() {
		try {
			BigInteger c = (BigInteger) oiS.readObject();
			EncryptionObject m = (EncryptionObject) ByteArrayConversions.ByteArrayToAnyType( Decrypt.Decrypt(c) );
			long size = m.getTotalByteSizeOfAllSegments();
			int totalSeg = m.getMaxSegments();
			long i = -1;
			
			byte [] temp = new byte [ (int) size ];
			int currentSeg = m.getSegmentNum();
			byte [] x = m.getMsg();
			for (int j = 0; j < x.length; j++ ) {
				i++;
				temp[(int) i] = x[j];
			}
			
			while ( currentSeg <= totalSeg && i < size ) {
				c = (BigInteger) oiS.readObject();
				m = (EncryptionObject) ByteArrayConversions.ByteArrayToAnyType( Decrypt.Decrypt(c) );
				currentSeg = m.getSegmentNum();
				x = m.getMsg();
				for (int j = 0; j < x.length; j++ ) {
					i++;
					temp[(int) i] = x[j];
				}
				
				return temp;
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}*/
	
	private EncryptionObject CreateEncryptionObject (byte [] x, boolean isKey, int segNum, int totalSeg, long totalSize) {
		EncryptionObject EO = new EncryptionObject();
		
		EO.setKey(isKey);
		EO.setMsg(x);
		EO.setSegmentNum(segNum);
		EO.setMaxSegments(totalSeg);
		EO.setTotalByteSizeOfAllSegments(totalSize);
		
		return EO;
	}
	
	private class OStream extends OutputStream {
		
		@Override
		public void write(int b) throws IOException {
			WriteMessage( ByteArrayConversions.AnyTypeToByteArray(b), false );
		}
		
		@Override
		public void write(byte [] b) throws IOException {
			WriteMessage(b, false);
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			// Copies byte array from given array into a new array with specified elements to write from
			byte [] arr = new byte[len];
			for (int i = 0; i < len; i++)
				arr[i] = b[off + i];
			
			WriteMessage(arr, false);
		}
		
	}
	
	private class IStream extends InputStream {

		@Override
		public int read(byte [] b) throws IOException {
			byte [] arr = ReadMessage();
			
			for (int i = 0; i < b.length && i < arr.length; i++)
				b[i] = arr[i];
			
			return arr.length;
		}
		
		@Override
		public int read(byte [] b, int off, int len) throws IOException {
			byte [] arr = ReadMessage();
			
			for (int i = 0; i < b.length && i < arr.length && i < len; i++)
				b[i + off] = arr[i];
			
			return arr.length;
		}

		@Override
		// This method needs changed.....it discards the rest of the byte array after returning
		// the first byte
		public int read() throws IOException {
			return ReadMessage()[0];
		}
	}
	
	private class OOStream extends ObjectOutputStream {
		private ObjectOutputStream ooS = null;
		
		OOStream ( OutputStream x) throws IOException {
			ooS = new ObjectOutputStream( x );
		}
		
		public ObjectOutputStream getOOStream() {
			return ooS;
		}
		
	}
	
	private class OIStream extends ObjectInputStream {
		private ObjectInputStream oiS = null;
		
		OIStream ( InputStream x) throws IOException {
			oiS = new ObjectInputStream( x );
		}
		
		public ObjectInputStream getOIStream() {
			return oiS;
		}
		
		// public byte 
	}
}