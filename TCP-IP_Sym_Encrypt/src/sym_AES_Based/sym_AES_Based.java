package sym_AES_Based;

public class sym_AES_Based {
	
	private int blockCount = 16;
	private int matrixBlock = 4;
	private int keyCount; // Used in Round Key
	private byte [] key = null;
	// Key needs a value
	
	private byte [][] SubBlockEnc = new byte [16][16];
	private byte [][] SubBlockDec = new byte [16][16];
	
	public sym_AES_Based () {
		int blockSize = 16;
		for (int i = 0, t = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++, t++)
				SubBlockEnc[i][j] = (byte) t;
		
		// Added some skeleton code for key generation. Don't know what we're doing here.
		// It's necessary for the XOR operation in AddRoundKey
		key = new byte [256];
		keyCount = 0;
		for (int i = 0; i < key.length; i++) {
			// Whatever we're doing to generate the key
		}
		
		int row = 0,
			col = 0;
		
		SubBlockEnc = ShiftColsInRows(SubBlockEnc, blockSize);
		SubBlockEnc = ShiftCol(SubBlockEnc, 2, blockSize);
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) {
				col = SubBlockEnc[i][j] % blockSize;
				row = (int) SubBlockEnc[i][j] / blockSize;
				
				SubBlockDec[row][col] = (byte) (i * blockSize + j);
			}
		
	}
	
	public void Encrypt(byte [] x) {
		// Resets keyCount every encrypt
		keyCount = 0;
		
		byte [][][] arr = new byte [blockCount][matrixBlock][matrixBlock];
		
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					arr[i][j][l] = x[k];
		
		for (int i = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++) {
				arr[i] = ShiftColsInRows(arr[i], matrixBlock);
				arr[i] = ShiftCol(arr[i], j % matrixBlock, matrixBlock);
				arr[i] = AddRoundKey(arr[i], j % matrixBlock, martrixBlock); // Think this would go here
		}
		
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					x[k] = arr[i][j][l];
	}
	
	private byte [][] ShiftColsInRows(byte [][] x, int blockSize) {
		byte [][] arr = new byte[blockSize][blockSize];
		
		for (int i = 1; i < blockSize; i++)
			if (i == 1)
				for (int j = 0; j < blockSize; j++) 
					arr[i][(j + 1) % blockSize] = x[i][j];
			else if (i == 2)
				for (int j = 0; j < blockSize; j++) 
					arr[i][(j + 2) % blockSize] = x[i][j];
			else if (i == 3)
				for (int j = 0; j < blockSize; j++) 
					arr[i][(j + 3) % blockSize] = x[i][j];
		
		return arr;
	}
	
	private byte [][] ShiftCol(byte [][] x, int amount, int blockSize) {
		byte [][] arr = new byte[blockSize][blockSize];
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) 
				arr[i][(j + amount) % blockSize] = x[i][j]; 
		
		return arr;
	}
	
	// Algorithm should be sound. The only thing I'm unsure of is placement in the encryption algorithm
	private byte [][] AddRoundKey(byte[][] x, int amount, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		for (int i = 0; i < blockSize; i++) {
			for (int j = 0; j < blockSize; j++) {
				arr[i][j] = (byte) (x[i][(j + amount) % blockSize] ^ key[keyCount]); // XORs Key with array to get round key
				keyCount++; // Increments keyCount to use next part of key
			}
		}
		return arr;
	}
	
	private byte [][] SubEncrypyt(byte [][] x, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		int col = 0,
			row = 0;
		
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) {
				col = x[i][j] % blockSize;
				row = (int) x[i][j] / blockSize;
				
				arr[i][j] = SubBlockEnc[row][col];
			}
		
		return arr;
	}
	
	private byte [][] SubDecrypyt(byte [][] x, int blockSize) {
		byte [][] arr = new byte [blockSize][blockSize];
		int col = 0,
			row = 0;
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) {
				col = x[i][j] % blockSize;
				row = (int) x[i][j] / blockSize;
				
				arr[i][j] = SubBlockDec[row][col];
			}
		
		return arr;
	}
}
