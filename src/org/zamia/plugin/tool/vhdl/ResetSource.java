package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;
import java.util.List;

public class ResetSource {

	private VhdlSignalDeclaration signalDeclaration;

	private List<ResetSignal> listResetSignal = new ArrayList<ResetSignal>();  
	
	private List<ResetSourceRead> listReadResetSource = new ArrayList<ResetSourceRead>();

	private List<ViolationPreservationName> listViolationPreservationName = new ArrayList<ViolationPreservationName>();  
	
	private String entityName;

	private String architectureName;  

	private String tag;

	private LevelE levelPerProject;  
	


	public ResetSource(SignalSource signalSource) {
		signalDeclaration = signalSource.getSignalDeclaration();
		entityName = signalSource.getEntityName();
		architectureName = signalSource.getArchitectureName();
	}

	public void clearListReadReadSource() {
		listReadResetSource = new ArrayList<ResetSourceRead>();
	}

	public List<ResetSourceRead> getListReadResetSource() {
		return listReadResetSource;
	}

	public boolean addReadResetSource(ResetSourceRead readResetSource) {
		if (!listReadResetSource.contains(readResetSource)) {
			this.listReadResetSource.add(readResetSource);
			return true;
		}
		return false;
	}

	public List<ResetSignal> getListResetSignal() {
		return listResetSignal;
	}

	public void setListResetSignal(ArrayList<ResetSignal> listResetSignal) {
		this.listResetSignal = listResetSignal;
	}

	public void addResetSignalElement(ResetSignal ResetSignal) {
		if (listResetSignal == null) {
			listResetSignal = new ArrayList<ResetSignal>();
		}


		this.listResetSignal.add(ResetSignal);
	}

	public ResetSignal getResetSignalElement(int index) {
		return listResetSignal.get(index);
	}

	public void clearListResetSignal() {
		listResetSignal = new ArrayList<>();
	}


	public VhdlSignalDeclaration getSignalDeclaration() {
		return signalDeclaration;
	}

	@Override
	public String toString() {
		return signalDeclaration.toString();
	}

	public void addViolationPreservationName(ViolationPreservationName violationPreservationName) {
		listViolationPreservationName.add(violationPreservationName);
	}
	
	public List<ViolationPreservationName> getViolationPreservationName() {
		return listViolationPreservationName ;
	}

	public String getEntity() {
		return entityName;
	}

	public String getArchitecture() {
		return architectureName;
	}

	public String getType() {
		return signalDeclaration.getType();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ResetSource)) {
			return false;
		}
		
		ResetSource resetSource = (ResetSource)obj;
		
		if (!(getSignalDeclaration().equals(resetSource.getSignalDeclaration()))) {
			return false;
		}
		
		return true;
	}


	public LevelE getLevelPerProject() {
		return levelPerProject;
	}

	public void setLevelPerProject(LevelE levelPerProject) {
		this.levelPerProject = levelPerProject;
	}


	public String getTag() {
		return tag;
	}


	public void setTag(String _tag) {
		tag =_tag;
	}

}
