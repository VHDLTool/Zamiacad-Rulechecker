package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;

import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationConcat;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.VHDLNode;
import org.zamia.vhdl.ast.Waveform;
import org.zamia.vhdl.ast.WaveformElement;

public class Register extends RegisterInput {
	SequentialSignalAssignment sequentialSignal;
	
	public Register(SequentialSignalAssignment _sequentialSignal, HdlEntity hdlEntity, 
			HdlArchitecture hdlArchitecture, Signal signal, ClockSource _clockSource,
			String _tagS, int _tagNum) {
		sequentialSignal = _sequentialSignal;
		clockSource = _clockSource;
		clockSignal = (ClockSignal) signal;
		location = _sequentialSignal.getLocation();
		name = _sequentialSignal.getTarget().getName();
		tagS = _tagS;
		tagNum = _tagNum;
		setType(hdlEntity, hdlArchitecture);
		if (type == RegisterTypeE.RECORD && (!toString().equalsIgnoreCase(getRecordName()))) {
			type = RegisterTypeE.RECORD_PART;
			subName = getRecordName();
		}
		setOperand(_sequentialSignal);
	}
	
	@Override
	public VHDLNode getVhdlNode() {
		return sequentialSignal;
	}

	public String getTag() {
		return tagS + tagNum;
	}
	
	private void setOperand(SequentialSignalAssignment sequentialSignal) {
		listOperand = new ArrayList<String>();
		Waveform value = sequentialSignal.getValue();
		WaveformElement element = value.getElement(0);
		searchInOp(element);
	}

	
	private void searchInOp(VHDLNode child) {
		if (child == null) { return; }
		int numChildren2 = child.getNumChildren();
		for (int j = 0; j < numChildren2; j++) {
			VHDLNode child2 = child.getChild(j);
			if (child2 instanceof OperationCompare) {
				searchInOp(child2);
			} else if (child2 instanceof OperationLogic) {
				searchInOp(child2);
			}else  if (child2 instanceof OperationName) {
				if (!child2.toString().equalsIgnoreCase(toString())) {
					listOperand.add(child2.toString());
				}
			} else if (child2 instanceof OperationConcat) {
				searchInOp(child2);
			}

		}
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) { return false;}
		
		if (! (obj instanceof Register)) { return false;}
		
		Register register = (Register) obj;

		if (! toString().equalsIgnoreCase(register.toString())) { return false;}
		
		return true;
	}

	

}
