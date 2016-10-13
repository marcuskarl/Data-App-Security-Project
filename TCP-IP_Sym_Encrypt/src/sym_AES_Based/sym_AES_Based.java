package sym_AES_Based;

public class sym_AES_Based {
	
	private int maxtrixSize = 16;
	
	private byte [] key = null;
	
	
	public void Encrypt(byte [] x) {
		
		byte [][] arr = new byte [maxtrixSize][maxtrixSize];
		
		
		
		
	}
	
	private byte [][] ShiftRow(byte [][] x, int amount) {
		byte [][] tempRow = new byte[maxtrixSize][maxtrixSize];
		
		for (int i = 0; i < maxtrixSize; i++)
			for (int j = 0; j < maxtrixSize; j++) 
				tempRow[(i + amount) % maxtrixSize][j] = x[i][j]; 
		
		return tempRow;
	}
	
	private byte [][] ShiftCol(byte [][] x, int amount) {
		byte [][] tempRow = new byte[maxtrixSize][maxtrixSize];
		
		for (int i = 0; i < maxtrixSize; i++)
			for (int j = 0; j < maxtrixSize; j++) 
				tempRow[i][(j + amount) % maxtrixSize] = x[i][j]; 
		
		return tempRow;
	}
}
