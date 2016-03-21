package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;
import java.util.List;

import org.zamia.SourceLocation;
import org.zamia.vhdl.ast.AssociationElement;
import org.zamia.vhdl.ast.ConditionalSignalAssignment;
import org.zamia.vhdl.ast.ConditionalWaveform;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationConcat;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.VHDLNode;
import org.zamia.vhdl.ast.Waveform;
import org.zamia.vhdl.ast.WaveformElement;

public class VhdlSignalDeclaration {

	VHDLNode vhdlNode;
	
	private List<String> listOperand = new ArrayList<String>();
	
	public VhdlSignalDeclaration(VHDLNode _vhdlNode) {
		vhdlNode = _vhdlNode;
	}
	
	@Override
	public String toString() {
		if (vhdlNode instanceof AssociationElement) {
			// black box
			return ((AssociationElement)vhdlNode).getActualPart().toString();
		} else if (vhdlNode instanceof SequentialSignalAssignment){ 
			return ((SequentialSignalAssignment)vhdlNode).getTarget().getName().toString();
		} else if (vhdlNode instanceof InterfaceDeclaration){
			// port in du TOP or port out d'une black box
			return ((InterfaceDeclaration)vhdlNode).getId();
		} else if (vhdlNode instanceof ConditionalSignalAssignment){
			// affectation
			return ((ConditionalSignalAssignment)vhdlNode).getTarget().getName().toString();
		} else {
			return vhdlNode.toString() + " TYPE "+ vhdlNode.getClass().getSimpleName();
		}	}

	public String getType() {
		if (vhdlNode instanceof AssociationElement) {
			// black box
			return "Instance Output";
		} else if (vhdlNode instanceof SequentialSignalAssignment){ 
			return "Signal Assignment";
		} else if (vhdlNode instanceof InterfaceDeclaration){
			// port in du TOP
			return "Input Port";
		} else if (vhdlNode instanceof ConditionalSignalAssignment){
			// affectation
			return "Signal Assignment";
		} else {
			return "NA";
		}
	}


	public boolean checkAffectation() {
		if (vhdlNode instanceof ConditionalSignalAssignment ||
				vhdlNode instanceof SequentialSignalAssignment) {
			return true;
		}
		return false;
	}

	public SourceLocation getLocation() {
		return vhdlNode.getLocation();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) 
			return false;
		
		if (!(obj instanceof VhdlSignalDeclaration))
			return false;
		
		VhdlSignalDeclaration signal = (VhdlSignalDeclaration) obj;
		
		if (!(getLocation().equals(signal.getLocation())))
			return false;

		if (!(toString().equals(signal.toString())))
			return false;

		return true;
	}
	
	public VHDLNode getVhdlNode() {
		return vhdlNode;
	}

	public List<String> getListOperand() {
		if (vhdlNode instanceof SequentialSignalAssignment){ 
			SequentialSignalAssignment assignment = (SequentialSignalAssignment) vhdlNode;
			Waveform value = assignment.getValue();
			WaveformElement element = value.getElement(0);
			searchInOp(element);

			return listOperand;
		} else if (vhdlNode instanceof ConditionalSignalAssignment){
			ConditionalSignalAssignment assignment = (ConditionalSignalAssignment) vhdlNode;
			ConditionalWaveform conditionalWF = (ConditionalWaveform)assignment.getChild(0);
			VHDLNode child = conditionalWF.getChild(0);
			if (child != null) {
				searchInOp(child);
			}
			Waveform waveform = (Waveform)conditionalWF.getChild(1);
			WaveformElement element = waveform.getElement(0);
			searchInOp(element);
			if (assignment.getNumChildren() == 3) {
				VHDLNode child2 = assignment.getChild(1);
				if (child2 != null) {
					ConditionalWaveform conditionalWF2 = (ConditionalWaveform) child2;
					Waveform waveform2 = (Waveform)conditionalWF2.getChild(1);
					WaveformElement element2 = waveform2.getElement(0);
					searchInOp(element2);
				}
			}
			return listOperand;
		} else {
		}
		
		return new ArrayList<String>();
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
				listOperand.add(child2.toString());
			} else if (child2 instanceof OperationConcat) {
				searchInOp(child2);
			}
	
		}
	}

	
}
