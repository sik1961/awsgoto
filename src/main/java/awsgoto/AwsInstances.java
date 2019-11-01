package awsgoto;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class AwsInstances {
	private SortedMap<Integer,AwsInstance> instanceMap;

	public AwsInstances(final Collection<AwsInstance> awsInstances, final Integer startIndex) {
		Integer index = 1; 
		if (startIndex != null) {
			index=startIndex;
		} 
		this.instanceMap = new TreeMap<>();
		for(AwsInstance instance:awsInstances) {
			this.instanceMap.put(index++, instance);
		}
	}
	
	public SortedMap<Integer, AwsInstance> getInstanceMap() {
		return this.instanceMap;
	}
	
	

}
