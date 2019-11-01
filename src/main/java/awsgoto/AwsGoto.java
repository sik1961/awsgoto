package awsgoto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AwsGoto {

	private static final String DGW_KEY_PREFIX = "hermes-dgw-";
	private static final String BASH = "#!/bin/bash";
	private static final String MENU_FMT = "%1$2s %2$s";
	private static final String MT = "";

	
	public static void main(String[] args) {
		List<String> fileLines = new ArrayList<>();
		try {
			Files.lines(Paths.get("/media/sf_vmShared/aws-instances-name-instanceid-keyname-privateipaddress.dat"))
					.map(s -> s.trim()).filter(s -> !s.startsWith("#")).forEach(fileLines::add);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<AwsInstance> instances = new ArrayList<>();

		int lineType = -1;
		String name = null;
		String id = null;
		String key = null;
		String address = null;

		for (String line : fileLines) {
			lineType++;
			System.out.println(line + "(" + lineType + ")");

			switch (lineType) {
			case 0:
				name = line;
				break;

			case 1:
				id = line;
				break;

			case 2:
				key = line;
				break;

			case 3:
				address = line;
				break;

			case 4 :
			      if (line.isEmpty()) {
			    	  instances.add(new AwsInstance(name,id,key,address));
			    	  name = null;
			    	  id = null;
			    	  key = null;
			    	  address = null;
			    	  lineType = -1;
			      } else {
			    	  throw new IllegalStateException("Error - line unintentionally blank" + line);
			      }
			      break;
			}

		}
		
		Collection<AwsInstance> dgwInstances = instances.stream()
			.filter(i -> i.getProduct().equals(HermesProduct.DGW))	
			.sorted()
			.collect(Collectors.toCollection(LinkedHashSet::new));
		
		AwsInstances dgw = new AwsInstances(dgwInstances, 1);
		
		for (Integer i:dgw.getInstanceMap().keySet()) {
			System.out.println(">>>" + i + " - " + dgw.getInstanceMap().get(i));
		}	
		generateScript(dgw);
		
//		for (AwsInstance i:dgwInstances) {
//			System.out.println(i);
//		}
		
		Integer startIndex = 1;
		AwsInstances prodInstances = new AwsInstances(dgwInstances.stream()
				.filter(i -> i.getEnv().equals(HermesEnv.PROD))
				.sorted()
				.collect(Collectors.toList()),
				startIndex);
		startIndex = startIndex + prodInstances.getInstanceMap().size();
		
		AwsInstances preprodInstances = new AwsInstances(dgwInstances.stream()
				.filter(i -> i.getEnv().equals(HermesEnv.PREPROD))
				.sorted()
				.collect(Collectors.toList()),
				startIndex);
		startIndex = startIndex + preprodInstances.getInstanceMap().size();
		
		AwsInstances sitInstances = new AwsInstances(dgwInstances.stream()
				.filter(i -> i.getEnv().equals(HermesEnv.SIT))
				.sorted()
				.collect(Collectors.toList()),
				startIndex);
		startIndex = startIndex + sitInstances.getInstanceMap().size();
		
		AwsInstances uatInstances = new AwsInstances(dgwInstances.stream()
				.filter(i -> i.getEnv().equals(HermesEnv.UAT))
				.sorted()
				.collect(Collectors.toList()),
				startIndex);

		
//		System.out.println("Prod============================================"); 
//		for(Integer index:prodInstances.getInstanceMap().keySet()) {
//			System.out.println(String.format("%s - %s \t\t ssh -t -i ~/.ssh/develop-%s.pem ubuntu@%s", index, 
//					prodInstances.getInstanceMap().get(index).getName(), 
//					prodInstances.getInstanceMap().get(index).getKeyName(),
//					prodInstances.getInstanceMap().get(index).getIpAddress()));
//		}
//		System.out.println("Preprod============================================"); 
//		for(Integer index:preprodInstances.getInstanceMap().keySet()) {
//			System.out.println(String.format("%s - %s \t\t ssh -t -i ~/.ssh/develop-%s.pem ubuntu@%s", index, 
//					preprodInstances.getInstanceMap().get(index).getName(), 
//					preprodInstances.getInstanceMap().get(index).getKeyName(),
//					preprodInstances.getInstanceMap().get(index).getIpAddress()));
//		}
//		System.out.println("SIT============================================"); 
//		for(Integer index:sitInstances.getInstanceMap().keySet()) {
//			System.out.println(String.format("%s - %s \t\t ssh -t -i ~/.ssh/develop-%s.pem ubuntu@%s", index, 
//					sitInstances.getInstanceMap().get(index).getName(), 
//					sitInstances.getInstanceMap().get(index).getKeyName(),
//					sitInstances.getInstanceMap().get(index).getIpAddress()));
//		}
//		System.out.println("UAT============================================"); 
//		for(Integer index:uatInstances.getInstanceMap().keySet()) {
//			System.out.println(String.format("%s - %s \t\t ssh -t -i ~/.ssh/develop-%s.pem ubuntu@%s", index, 
//					uatInstances.getInstanceMap().get(index).getName(), 
//					uatInstances.getInstanceMap().get(index).getKeyName(),
//					uatInstances.getInstanceMap().get(index).getIpAddress()));
//		}
		
	}
	
	protected static void generateScript(AwsInstances instances) {
		List<String> scriptLines = new ArrayList<>();
		scriptLines.add(BASH);

		scriptLines.addAll(buildMenu(instances.getInstanceMap()));
		
		scriptLines.stream().forEach(System.out::println);
		
		
	}
	
	private static Collection<String> buildMenu(SortedMap<Integer, AwsInstance> instanceMap) {
		Collection<String> menuLines = new HashSet<>();
		int colWidth = maxShortNameSize(instanceMap) + 4;
		Iterator<Entry<Integer, AwsInstance>> prodItr = filterMap(instanceMap, HermesEnv.PROD);
		Iterator<Entry<Integer, AwsInstance>> preItr = filterMap(instanceMap, HermesEnv.PREPROD);
		Iterator<Entry<Integer, AwsInstance>> sitItr = filterMap(instanceMap, HermesEnv.SIT);
		Iterator<Entry<Integer, AwsInstance>> uatItr = filterMap(instanceMap, HermesEnv.UAT);

		while (prodItr.hasNext() || preItr.hasNext() || sitItr.hasNext() || uatItr.hasNext()) {
			menuLines.add(String.format("%1$" + colWidth + "s %2$" + colWidth + "s %3$" + colWidth + "s %4$" + colWidth + "s", 
					prodItr.hasNext()? menuText(prodItr.next()):MT,
					preItr.hasNext()? menuText(preItr.next()):MT,
					sitItr.hasNext()? menuText(sitItr.next()):MT,
					uatItr.hasNext()? menuText(uatItr.next()):MT	));
		}
		return menuLines;
	}

	private static String menuText(Entry<Integer, AwsInstance> entry) {
		System.out.println(">>>>>>>>>>>>>>" + entry.getValue().getName());
		return String.format(MENU_FMT, entry.getKey(), shortName(entry.getValue().getName()));
	}

	private static Iterator<Entry<Integer, AwsInstance>> filterMap(SortedMap<Integer, AwsInstance> instanceMap, HermesEnv env) {
		SortedMap<Integer,AwsInstance> filteredMap = new TreeMap<>();
		for(Integer i:instanceMap.keySet()) {
			if (instanceMap.get(i).getEnv().equals(env)) {
				//System.out.println(">>>>>>>>>> putting: " + i + " - " + instanceMap.get(i));
				filteredMap.put(i, instanceMap.get(i)); 
			}
		}
		return filteredMap.entrySet().iterator();
	}

	private static String shortName(String name) {
		return name.replace(DGW_KEY_PREFIX, MT); 
	}
	
	private static int maxShortNameSize(Map<Integer, AwsInstance> instanceMap) {
		int max = 0;
		for(Integer i:instanceMap.keySet()) {
			if (shortName(instanceMap.get(i).getName()).length() > max) {
				max = shortName(instanceMap.get(i).getName()).length();
			}
		}
		return max;
	}
	
	
	
	

}
