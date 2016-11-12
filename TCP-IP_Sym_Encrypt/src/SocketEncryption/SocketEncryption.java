package SocketEncryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
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
	private DataInputStream inputStream = null;
	private DataOutputStream outputStream = null;
	private ASym_Encrypt Encrypt = new ASym_Encrypt();
	private ASym_Decrypt Decrypt = new ASym_Decrypt();
	private boolean swappedKeysOnce = false;
	private Socket socket;
	
	 public SocketEncryption(Socket x) throws IOException {
	        socket = x;
	        outputStream = new DataOutputStream( x.getOutputStream() );
	        inputStream = new DataInputStream( x.getInputStream() );
	    }
	
	public InputStream getInputStream() throws IOException {
		return iS;
	}
	
	public OutputStream getOutputStream() throws IOException {
		return oS;
	}
	
	public boolean SwapPublicKeys () throws Exception{
		if ( socket.isConnected() && !swappedKeysOnce ) {			
			// Send signature message
			// Verify signature of remote user
			
			WriteMessage(Decrypt.GetPublicKey(), true);
			ReadMessage();
			
			return swappedKeysOnce;
		}
		return false;
	}
	
	private int BuildMessage(byte [] sourceArray, byte[] destArray, int startingByte, int totalSize) {
		Random rand = new Random( System.currentTimeMillis() );
		for (int i = 0; i < destArray.length; i++) {
			if (startingByte < totalSize)
				destArray[i] = sourceArray[startingByte++];
			else
				destArray[i] = (byte) rand.nextInt(255);
		}
		
		return startingByte;
	}
	
	private void WriteMessage (byte [] sourceData, boolean isKey) throws IOException {
		if ( isKey ) {
			outputStream.write( sourceData );
		}
		else {
			int startingByte = 0;
			int totalSize = sourceData.length;
			byte [] msgToSend;
			int RSA_MSG_Size = (Encrypt.getOthersNValue().bitLength() / 8) - 2;
			
			// Sends size of message
			outputStream.writeInt( totalSize );
			
			if (totalSize <= RSA_MSG_Size)
				outputStream.write( Encrypt.Encrypt( sourceData ) );
			else {
				// do while will break array into messageByteArraySize chunks and send them
				// loop will exit when all bytes are sent
				do {
					msgToSend = new byte [ RSA_MSG_Size ];
					startingByte = BuildMessage(sourceData, msgToSend, startingByte, totalSize);
					
					outputStream.write( Encrypt.Encrypt( msgToSend ) );
				} while(startingByte < totalSize);
			}
		}
	}
	
	private int ParseMessage (byte[] sourceArray, byte [] destArray, int startByte, int totalSize) {
		for (int i = 0; i < sourceArray.length; i++)
			if (startByte < totalSize)
				destArray[startByte++] = sourceArray[i];
			else
				break;
		
		return startByte;
	}
	
	private byte [] ReadMessage() {
		try {
			byte [] newMSG = new byte [1024];
			
			if (!swappedKeysOnce) {
				int len = inputStream.read( newMSG );
				byte [] msgRcvd = Arrays.copyOfRange(newMSG, 0, len);
				swappedKeysOnce = Encrypt.ReceiveKey( msgRcvd );
			}
			else {
				int totalBytes = inputStream.readInt();
				
				System.out.println("READMESSAGE(): totalBytes " + totalBytes );
				
				int len = inputStream.read(newMSG);
				
				System.out.println("READMESSAGE(): len " + len );
				
				newMSG = Arrays.copyOfRange(newMSG, 0, len);
				
				if (len == totalBytes)
					return Decrypt.Decrypt( newMSG );
				else {
					int startingByte = 0;
					byte [] msgRcvd = new byte [(int) totalBytes];
					
					startingByte = ParseMessage(newMSG, msgRcvd, startingByte, totalBytes);
					int RSA_MSG_Size = (Decrypt.getNValue().bitLength() / 8) - 2;
					
					while (startingByte < totalBytes) {
						newMSG = new byte[RSA_MSG_Size];
						len = inputStream.read(newMSG);
						newMSG = Decrypt.Decrypt( Arrays.copyOfRange(msgRcvd, 0, len) );
						startingByte = ParseMessage(newMSG, msgRcvd, startingByte, totalBytes);
						System.out.println("Read: " + (new String( Arrays.copyOfRange(newMSG, 0, len) ) ) );
					}
					
					return msgRcvd;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public class OStream extends OutputStream {
		
		@Override
		public void write(int b) throws IOException {
			WriteMessage( ByteArrayConversions.ObjectToByteArray(b), false );
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
}