package sym_AES_Based;

public class sym_AES_Based {
	
	private int blockCount = 16;
	private int matrixBlock = 4;
	
	private byte [] key = null;
	
	
	public void Encrypt(byte [] x) {
		
		byte [][][] arr = new byte [blockCount][matrixBlock][matrixBlock];
		
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					arr[i][j][l] = x[k];
		
		for (int i = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++) {
				arr[i] = ShiftRow(arr[i], j % matrixBlock);
				arr[i] = ShiftCol(arr[i], j % matrixBlock);
		}
		
		for (int i = 0, k = 0; i < blockCount; i++)
			for (int j = 0; j < matrixBlock; j++)
				for (int l = 0; l < matrixBlock && k < x.length; l++, k++)
					x[k] = arr[i][j][l];
	}
	
	private byte [][] ShiftRow(byte [][] x, int amount) {
		byte [][] tempRow = new byte[matrixBlock][matrixBlock];
		
		for (int i = 0; i < matrixBlock; i++)
			for (int j = 0; j < matrixBlock; j++) 
				tempRow[(i + amount) % 4][j] = x[i][j]; 
		
		return tempRow;
	}
	
	private byte [][] ShiftCol(byte [][] x, int amount) {
		byte [][] tempRow = new byte[matrixBlock][matrixBlock];
		
		for (int i = 0; i < matrixBlock; i++)
			for (int j = 0; j < matrixBlock; j++) 
				tempRow[i][(j + amount) % matrixBlock] = x[i][j]; 
		
		return tempRow;
	}
}
