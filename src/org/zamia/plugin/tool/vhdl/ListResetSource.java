package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;


public class ListResetSource {

	private ArrayList<ResetSource> listResetSource;  
	
	public ListResetSource() {
		listResetSource = new ArrayList<ResetSource>();
	}

	public ArrayList<ResetSource> getListResetSource() {
		return listResetSource;
	}

	public String add(ResetSource resetSource, ResetSignal resetSignalItem) {
		boolean find = false;
		int tag = 0; 
		
		if (!listResetSource.isEmpty()) {
			for (ResetSource resetSourceItem : listResetSource) {
				if (resetSourceItem.getSignalDeclaration().equals(resetSource.getSignalDeclaration())) {
					find = true;
//					if (!resetSourceItem.getListResetSignal().contains(resetSignalItem)) {
						resetSourceItem.addResetSignalElement(resetSignalItem);
//					}
						return resetSourceItem.getTag();
				}
			}
		}
		if (listResetSource.isEmpty() || !find) {
			resetSource.addResetSignalElement(resetSignalItem);
			if (resetSource.getTag() == null) {
				if (listResetSource.isEmpty()) {
					resetSource.setTag(NodeType.CLOCK_SOURCE.toString()+tag);
				} else {
					tag = tag+listResetSource.size();
					resetSource.setTag(NodeType.CLOCK_SOURCE.toString()+tag);
				}
			}

			listResetSource.add(resetSource);
			return resetSource.getTag();
		}
		return NodeType.RESET_SOURCE.toString()+tag;
	}

	public void dump() {
		System.out.println("-------------------listResetSource");
		for (ResetSource resetSource : listResetSource) {
			System.out.println("-------------------");
			System.out.println("RESET SOURCE "+resetSource.getSignalDeclaration().toString());
			System.out.println("  location  "+ resetSource.getSignalDeclaration().getLocation());
			for (ResetSignal resetSignal : resetSource.getListResetSignal()) {
				System.out.println("RESET SIGNAL "+resetSignal.toString()+" location "+resetSignal.getLocation());
			}
		}
		
	}

	public String sizeString() {
		return String.valueOf(listResetSource.size());
	}
	
	
}
