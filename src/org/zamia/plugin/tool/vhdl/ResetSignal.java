package org.zamia.plugin.tool.vhdl;

import org.zamia.SourceLocation;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.VHDLNode;



public class ResetSignal extends Signal {
	
	private VHDLNode node;
	
	private SequenceOfStatements sequenceOfStatementsChild = null;  // pour recup l'init des registres

	private ResetSource resetSource;
	
	private LevelE level;
	
	private String fileName;
	
	private Entity entity;
	
	private Architecture architecture;
	
	private SequentialProcess process;
	
	private VHDLNode declarationType;

	private ClockSignal clockSignal;

	public ResetSignal(VHDLNode node, String levelS, String localPath, HdlEntity _entity, HdlArchitecture _architecture,
			SequentialProcess _process, ClockSignal _clockSignal, VHDLNode clockSequentialIf) {
		this.node = node;
		if (levelS.equalsIgnoreCase("0")) {
			level = LevelE.LOW;
		} else {
			level = LevelE.HIGH;
		}
		fileName = localPath;
		entity = _entity.getEntity();
		process = _process;
		architecture = _architecture.getArchitecture();
		clockSignal = _clockSignal;
		setSequenceOfStatementsChild(clockSequentialIf);
		setType(_entity, _architecture);
	}

	public SequenceOfStatements getSequenceOfStatementsChild() {
		return sequenceOfStatementsChild;
	}

	public void setSequenceOfStatementsChild(VHDLNode clockSequentialIf) {
		if (clockSequentialIf == null) { return;}

		int numChildren = clockSequentialIf.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = clockSequentialIf.getChild(i);
			if (child instanceof SequenceOfStatements) {
				this.sequenceOfStatementsChild = (SequenceOfStatements) child;
			}
		}
	}

	public VHDLNode getNode() {
		return node;
	}

	@Override
	public String toString() {
		return node.toString();
	}

	public SourceLocation getLocation() {
		return node.getLocation();
	}

	public ResetSource getResetSource() {
		return resetSource;
	}

	public void setResetSource(ResetSource resetSource) {
		this.resetSource = resetSource;
	}
	
	public LevelE getLevel() {
		return level;
	}
	
	public String getFileName() {
		return fileName;
	}

	public Entity getEntity() {
		return entity;
	}

	public SequentialProcess getProcess() {
		return process;
	}

	public VHDLNode getDeclarationType() {
		return declarationType;
	}

	public Architecture getArchitecture() {
		return architecture;
	}

	public void setDeclarationType(HdlEntity hdlEntity,
			HdlArchitecture hdlArchitecture) {
			VHDLNode _declarationType = hdlEntity.getInterfaceDeclaration(this.toString());
			if (_declarationType != null) {
				declarationType = _declarationType;
				return;
			} 
			_declarationType = hdlArchitecture.getSignalDeclaration(this.toString());
			declarationType = _declarationType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ResetSignal)) {
			return false;
		}
		
		ResetSignal resetSignal = (ResetSignal)obj;
		
		if (!(getLocation().fSF.equals(resetSignal.getLocation().fSF))) {
			return false;
		}
		
		if (!(toString().equalsIgnoreCase(resetSignal.toString()))) {
			return false;
		}
		
		return true;
	}

	public ClockSignal getClockSignal() {
		return clockSignal;
	}

	public ClockSource isSignalRegister(SignalSource signalSource) {
		
		for (RegisterInput register : listRegister.getListRegisterInput()) {
			if (register.isSameSignal(signalSource)) {
				return register.getClockSource();
			}
		}
		return null;
	}

	@Override
	public VHDLNode getVhdlNode() {
		return node;
	}
	
}
