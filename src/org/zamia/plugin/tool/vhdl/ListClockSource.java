package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;


public class ListClockSource {

	private ArrayList<ClockSource> listClockSource;  
	

	public ListClockSource() {
		listClockSource = new ArrayList<ClockSource>();
	}

	public ArrayList<ClockSource> getListClockSource() {
		return listClockSource;
	}

	public String add(ClockSource clockSource, ClockSignal clockSignalItem) {
		boolean find = false;
		int tag = 0; 

		if (!listClockSource.isEmpty()) {
			for (ClockSource clockSourceItem : listClockSource) {
				if (clockSourceItem.getSignalDeclaration().equals(clockSource.getSignalDeclaration())) {
					find = true;
//					if (!clockSourceItem.getListClockSignal().contains(clockSignalItem)) {
						clockSourceItem.addClockSignalElement(clockSignalItem);
//					}
						return clockSourceItem.getTag();
				}
			}
		}
		if (listClockSource.isEmpty() || !find) {
			clockSource.addClockSignalElement(clockSignalItem);
			if (clockSource.getTag() == null) {
				if (listClockSource.isEmpty()) {
					clockSource.setTag(NodeType.CLOCK_SOURCE.toString()+tag);
				} else {
					tag = tag+listClockSource.size();
					clockSource.setTag(NodeType.CLOCK_SOURCE.toString()+tag);
				}
			}
			listClockSource.add(clockSource);
			return clockSource.getTag();
		}
		
		return NodeType.CLOCK_SOURCE.toString()+tag;
	}

	public void dump() {
		for (ClockSource clockSource : listClockSource) {
			System.out.println("-------------------");
			System.out.println("CLOCK SOURCE "+clockSource.getSignalDeclaration().toString()+"  LOCATION  "+ clockSource.getSignalDeclaration().getLocation()+ " TYPE "+clockSource.getSignalDeclaration().getClass().getSimpleName());
			for (ClockSignal clockSignal : clockSource.getListClockSignal()) {
				System.out.println("CLOCK SIGNAL "+clockSignal.toString()+" LOCATION "+clockSignal.getLocation());
			}
		}
		
	}

	public String sizeString() {
		return String.valueOf(listClockSource.size());
	}
	
	
}
