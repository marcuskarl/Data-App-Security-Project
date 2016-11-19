package sym_AES_Based;

import java.math.BigInteger;

public class sym_AES_Based {
	
	private int blockCount = 16;
	private int matrixBlock = 4;
	private String String_key = null;
	private byte [] key = null;
	
	private byte [][] SubBlockEnc = null;
	private byte [][] SubBlockDec = null;
	
	public sym_AES_Based () {
		String_key = "250219206866755364471960248037233305641";	// 128 bit generated prime
		key = String_key.getBytes();
		
		int blockSize = 16;
		
		SubBlockEnc = new byte [blockSize][blockSize];
		SubBlockDec = new byte [blockSize][blockSize];
		
		// Fills SubBlockEnc with values from 0 to 255 in sequential order
		for (int i = 0, t = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++, t++)
				SubBlockEnc[i][j] = (byte) t;
		
		// Scrambles SubBlockEnc using key decimal values and shifting of rows within cols 
		// and cols within rows by varying amounts
		for (int i = 0; i < String_key.length(); i++){
			SubBlockEnc = ShiftColInRowEncrypt(SubBlockEnc, String_key.charAt(i++), blockSize);
			
			if (i < String_key.length())
				SubBlockEnc = ShiftRowInColEncrypt(SubBlockEnc, String_key.charAt(i), blockSize);
		}
		
		int row = 0,
			col = 0;
		
		// Builds up a SubBlockDec matrix of original values in matrix position
		// is used to reverse the byte substitution
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) {
				col = Math.floorMod(Byte.toUnsignedInt(SubBlockEnc[i][j]), blockSize);
				row = Byte.toUnsignedInt(SubBlockEnc[i][j]) / blockSize;
				
				SubBlockDec[row][col] = (byte) ( (i * blockSize) + j);
			}
	}
	
	public sym_AES_Based (String s) throws Exception {
		String_key = s;				// Custom 128 bit key from user
		
		if ( (new BigInteger( s.getBytes() ) ).bitLength() != 128 )
			throw new Exception("Key bit length is not 128 bits!");
		
		key = String_key.getBytes();
		
		int blockSize = 16;
		
		SubBlockEnc = new byte [blockSize][blockSize];
		SubBlockDec = new byte [blockSize][blockSize];
		
		// Fills SubBlockEnc with values from 0 to 255 in sequential order
		for (int i = 0, t = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++, t++)
				SubBlockEnc[i][j] = (byte) t;
		
		// Scrambles SubBlockEnc using key decimal values and shifting of rows within cols 
		// and cols within rows by varying amounts
		for (int i = 0; i < String_key.length(); i++){
			SubBlockEnc = ShiftColInRowEncrypt(SubBlockEnc, String_key.charAt(i++), blockSize);
			
			if (i < String_key.length())
				SubBlockEnc = ShiftRowInColEncrypt(SubBlockEnc, String_key.charAt(i), blockSize);
		}
		
		int row = 0,
			col = 0;
		
		// Builds up a SubBlockDec matrix of original values in matrix position
		// is used to reverse the byte substitution
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) {
				col = Math.floorMod(Byte.toUnsignedInt(SubBlockEnc[i][j]), blockSize);
				row = Byte.toUnsignedInt(SubBlockEnc[i][j]) / blockSize;
				
				SubBlockDec[row][col] = (byte) ( (i * blockSize) + j);
			}
	}
	
	public byte [] Encrypt(byte [] x) {
		byte [][][] arr = new byte [blockCount][matrixBlock][matrixBlock];
		
		// Copies original array into 3D array for scrambling values
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					arr[i][j][l] = x[k];
		
		// Does encryption on each 128 bit section of the array
		for (int i = 0; i < blockCount; i++)
			for (int j = 0; j < 10; j++) {
				arr[i] = SubEncrypt(arr[i], matrixBlock);
				arr[i] = ShiftColInRowEncrypt(arr[i], key[j], matrixBlock);
				arr[i] = ShiftRowInColEncrypt(arr[i], key[j], matrixBlock);
				arr[i] = XORRoundKey(arr[i], matrixBlock);
		}
		
		byte [] y = new byte [256];
		
		// Copies 3D array back into new single dimension array
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					y[k] = arr[i][j][l];
		
		return y;
	}
	
	public byte [] Decrypt(byte [] x) {
		byte [][][] arr = new byte [blockCount][matrixBlock][matrixBlock];
		
		// Copies original array into 3D array for scrambling values
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					arr[i][j][l] = x[k];
		
		// Does encryption on each 128 bit section of the array
		for (int i = 0; i < blockCount; i++)
			for (int j = 9; j > -1; j--) {
				arr[i] = XORRoundKey(arr[i], matrixBlock);
				arr[i] = ShiftRowInColDecrypt(arr[i], key[j], matrixBlock);
				arr[i] = ShiftColInRowDecrypt(arr[i], key[j], matrixBlock);
				arr[i] = SubDecrypt(arr[i], matrixBlock);
		}
		
		byte [] y = new byte [256];
		
		// Copies 3D array back into original array
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					y[k] = arr[i][j][l];
		
		return y;
	}
	
	private byte [][] XORRoundKey(byte[][] x, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++)
				arr[i][j] = (byte) (x[i][j] ^ key[i + j]); // XORs Key with array to get round key
		
		return arr;
	}
	
	private byte [][] SubEncrypt(byte [][] x, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		int col = 0,
			row = 0;
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) {
				col = Math.floorMod(Byte.toUnsignedInt( x[i][j] ), 16);
				row = Byte.toUnsignedInt( x[i][j] ) / 16;
				
				arr[i][j] = SubBlockEnc[row][col];
			}
		
		return arr;
	}
	
	private byte [][] SubDecrypt(byte [][] x, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		int col = 0,
			row = 0;
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) {
				col = Math.floorMod(Byte.toUnsignedInt( x[i][j] ), 16);
				row = Byte.toUnsignedInt( x[i][j] ) / 16;
				
				arr[i][j] = SubBlockDec[row][col];
			}
		
		return arr;
	}
	
	private byte [][] ShiftColInRowEncrypt (byte [][] x, int amount, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		
		for (int i = 0; i < blockSize; i++) {
			int shift = Math.floorMod(i + amount, blockSize);
			for (int j = 0; j < blockSize; j++)
				arr[i][j] = x[i][ (j + shift) % blockSize ];
		}
		return arr;
	}
	
	private byte [][] ShiftRowInColEncrypt (byte [][] x, int amount, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		
		for (int j = 0; j < blockSize; j++) {
			int shift = Math.floorMod(j + amount, blockSize);
			for (int i = 0; i < blockSize; i++)
				arr[i][j] = x[ (i + shift) % blockSize ][j];
			}
		
		return arr;
	}
	
	private byte [][] ShiftColInRowDecrypt (byte [][] x, int amount, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		
		for (int i = blockSize - 1; i > -1; i--) {
			int shift = Math.floorMod(blockSize - i - amount, blockSize);
			for (int j = blockSize - 1; j > -1; j--)
				arr[i][j] = x[i][ (j + shift) % blockSize ];
		}
		return arr;
	}
	
	private byte [][] ShiftRowInColDecrypt (byte [][] x, int amount, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		
		for (int j = blockSize - 1; j > -1; j--){
			int shift = Math.floorMod(blockSize - j - amount, blockSize);
			for (int i = blockSize - 1; i > -1; i--)
				arr[i][j] = x[ (i + shift) % blockSize ][j];
			}
		
		return arr;
	}
}