package awsgoto;

public class AwsInstance implements Comparable<AwsInstance> {
	private String name;
	private String instanceId;
	private String keyName;
	private String ipAddress;
	private HermesEnv env;
	private HermesProduct product;
	private boolean appInstance;

	public AwsInstance(String name, String instanceId, String keyName, String ipAddress) {
		super();
		this.name = name;
		this.instanceId = instanceId;
		this.keyName = keyName;
		this.ipAddress = ipAddress;
		this.setEnv(name);
		this.setDomain(name);
		this.setAppInstance(name);
	}

	public String getName() {
		return this.name;
	}

	public String getInstanceId() {
		return this.instanceId;
	}

	public String getKeyName() {
		return this.keyName;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public HermesEnv getEnv() {
		return this.env;
	}

	public HermesProduct getProduct() {
		return this.product;
	}

	public boolean isAppInstance() {
		return this.appInstance;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	private void setEnv(final String name) {
		if (name.split("-")[2].equalsIgnoreCase("prod")) {
			env = HermesEnv.PROD;
		} else if (name.split("-")[2].equalsIgnoreCase("preprod")) {
			env = HermesEnv.PREPROD;
		} else if (name.split("-")[2].equalsIgnoreCase("sit")) {
			env = HermesEnv.SIT;
		} else if (name.split("-")[2].equalsIgnoreCase("uat")) {
			env = HermesEnv.UAT;
		} else {
			env = HermesEnv.UNKNOWN;
		}
	}

	private void setDomain(final String name) {
		if (name.contains("-cpa-") || name.contains("-app-cpa-")) {
			product = HermesProduct.CPA;
		} else if (name.contains("-pr-") || name.contains("-app-pr-")) {
			product = HermesProduct.PR;
		} else if (name.contains("-app-dgw-")) {
			product = HermesProduct.DGW;
		} else {
			product = HermesProduct.DGW;
		}
	}

	private void setAppInstance(final String name) {
		this.appInstance = name.contains("-app-cpa-") || name.contains("-app-pr-") || name.contains("-app-dgw-");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.instanceId == null) ? 0 : this.instanceId.hashCode());
		result = prime * result + ((this.ipAddress == null) ? 0 : this.ipAddress.hashCode());
		result = prime * result + ((this.keyName == null) ? 0 : this.keyName.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AwsInstance other = (AwsInstance) obj;
		if (this.instanceId == null) {
			if (other.instanceId != null)
				return false;
		} else if (!this.instanceId.equals(other.instanceId))
			return false;
		if (this.ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!this.ipAddress.equals(other.ipAddress))
			return false;
		if (this.keyName == null) {
			if (other.keyName != null)
				return false;
		} else if (!this.keyName.equals(other.keyName))
			return false;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AwsInstance [name=");
		builder.append(this.name);
		builder.append(", env=");
		builder.append(this.env);
		builder.append(", product=");
		builder.append(this.product);
		builder.append(", instanceId=");
		builder.append(this.instanceId);
		builder.append(", keyName=");
		builder.append(this.keyName);
		builder.append(", ipAddress=");
		builder.append(this.ipAddress);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(AwsInstance that) {
		if (this.appInstance && !that.appInstance) {
			return 1;
		} else if (!this.appInstance && that.appInstance) {
			return -1;
		} else if (this.env.getRank() < that.env.getRank()) {
			return -1;
		} else if (this.env.getRank() > that.env.getRank()) {
			return 1;
		} else {
			if (this.product.getRank() < that.product.getRank()) {
				return -1;
			} else if (this.product.getRank() > that.product.getRank()) {
				return 1;
			} else {
				return that.name.hashCode() - this.name.hashCode();
			}
		}
	}

}
