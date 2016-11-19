package SocketEncryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import asym_Decrypt.ASym_Decrypt;
import asym_Encrypt.ASym_Encrypt;
import sym_AES_Based.sym_AES_Based;

// Class extends Socket in order to override read and write methods from inputstream and outputstream
// The methods are overridden in order to provide a seam less opportunity to user for encryption and decryption
// of data being transmitted
public class SocketEncryption extends Socket {
	
	// Creates and initializes InputStream and OutputStream objects
	private OStream oS = new OStream();
	private IStream iS = new IStream();
	private ObjectInputStream objectInputStream = null;
	private ObjectOutputStream objectOutputStream = null;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private ASym_Encrypt Encrypt = new ASym_Encrypt();
	private ASym_Decrypt Decrypt = null;
	private sym_AES_Based Sym_Cipher = null;
	private boolean swappedRSAKeys = false;
	private boolean useRSA = false;
	private boolean useAES = true;
	private Socket socket;
	
	public SocketEncryption(Socket x, int RSAKeyLength) throws Exception{
		useRSA = true;
		useAES = false;
		
		Decrypt = new ASym_Decrypt( RSAKeyLength );
		socket = x;
		objectOutputStream = new ObjectOutputStream( x.getOutputStream() );
		objectInputStream = new ObjectInputStream( x.getInputStream() );
		
		SwapRSAPublicKeys();
	}
	
	public SocketEncryption(Socket x, int RSAKeyLength, String AESKey, boolean UseCustomAESKey) throws Exception{
		useRSA = true;
		
		Decrypt = new ASym_Decrypt( RSAKeyLength );
		
		if (UseCustomAESKey)
			Sym_Cipher = new sym_AES_Based(AESKey);
		else
			Sym_Cipher = new sym_AES_Based();
		
		socket = x;
		objectOutputStream = new ObjectOutputStream( x.getOutputStream() );
		objectInputStream = new ObjectInputStream( x.getInputStream() );
		
		outputStream = x.getOutputStream();
		inputStream = x.getInputStream();
		
		SwapRSAPublicKeys();
	}
	
	public SocketEncryption(Socket x, String AESKey, boolean UseCustomAESKey) throws Exception{
		if (UseCustomAESKey)
			Sym_Cipher = new sym_AES_Based(AESKey);
		else
			Sym_Cipher = new sym_AES_Based();
		
		socket = x;
		outputStream = x.getOutputStream();
		inputStream = x.getInputStream();
	}
	
	public InputStream getInputStream() throws IOException {
		return iS;
	}
	
	public OutputStream getOutputStream() throws IOException {
		return oS;
	}
	
	public int getKeyBitLength () {
		return Decrypt.getNBitLength();
	}
	
	public void SwapRSAPublicKeys () throws Exception{
		if ( socket.isConnected() && !swappedRSAKeys ) {			
			objectOutputStream.writeObject(Decrypt.GetPublicKey());
			swappedRSAKeys = Encrypt.ReceiveKey( (KeyObject) objectInputStream.readObject() );
		
			if (!swappedRSAKeys)
				throw new Exception("Failed to exchange RSA Keys!");
		}
	}
	
	private int BuildMessage(byte [] sourceArray, byte[] destArray, int startingByte, int totalSize) {
		Random rand = new Random();
		for (int i = 0; i < destArray.length; i++) {
			if (startingByte < totalSize)
				destArray[i] = sourceArray[startingByte++];
			else
				destArray[i] = (byte) rand.nextInt(255);
		}
		
		return startingByte;
	}
	
	private void WriteMessage (byte [] sourceData) throws IOException {
		if (useRSA)
			WriteRSAMessage(sourceData);
		else
			WriteAESMessage(sourceData);
	}
	
	private void WriteAESMessage (byte [] sourceData) throws IOException {
		int startingByte = 0;
		int totalBytes = sourceData.length;
		byte [] msgToSend = new byte [256];
		
		byte [] countByteArray = ByteArrayConversions.IntToByteArray(totalBytes);
		// Sends size of message
		BuildMessage(countByteArray, msgToSend, 0, countByteArray.length);
		outputStream.write( Sym_Cipher.Encrypt( msgToSend ) );
		
		// do while will break array into 256 byte chunks chunks and send them
		// loop will exit when all bytes are sent
		do {
			msgToSend = new byte [ 256 ];
			startingByte = BuildMessage(sourceData, msgToSend, startingByte, totalBytes);
			
			outputStream.write( Sym_Cipher.Encrypt( msgToSend ) );
		} while(startingByte < totalBytes);
	
	}
	
	private void WriteRSAMessage (byte [] sourceData) throws IOException {
		int startingByte = 0;
		int totalBytes = sourceData.length;
		byte [] msgToSend;
		int RSA_MSG_Size = (Encrypt.getOthersNBitLength() / 8) - 2;
		
		// Sends size of message
		if (useAES)
			WriteAESMessage( Encrypt.Encrypt( ByteArrayConversions.IntToByteArray(totalBytes) ).toByteArray() );
		else
			objectOutputStream.writeObject( Encrypt.Encrypt( ByteArrayConversions.IntToByteArray(totalBytes) ) );
		
		// do while will break array into RSA_MSG_Size chunks and send them
		// loop will exit when all bytes are sent
		do {
			msgToSend = new byte [ RSA_MSG_Size ];
			startingByte = BuildMessage(sourceData, msgToSend, startingByte, totalBytes);
			
			if (useAES)
				WriteAESMessage( Encrypt.Encrypt( msgToSend ).toByteArray() );
			else
				objectOutputStream.writeObject( Encrypt.Encrypt( msgToSend ) );
		
		} while(startingByte < totalBytes);
	
	}
	
