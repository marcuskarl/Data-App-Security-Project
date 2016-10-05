package Sym_Encrypt;

public class Sym_Encrypt {
	private byte [] OthersPublicKey = null;
	
	public boolean SetPublicKey (byte [] PK) {
		if (PK != null) {
			OthersPublicKey = PK;
			return true;
		}
		else
			return false;		
	}
	
	
	
}
