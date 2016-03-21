package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;

import org.zamia.SourceLocation;
import org.zamia.vhdl.ast.Name;
import org.zamia.vhdl.ast.NameExtension;
import org.zamia.vhdl.ast.Operation;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationLiteral;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationLogic.LogicOp;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.VHDLNode;
import org.zamia.vhdl.ast.Waveform;


public class Process {

	private SequentialProcess sequentialProcess; 
	
	private ArrayList<ClockSignal> listClockSignal = new ArrayList<ClockSignal>();

	private ArrayList<Sensitivity> listSensitivity = new ArrayList<Sensitivity>();

	private int endLine = 0;

	private ListRegisterInput listInput = new ListRegisterInput();

	public Process(SequentialProcess _sequentialProcess, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		sequentialProcess = _sequentialProcess;
		setListSensitivity(hdlEntity, hdlArchitecture);
		searchEndLineInProcess();
	}
	
	public ArrayList<ClockSignal> getListClockSignal() {
		return listClockSignal;
	}

	public void setListClockSignal(ArrayList<ClockSignal> listClockSignal) {
		this.listClockSignal = listClockSignal;
	}

	public int getNumberOfClockSignalElement() {
		return listClockSignal.size();
	}
	
	public ClockSignal getClockSignalElement(int index) {
		return listClockSignal.get(index);
	}
	
	public void clearClockSignal() {
		listClockSignal = new ArrayList<>();
	}
	
	public void addClockSignalElement(ClockSignal clockSignalElement) {
		if (listClockSignal == null) {
			listClockSignal = new ArrayList<ClockSignal>();
		}

		listClockSignal.add(clockSignalElement);
	}
	
	public SequentialProcess getSequentialProcess() {
		return sequentialProcess;
	}

	public void setSequentialProcess(SequentialProcess sequentialProcess) {
		this.sequentialProcess = sequentialProcess;
	}

	public String getLabel() {
		return sequentialProcess != null ? (sequentialProcess.getLabel() != null ? sequentialProcess.getLabel() : "unnamed") : "unnamed";
	}

	public SourceLocation getLocation() {
		return sequentialProcess != null ? sequentialProcess.getLocation() : null;
	}

	public int getNumChildren() {
		return sequentialProcess != null ? sequentialProcess.getNumChildren() : 0;
	}

	public VHDLNode getChild(int i) {
		return sequentialProcess != null ? sequentialProcess.getChild(i) : null;
	}

	public boolean isSynchronous() {
		return !listClockSignal.isEmpty();
	}

	public void setClockSignalElement(int index, ClockSignal clockSignal) {
		listClockSignal.set(index, clockSignal);
		
	}

	public void searchUseSignalNameInProcess(String signalName) {
		SequentialProcess sequentialProcess = getSequentialProcess();
		VHDLNode child = sequentialProcess.getChild(0);
		nodeToLogger(child, signalName);
		
	}