	private int ParseMessage (byte[] sourceArray, byte [] destArray, int startByte, int totalSize) {
		for (int i = 0; i < sourceArray.length; i++)
			if (startByte < totalSize)
				destArray[startByte++] = sourceArray[i];
			else
				break;
		
		return startByte;
	}
	
	private byte [] ReadMessage() throws IOException {
		if (useRSA)
			return ReadRSAMessage();
		else
			return ReadAESMessage();
	}
	
	private byte [] ReadAESMessage() throws IOException {
	
		byte [] msg = new byte [256];
		inputStream.read(msg);
		
		byte [] countSize = Sym_Cipher.Decrypt( msg );
		int totalBytes = ByteArrayConversions.ByteArrayToInt( Arrays.copyOfRange(countSize, 0, 4) );
		
		if (totalBytes == 1) {
			inputStream.read(msg);
			
			byte [] b = new byte[1];
			b[0] = Sym_Cipher.Decrypt( msg )[0];				
			return b;
		}
		else {
			int startingByte = 0;
			byte [] msgRcvd = new byte [totalBytes];
			
			do {
				msg = new byte [256];
				inputStream.read(msg);
				
				startingByte = ParseMessage(Sym_Cipher.Decrypt( msg ), msgRcvd, startingByte, totalBytes);
			} while (startingByte < totalBytes);
			
			return msgRcvd;
		}
	}
	
	private byte [] ReadRSAMessage() throws IOException {
		try {
			int totalBytes = 0;
			
			if (useAES) {
				byte [] countSize = Decrypt.Decrypt( new BigInteger( ReadAESMessage() ) );
				totalBytes = ByteArrayConversions.ByteArrayToInt( countSize );
			}
			else
				totalBytes = ByteArrayConversions.ByteArrayToInt(Decrypt.Decrypt( (BigInteger) objectInputStream.readObject() ));
			
			if (totalBytes == 1) {
				if (useAES) {
					byte [] b = new byte[1];
					b[0] = Decrypt.DecryptSingleByte( new BigInteger( ReadAESMessage()) );
					return b;
				}
				else {
					byte [] b = new byte[1];
					b[0] = Decrypt.DecryptSingleByte( (BigInteger) objectInputStream.readObject() );				
					return b;
				}
			}
			else {
				BigInteger c;
				
				int startingByte = 0;
				byte [] msgRcvd = new byte [totalBytes];
				
				do {
					if (useAES)
						c = new BigInteger( ReadAESMessage() );
					else
						c = (BigInteger) objectInputStream.readObject();
					
					startingByte = ParseMessage(Decrypt.Decrypt( c ), msgRcvd, startingByte, totalBytes);
				} while (startingByte < totalBytes);
				
				return msgRcvd;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public class OStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			byte [] x = new byte[1];
			x[0] = ByteBuffer.allocate(4).putInt(b).array()[3];
			WriteMessage( x );
		}
		
		@Override
		public void write(byte [] b) throws IOException  {
			WriteMessage(b);
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			WriteMessage( Arrays.copyOfRange(b, off, off+len) );
		}
	}
	
	private class IStream extends InputStream {
		byte [] buffer = null;
		
		@Override
		public int read(byte [] b) throws IOException {
			if (buffer == null) {
				buffer = ReadMessage();
				
				int i = 0;
				
				for ( ; i < b.length && i < buffer.length; i++)
					b[i] = buffer[i];
				
				if (i < buffer.length)
					buffer = Arrays.copyOfRange(buffer, i, buffer.length);
				else
					buffer = null;
				
				return i;
			}
			else {
				int i = 0;
				
				for ( ; i < b.length && i < buffer.length; i++)
					b[i] = buffer[i];
				
				if (i < buffer.length)
					buffer = Arrays.copyOfRange(buffer, i, buffer.length);
				else
					buffer = null;
				
				return i;
			}
		}
		
		@Override
		public int read(byte [] b, int off, int len) throws IOException {
			if (buffer == null) {
				buffer = ReadMessage();
				
				int i = 0;
				
				for ( ; i < b.length && i < buffer.length && i < len; i++)
					b[i + off] = buffer[i];
				
				if (i < buffer.length)
					buffer = Arrays.copyOfRange(buffer, i, buffer.length);
				else
					buffer = null;
				
				return i;
			}
			else {
				int i = 0;
				
				for ( ; i < b.length && i < buffer.length && i < len; i++)
					b[i + off] = buffer[i];
				
				if (i < buffer.length)
					buffer = Arrays.copyOfRange(buffer, i, buffer.length);
				else
					buffer = null;
				
				return i;
			}
		}
		
		@Override
		public int read() throws IOException {
			if (buffer == null) {
				buffer = ReadMessage();
				
				byte b = buffer[0];
				
				if (buffer.length > 1)
					buffer = Arrays.copyOfRange(buffer, 1, buffer.length);
				else
					buffer = null;
				
				return b;
			}
			else {
				byte b = buffer[0];
				
				if (buffer.length > 1)
					buffer = Arrays.copyOfRange(buffer, 1, buffer.length);
				else
					buffer = null;
				
				return b;	
			}
		}
	}
	
	public Object readObject() throws ClassNotFoundException, IOException {
		return ByteArrayConversions.ByteArrayToObject( ReadMessage() );
	}
	
	public void writeObject(Object x) throws IOException {
		WriteMessage( ByteArrayConversions.ObjectToByteArray( x ) );
	}
}