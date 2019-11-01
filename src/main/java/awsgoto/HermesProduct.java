package awsgoto;

public enum HermesProduct {
	CPA(1),
	PR(2),
	DGW(3),
	OTHER(4);
	
private int rank;
	
	private HermesProduct(int rank) {
		this.rank = rank;
	}
	
	public int getRank() {
		return this.rank;
	}
	
}