	private void nodeToLogger(VHDLNode node, String signalName) {
		if (node==null){
//			System.out.println("CHILD NULL");
		}else{
			if (node.toString().contains(signalName)){
				System.out.println(node.toString() +" ["+node.getClass().getSimpleName()+"]");
			}
			
			int numChildren = node.getNumChildren();
			if (node instanceof SequentialIf) {

				for (int i = 0; i < numChildren; i++) {
					VHDLNode subChild = node.getChild(i);
					
					if (subChild instanceof OperationName) {
						if (subChild.toString().contains(signalName)) {
							Name name = ((OperationName)subChild).getName();
							NameExtension numExtensions = name.getExtension(0);
							VHDLNode range = numExtensions.getChild(0);
							if (range.toString().equalsIgnoreCase(signalName)) {
								System.out.println("READ "+subChild);
							}
						}
					} else if (subChild instanceof OperationCompare) {
						if (subChild.toString().contains(signalName)) {
							int numChildOpComp = subChild.getNumChildren();
							for (int j = 0; j < numChildOpComp; j++) {
								VHDLNode subChildOpComp = subChild.getChild(j);
								if (subChildOpComp instanceof OperationName) {
									if (subChildOpComp.toString().equalsIgnoreCase(signalName)) {
										System.out.println("READ "+subChildOpComp.toString());
									}
								} else if (subChildOpComp instanceof OperationLiteral) {
								} else {
									System.out.println("OPERATION COMPARE "+subChildOpComp.toString() +" ["+subChildOpComp.getClass().getSimpleName()+"]");
								}
								
							}
						}
					} else {
						nodeToLogger(subChild, signalName);
					}
					
				}
				
				
			} else if (node instanceof SequentialSignalAssignment){
				if (node.toString().contains(signalName)){
					int numAssignment = node.getNumChildren();
					for (int i = 0; i < numAssignment; i++) {
						VHDLNode childAssignment = node.getChild(i);
						if (childAssignment instanceof Waveform) {
							VHDLNode childWaveform = ((Waveform)childAssignment).getElement(0).getChild(0);
							if (childWaveform instanceof OperationLogic) {
								OperationLogic opLogic = (OperationLogic)childWaveform;
								Operation operandA = opLogic.getOperandA();
								Operation operandB = opLogic.getOperandB();
								if ((operandA != null && operandA.toString().equalsIgnoreCase(signalName)) || (operandB != null && operandB.toString().equalsIgnoreCase(signalName))) {
									LogicOp op = opLogic.getOp();
									StringBuffer buf = new StringBuffer("");
									if (operandB == null) {
										buf.append(op+" ");
									}
									buf.append(operandA);
									if (operandB != null) {
										buf.append(" "+op+" ");
										buf.append(operandB);
									}
									
									System.out.println("READ  OperationLogic "+((SequentialSignalAssignment)node).getTarget().getName().toString() + " <= "+ buf.toString());
								}
							} else if (childWaveform instanceof OperationName) {
								System.out.println("READ  OperationName "+((SequentialSignalAssignment)node).getTarget().getName().toString() + " <= "+ ((SequentialSignalAssignment)node).getValue());
							
							} else {
								System.out.println("childWaveform instanceof OTHER  "+childWaveform.getChild(0));

							}
						}
					}
				}
			} else {
				for (int i = 0; i < numChildren; i++) {
					VHDLNode subChild = node.getChild(i);
					nodeToLogger(subChild, signalName);
				}
				
			}
		}

	}

	public boolean hasLabel() {
		if (sequentialProcess == null) {
			return false;
		}
		return sequentialProcess.getLabel() != null;
	}

	public Integer getNbLine() {
		if (sequentialProcess == null) {
			return 0;
		}
		
		return getEndLine() - sequentialProcess.getStartLine() + 1;
	}

	public boolean hasSynchronousReset() {
		for (ClockSignal clockSignal : listClockSignal) {
			if (clockSignal.hasSynchronousReset()) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Sensitivity> getListSensitivity() {
		return listSensitivity;
	}

	private void setListSensitivity(HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		int numChildren = sequentialProcess.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = sequentialProcess.getChild(i);
			if (child instanceof Name) {
				listSensitivity.add(new Sensitivity((Name)child, hdlEntity, hdlArchitecture));
			}
		}
	}

	public int getEndLine() {
		return endLine;
	}

	private void searchEndLineInProcess() {
		int numChildren = sequentialProcess.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			searchEndLineInChild(sequentialProcess.getChild(i));
		}
	}

	private void searchEndLineInChild(VHDLNode child) {
		if (child == null) { return;}
		
		if (child.getLocation().fLine > endLine) { endLine = child.getLocation().fLine;}
		
		int numChildren = child.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			searchEndLineInChild(child.getChild(i));
		}
		
	}

	
	public ArrayList<RegisterInput> getListInput() {
		return listInput.getListRegisterInput();
	}

	public int addInput(Input input) {
		return listInput.addRegisterInput(input);
	}

	public ClockSource isSignalRegister(SignalSource signalSource) {
		if (! isSynchronous() ) {
			return null;
		}
		for (ClockSignal clockSignal : listClockSignal) {
			ClockSource clockSourceRegister = clockSignal.isSignalRegister(signalSource);
			if (clockSourceRegister != null) {
				return clockSourceRegister;
			}
		}
		return null;
	}


}
