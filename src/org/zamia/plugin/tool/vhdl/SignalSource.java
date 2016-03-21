package org.zamia.plugin.tool.vhdl;

import java.util.List;

import org.zamia.SourceLocation;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;



public class SignalSource {
	
	private VhdlSignalDeclaration signalDeclaration;
	
	private HdlEntity hdlEntity = null;
	
	private HdlArchitecture hdlArchitecture = null;

	private Entity entity = null;
	
	private Architecture architecture = null;
	
	private ClockSource clockSource;

	public SignalSource(VhdlSignalDeclaration _signalDeclaration, HdlEntity _hdlEntity,
			HdlArchitecture _hdlArchitecture) {
		signalDeclaration = _signalDeclaration;
		hdlEntity = _hdlEntity;
		hdlArchitecture = _hdlArchitecture;
		entity = hdlEntity.getEntity();
		architecture = hdlArchitecture.getArchitecture();
	}


	public VhdlSignalDeclaration getSignalDeclaration() {
		return signalDeclaration;
	}

	public String getEntityName() {
		return entity.getId();
	}

	public String getArchitectureName() {
		return architecture.getId();
	}

	public SourceLocation getLocation() {
		return signalDeclaration.getLocation();
	}

	public List<String> getListOperand() {
		return signalDeclaration.getListOperand();
	}
	
	public HdlEntity getHdlEntity() {
		return hdlEntity;
	}
	
	public HdlArchitecture getHdlArchitecture() {
		return hdlArchitecture;
	}
	
	@Override
	public String toString() {
		return signalDeclaration.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof SignalSource)) {
			return false;
		}
		
		SignalSource signalSource = (SignalSource)obj;
		
		if (!(getSignalDeclaration().getLocation().fSF.equals(signalSource.getSignalDeclaration().getLocation().fSF))) {
			return false;
		}
		
		if (!(toString().equalsIgnoreCase(signalSource.toString()))) {
			return false;
		}
		
		return true;
	}
	
	public ClockSource getClockSource() {
		return clockSource;
	}

	public void addClockSourceRegister(ClockSource clockSourceRegister) {
		clockSource = clockSourceRegister;
		
	}


	public boolean isSameClockSource(ClockSource _clockSource) {
		if (clockSource == null) {System.out.println("clockSource == null");return false;}
		return clockSource.getTag().equalsIgnoreCase(_clockSource.getTag());
	}


	public String getType() {
		return signalDeclaration.getType();
	}
}
