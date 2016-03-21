package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;

import org.zamia.SourceLocation;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.VHDLNode;

public class ClockSignal extends Signal {

	private VHDLNode sequenceOfStatementsParent;  // pour recup le reset

	private SequenceOfStatements sequenceOfStatementsChild = null;  // pour recup les registre

	private VHDLNode node; 
	
	private ArrayList<ResetSignal> listResetSignal = new ArrayList<ResetSignal>();  // pour recup le reset
	
	private EdgeE edge;
	
	private ClockSource clockSource;
	
	private String fileName;
	
	private Entity entity;
	
	private Architecture architecture;
	
	private SequentialProcess process;
	
	private VHDLNode declarationType;
	
	
	public ClockSignal(VHDLNode _node, EdgeE _edge, String localPath, HdlEntity _entity, HdlArchitecture _architecture,
			SequentialProcess _process, VHDLNode clockSequentialIf) {
		node = _node;
		edge = _edge;
		fileName = localPath;
		entity = _entity.getEntity();
		process = _process;
		architecture = _architecture.getArchitecture();
		setSequenceOfStatementsChild(clockSequentialIf);
		setType(_entity, _architecture);
	}

	public VHDLNode getSequenceOfStatementsParent() {
		return sequenceOfStatementsParent;
	}

	public void setSequenceOfStatementsParent(VHDLNode sequenceOfStatementsParent) {
		this.sequenceOfStatementsParent = sequenceOfStatementsParent;
	}

	public VHDLNode getNode() {
		return node;
	}

	public void setNode(VHDLNode node) {
		this.node = node;
	}

	public String toString() {
		return node != null ? (node.toString().contains("'EVENT")? node.toString().replace("'EVENT", ""):node.toString()) : "null";
	}

	public SourceLocation getLocation() {
		return node != null ? node.getLocation() : (SourceLocation)null;
	}

	public ArrayList<ResetSignal> getListResetSignal() {
		return listResetSignal;
	}

	public void setListResetSignal(ArrayList<ResetSignal> listResetSignal) {
		this.listResetSignal = listResetSignal;
	}

	public void addResetSignalElement(ResetSignal resetSignal) {
		if (listResetSignal == null) {
			listResetSignal = new ArrayList<ResetSignal>();
		}


		this.listResetSignal.add(resetSignal);
	}

	public void addResetSignalElement(int index, ResetSignal resetSignal) {
		if (listResetSignal == null) {
			listResetSignal = new ArrayList<ResetSignal>();
		}

		listResetSignal.add(index, resetSignal);
	}

	public ResetSignal getResetSignalElement(int index) {
		return listResetSignal.get(index);
	}

	public void clearListResetSignal() {
		listResetSignal = new ArrayList<>();
	}

	public EdgeE getEdge() {
		return edge;
	}

	public ClockSource getClockSource() {
		return clockSource;
	}

	public void setClockSource(ClockSource clockSource) {
		this.clockSource = clockSource;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean hasSynchronousReset() {
		if (listResetSignal == null) {
			return false;
		}
		return !listResetSignal.isEmpty();
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
		if (!(obj instanceof ClockSignal)) {
			return false;
		}
		
		ClockSignal clocSignal = (ClockSignal)obj;
		
		if (!(getLocation().equals(clocSignal.getLocation()))) {
			return false;
		}
		
		if (!(toString().equalsIgnoreCase(clocSignal.toString()))) {
			return false;
		}
		
		return true;
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

	public ClockSource isSignalRegister(SignalSource signalSource) {
		for (RegisterInput register : listRegister.getListRegisterInput()) {
			if (register.isSameSignal(signalSource)) {
				return register.getClockSource();
			}
		}
		if (! hasSynchronousReset()) {
			return null;
		}

		for (ResetSignal resetSignal : listResetSignal) {
			ClockSource clockSourceRegister = resetSignal.isSignalRegister(signalSource);
			if (clockSourceRegister != null) {
				return clockSourceRegister;
			}
		}

		return null;
	}

	public boolean registerNotInitialized() {
		
		if (!hasSynchronousReset()) {System.out.println("!hasSynchronousReset()"); return true;}

		for (ResetSignal resetSignal : listResetSignal) {
			if (!resetSignal.getListRegister().isEmpty()) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public VHDLNode getVhdlNode() {
		return node;
	}
}
