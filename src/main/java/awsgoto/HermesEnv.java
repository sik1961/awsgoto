package awsgoto;

public enum HermesEnv {
	PROD(1),
	PREPROD(2),
	SIT(3),
	UAT(4),
	UNKNOWN(5);
	
private int rank;
	
	private HermesEnv(int rank) {
		this.rank = rank;
	}
	
	public int getRank() {
		return this.rank;
	}
	
}
