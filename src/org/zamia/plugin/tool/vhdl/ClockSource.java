package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;

public class ClockSource {

	private VhdlSignalDeclaration signalDeclaration;

	private ArrayList<ClockSignal> listClockSignal = new ArrayList<ClockSignal>();  
	
	private ArrayList<ClockSourceRead> listReadClockSource = new ArrayList<ClockSourceRead>();  
	
	private ArrayList<ViolationPreservationName> listViolationPreservationName = new ArrayList<ViolationPreservationName>();

	private String entityName;

	private String tag;

	private String architectureName;  
	
	private EdgeE edgePerProject;  
	
	
	public ClockSource(SignalSource signalSource) {
		signalDeclaration = signalSource.getSignalDeclaration();
		entityName = signalSource.getEntityName();
		architectureName = signalSource.getArchitectureName();
	}


	public ArrayList<ClockSignal> getListClockSignal() {
		return listClockSignal;
	}

	public void setListClockSignal(ArrayList<ClockSignal> listClockSignal) {
		this.listClockSignal = listClockSignal;
	}

	public void addClockSignalElement(ClockSignal clockSignal) {
		if (listClockSignal == null) {
			listClockSignal = new ArrayList<ClockSignal>();
		}
		this.listClockSignal.add(clockSignal);
	}

	public ClockSignal getClockSignalElement(int index) {
		return listClockSignal.get(index);
	}

	public void clearListClockSignal() {
		listClockSignal = new ArrayList<>();
	}


	public VhdlSignalDeclaration getSignalDeclaration() {
		return signalDeclaration;
	}

	
	@Override
	public String toString() {
		return signalDeclaration.toString();
	}

	public String getType() {
		return signalDeclaration.getType();
	}

	public ArrayList<ClockSourceRead> getListReadClockSource() {
		return listReadClockSource;
	}

	public void clearListReadClockSource() {
		listReadClockSource = new ArrayList<ClockSourceRead>();
	}


	public void addReadClockSource(ClockSourceRead readClockSource) {
		if (!listReadClockSource.contains(readClockSource)) {
			this.listReadClockSource.add(readClockSource);
		}
	}


	public void addViolationPreservationName(ViolationPreservationName violationPreservationName) {
		if (! listViolationPreservationName.contains(violationPreservationName)) {
			listViolationPreservationName.add(violationPreservationName);
		}
	}
	
	public ArrayList<ViolationPreservationName> getViolationPreservationName() {
		return listViolationPreservationName;
	}


	public boolean checkAffectation() {
		return signalDeclaration.checkAffectation();
	}


	public String getEntity() {
		return entityName;
	}

	public String getArchitecture() {
		return architectureName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClockSource)) {
			return false;
		}
		
		ClockSource clockSource = (ClockSource)obj;
		
		if (!(getSignalDeclaration().equals(clockSource.getSignalDeclaration()))) {
			return false;
		}
		
		return true;
	}


	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public EdgeE getEdgePerProject() {
		return edgePerProject;
	}


	public void setEdgePerProject(EdgeE edgePerProject) {
		this.edgePerProject = edgePerProject;
	}

}
