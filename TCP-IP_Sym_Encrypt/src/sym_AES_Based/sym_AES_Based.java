package sym_AES_Based;

public class sym_AES_Based {
	
	private int blockCount = 16;
	private int matrixBlock = 4;
	
	private byte [] key = null;
	
	private byte [][] SubBlockEnc = new byte [16][16];
	private byte [][] SubBlockDec = new byte [16][16];
	
	public sym_AES_Based () {
		int blockSize = 16;
		for (int i = 0, t = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++, t++)
				SubBlockEnc[i][j] = (byte) t;
		
		int row = 0,
			col = 0;
		
		SubBlockEnc = ShiftRow(SubBlockEnc, 2, blockSize);
		SubBlockEnc = ShiftCol(SubBlockEnc, 2, blockSize);
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) {
				col = SubBlockEnc[i][j] % blockSize;
				row = (int) SubBlockEnc[i][j] / blockSize;
				
				SubBlockDec[row][col] = (byte) (i * blockSize + j);
			}
		
	}
	
	
	public void Encrypt(byte [] x) {
		
		byte [][][] arr = new byte [blockCount][matrixBlock][matrixBlock];
		
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					arr[i][j][l] = x[k];
		
		for (int i = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++) {
				arr[i] = ShiftRow(arr[i], j % matrixBlock, matrixBlock);
				arr[i] = ShiftCol(arr[i], j % matrixBlock, matrixBlock);
		}
		
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					x[k] = arr[i][j][l];
	}
	
	private byte [][] ShiftRow(byte [][] x, int amount, int blockSize) {
		byte [][] arr = new byte[blockSize][blockSize];
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) 
				arr[(i + amount) % blockSize][j] = x[i][j]; 
		
		return arr;
	}
	
	private byte [][] ShiftCol(byte [][] x, int amount, int blockSize) {
		byte [][] arr = new byte[blockSize][blockSize];
		
		for (int i = 0; i < blockSize; i++)
			for (int j = 0; j < blockSize; j++) 
				arr[i][(j + amount) % blockSize] = x[i][j]; 
		
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
