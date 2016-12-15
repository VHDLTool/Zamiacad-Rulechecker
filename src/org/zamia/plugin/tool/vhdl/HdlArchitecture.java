package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;
import java.util.List;

import org.zamia.ZamiaLogger;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.vhdl.ast.AliasDeclaration;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.AssociationElement;
import org.zamia.vhdl.ast.AssociationList;
import org.zamia.vhdl.ast.ComponentDeclaration;
import org.zamia.vhdl.ast.ComponentInstantiation;
import org.zamia.vhdl.ast.ConcurrentProcedureCall;
import org.zamia.vhdl.ast.ConditionalSignalAssignment;
import org.zamia.vhdl.ast.ConditionalWaveform;
import org.zamia.vhdl.ast.ConfigurationSpecification;
import org.zamia.vhdl.ast.ConstantDeclaration;
import org.zamia.vhdl.ast.Context;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.InterfaceList;
import org.zamia.vhdl.ast.Name;
import org.zamia.vhdl.ast.Operation;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationLiteral;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationMath;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.Range;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialCase;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.SequentialVariableAssignment;
import org.zamia.vhdl.ast.SignalDeclaration;
import org.zamia.vhdl.ast.SubProgram;
import org.zamia.vhdl.ast.Target;
import org.zamia.vhdl.ast.TypeDeclaration;
import org.zamia.vhdl.ast.VHDLNode;
import org.zamia.vhdl.ast.VariableDeclaration;
import org.zamia.vhdl.ast.Waveform;
import org.zamia.vhdl.ast.WaveformElement;

public class HdlArchitecture {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	private Architecture architecture;

	private List<Process> listProcess = new ArrayList<Process>();

	private List<HdlComponentInstantiation> listComponent = new ArrayList<HdlComponentInstantiation>();

	private List<HdlSignalAssignment> listSignalAssignment = new ArrayList<HdlSignalAssignment>();

	private String entityName;

	private String id;
	
	private HdlEntity hdlEntity;

	public HdlArchitecture(Architecture _architecture, HdlEntity _hdlEntity) {
		architecture = _architecture;
		entityName = _hdlEntity.getEntity().getId();
		id = architecture.getId();
		setListComponent(_hdlEntity);
		try {
			hdlEntity = (HdlEntity) _hdlEntity.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Architecture getArchitecture() {
		return architecture;
	}

	public String getEntityName() {
		return entityName;
	}

	public List<Process> getListProcess() {
		return listProcess;
	}

	public void setListProcess(ArrayList<Process> listProcess) {
		this.listProcess = listProcess;
	}

	private void setListComponent(HdlEntity hdlEntity) {

		listComponent = new ArrayList<HdlComponentInstantiation>();
		int numChildren = architecture.getNumChildren();
		VHDLNode child;
		for (int i = 0; i < numChildren; i++) {
			child = architecture.getChild(i);
			if (child instanceof ComponentInstantiation) {
				HdlComponentInstantiation hdlComponentInstantiation = new HdlComponentInstantiation((ComponentInstantiation) child, this);
				hdlComponentInstantiation.setComponentDeclaration(architecture);
				listComponent.add(hdlComponentInstantiation);
				hdlComponentInstantiation.addUseInEntity(hdlEntity);
			}
		}
	
	}

	public List<HdlComponentInstantiation> getListComponent() {
		return listComponent;
	}

	public void setListSignalAssignment(
			ArrayList<HdlSignalAssignment> listSignalAssignment) {
		this.listSignalAssignment = listSignalAssignment;
	}

	public List<HdlSignalAssignment> getListSignalAssignment() {
		return listSignalAssignment;
	}

	public Process getHdlProcess(SequentialProcess process) {
		if(listProcess == null || listProcess.isEmpty()) {
			return null;
		}
		for (Process processItem : listProcess) {
			if (processItem.getSequentialProcess().equals(process)) {
				return processItem;
			}
		}
		return null;
	}

	public SignalDeclaration getSignalDeclaration(String signalName) {
		int numChildren = architecture.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = architecture.getChild(i);
			if (child instanceof SignalDeclaration) {
				SignalDeclaration signalDeclaration = (SignalDeclaration)child;
				if (signalDeclaration.getId().trim().equalsIgnoreCase(signalName)) {
					return signalDeclaration;
				}
			}
		}

		return null;
	}

	public boolean isOutputComponent(String componentName, int numOutput) {
		int numPort = -1;

		int numComponent = architecture.getNumChildren();
		for (int i = 0; i < numComponent; i++) {
			VHDLNode component = architecture.getChild(i);
			if (component instanceof ComponentDeclaration) {
				if (((ComponentDeclaration)component).getId().trim().equalsIgnoreCase(componentName)) {
					int numInterfaceList = component.getNumChildren();
					for (int j = 0; j < numInterfaceList; j++) {
						VHDLNode interfaceList = component.getChild(j);
						if (interfaceList instanceof InterfaceList) {
							int numInterfaceDeclaration = interfaceList.getNumChildren();
							for (int k = 0; k < numInterfaceDeclaration; k++) {
								VHDLNode interfaceDeclaration = interfaceList.getChild(k);
								if (interfaceDeclaration instanceof InterfaceDeclaration) {
									VHDLNode typeDefSubType = interfaceDeclaration.getChild(0);
									VHDLNode name = typeDefSubType.getChild(0);
									int numExtension = name.getNumChildren();
									if (numExtension == 0) {
										numPort++;
									} else {
										VHDLNode child = name.getChild(0);
										if (child instanceof Range) {
											Range range = (Range)child;
											Integer opLiteral1 = Integer.parseInt(range.getRight().toString());
											Integer opLiteral2 = Integer.parseInt(range.getLeft().toString());
											if (opLiteral1 > opLiteral2) {
												numPort += (opLiteral1 - opLiteral2 + 1);
											} else {
												numPort += (opLiteral2 - opLiteral1 + 1);
											}
										}
									}
									if (numOutput <= numPort) {
										OIDir dir = ((InterfaceDeclaration) interfaceDeclaration).getDir();
										return (dir == OIDir.OUT || dir == OIDir.INOUT);
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public List<VHDLNode> searchOrigin(String idPort) {
		List<VHDLNode> listResult = new ArrayList<VHDLNode>();
		// cas affectation
		int numChildren = getArchitecture().getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = getArchitecture().getChild(i);
			if (child instanceof ConditionalSignalAssignment) {
				if (child.toString().contains(idPort)) {
					if (((ConditionalSignalAssignment)child).getTarget().getName().toString().trim().equalsIgnoreCase(idPort)) {
						listResult.add(child);
					}
				}
			} else if (child instanceof SequentialProcess) {
				int numSequence = child.getNumChildren();
				for (int j = 0; j < numSequence; j++) {
					VHDLNode sequence = child.getChild(j);
					if (sequence instanceof SequenceOfStatements) {
						listResult.addAll(searchSequentialSignalAssignment(sequence, idPort));
					}
				}
				//			} else if (child instanceof ComponentDeclaration ||
				//					child instanceof ComponentInstantiation ||
				//					child instanceof Context ||
				//					child instanceof SignalDeclaration ||
				//					child instanceof TypeDeclaration ||
				//					child instanceof SubProgram) {
				//			} else if (child != null ){
				//				System.out.println("searchOrigin OTHER CASE "+child.toString()+ "  type "+child.getClass().getSimpleName()+ "  loc  "+child.getLocation());
			}
		}

		return listResult;
	}


	private List<VHDLNode> searchSequentialSignalAssignment(VHDLNode node, String idPort) {
		List<VHDLNode> listResult = new ArrayList<VHDLNode>();
		if (node == null) {
			return listResult;
		}
		if (node instanceof SequenceOfStatements || node instanceof SequentialIf) {
			int numChildren = node.getNumChildren();
			for (int i = 0; i < numChildren; i++) {
				VHDLNode child = node.getChild(i);
				listResult.addAll( searchSequentialSignalAssignment(child, idPort));
			}
		} else if (node instanceof SequentialSignalAssignment) {
			if (((SequentialSignalAssignment)node).getTarget().getName().toString().trim().equalsIgnoreCase(idPort)) {
				listResult.add(node);
			}
		} else if (node instanceof SequentialCase) {
			listResult.addAll(searchInSequentialCase(node, idPort));
			//		} else if (node instanceof Name ||
			//				node instanceof OperationCompare ||
			//				node instanceof OperationLogic ||
			//				node instanceof SequentialVariableAssignment) {
			////			System.out.println("searchSequentialSignalAssignment  OperationCompare "+node.toString()+ " type "+node.getClass().getSimpleName()+ " loc "+node.getLocation());
			//
			//		} else {
			//			System.out.println("searchSequentialSignalAssignment  OTHER CASE "+node.toString()+ " type "+node.getClass().getSimpleName()+ " loc "+node.getLocation());
		}
		return listResult;

	}

	private List<VHDLNode> searchInSequentialCase(VHDLNode node, String idPort) {
		List<VHDLNode> listResult = new ArrayList<VHDLNode>();
		if (node == null) {return null;}

		int numChildren2 = node.getNumChildren();
		for (int j = 0; j < numChildren2; j++) {
			VHDLNode child2 = node.getChild(j);
			if (child2 != null && child2.getClass().getSimpleName().toString().equalsIgnoreCase("Alternative")) {
				listResult.addAll(searchInSequentialCase(child2, idPort));
			} else if (child2 instanceof SequenceOfStatements) {
				listResult.addAll(searchInSequentialCase(child2, idPort));
			}else  if (child2 instanceof SequentialSignalAssignment) {
				if (((SequentialSignalAssignment)child2).getTarget().getName().toString().trim().equalsIgnoreCase(idPort)) {
					listResult.add(child2);
				}
				//			}else  if (child2 instanceof Range) {
				////				System.out.println("searchInSequentialCase ConditionalSignalAssignment "+child2.toString()+"  type "+child2.getClass().getSimpleName()+"  loc  "+child2.getLocation());
				//			} else if (child2 != null) {
				//				System.out.println("searchInSequentialCase OTHER CASE "+child2.toString()+"  type "+child2.getClass().getSimpleName()+"  loc  "+child2.getLocation());
			}
		}
		return listResult;

	}


	public void searchUseSignal(String signalName, Object signalSource, String entityName, int cmptHierar, int nbHierarchie) {
		int numChildren = architecture.getNumChildren();
		VHDLNode child;
		for (int i = 0; i < numChildren; i++) {
			child = architecture.getChild(i);
//			if (child != null) {
//				
//				System.out.println("child "+i+"   "+child.toString()+ " type "+ child.getClass().getSimpleName()+ "  loc  "+ child.getLocation());
//			} else {
//				System.out.println("child NULL  "+i);
//			}
			if (child instanceof ConditionalSignalAssignment) {
				if (searchSignalInSignalAssignment((ConditionalSignalAssignment)child, signalName, signalSource, entityName, cmptHierar, nbHierarchie)) {
					addToSignalSource(child, signalSource, cmptHierar, nbHierarchie, "ConditionalSignalAssignment");
				}
				
			} else if (child instanceof ComponentInstantiation){
				for (HdlComponentInstantiation hdlComponentInstantiation : listComponent) {
					if (hdlComponentInstantiation.getComponentInstantiation().equals((ComponentInstantiation)child)) {
						InterfaceDeclaration inputPort = hdlComponentInstantiation.getInuputPort(signalName);
						if (inputPort != null) {
							String inputPortName = inputPort.getId(); 
							if (hdlComponentInstantiation.getEntity() != null) {
								//								System.out.println("COMPONENT "+hdlComponentInstantiation.getName()+ " IN "+signalName +" => "+inputPortName);
								if (!(signalName.trim().equalsIgnoreCase(inputPortName))) {
									HdlComponentInstantiation hdlComponentInstan = new HdlComponentInstantiation((ComponentInstantiation)child, this);
									AssociationElement portInstantiation = getPortInstantiation(hdlComponentInstan, inputPort);
									addVolationPreservationName(signalSource, 
											new ViolationPreservationName(entityName, getArchitecture().getId(), 
													getArchitecture().getLocation().fSF.getLocalPath(), signalName, 
													inputPortName, hdlComponentInstantiation.getComponentInstantiation().getLabel(), 
													((ComponentInstantiation)child).getLabel(), 
													portInstantiation != null ? portInstantiation.getLocation() : inputPort.getLocation()));
								}
								List<HdlArchitecture> listHdlArchitecture = hdlComponentInstantiation.getEntity().getListHdlArchitecture();
								if (listHdlArchitecture.isEmpty()) {
									if (signalSource instanceof RegisterInput  ||
											signalSource instanceof Register  ||
											signalSource instanceof Input ||
											signalSource instanceof InputOutput) {
										System.out.println(StopConditionE.IO.toString()+"  "+((RegisterInput)signalSource).toString()+"  "+cmptHierar);
										((RegisterInput)signalSource).setStopCondition(StopConditionE.IO);
									} else if (signalSource instanceof RegisterInputRead) {
										System.out.println(StopConditionE.IO.toString()+"  "+((RegisterInputRead)signalSource).toString()+"  "+cmptHierar);
										((RegisterInputRead)signalSource).setStopCondition(StopConditionE.IO);
									}
								}
								for (HdlArchitecture hdlArchi : listHdlArchitecture) {
									hdlArchi.searchUseSignal(inputPortName, signalSource, entityName, cmptHierar, nbHierarchie);
								}
							} else {
								System.out.println("hdlComponentInstantiation.getEntity() == null  "+hdlComponentInstantiation.toString());
							}
						}
					}
				}
			} else if (child instanceof SequentialProcess){
				searchSignalInProcess((SequentialProcess)child, signalName, signalSource, entityName, cmptHierar, nbHierarchie);
			} else if (child instanceof ComponentDeclaration 
					|| child instanceof SignalDeclaration
					|| child instanceof Context
					|| child instanceof ConstantDeclaration
					|| child instanceof TypeDeclaration
					|| child instanceof AliasDeclaration
					|| child instanceof SubProgram
					|| child instanceof ConcurrentProcedureCall
					|| child instanceof ConfigurationSpecification){
			} else {
				System.out.println("searchUseSignal CHILD OTHER "+child.toString()+ " TYPE "+child.getClass().getSimpleName() +
						" LOCATION "+child.getLocation());
			}

		}
	}



	private AssociationElement getPortInstantiation(HdlComponentInstantiation hdlComponentInstan,
			InterfaceDeclaration inputPort) {
		int numChildren = hdlComponentInstan.getComponentInstantiation().getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = hdlComponentInstan.getComponentInstantiation().getChild(i);
			if (child instanceof AssociationList) {
				int numAssoElem = child.getNumChildren();
				for (int j = 0; j < numAssoElem; j++) {
					VHDLNode subChild = child.getChild(j);
					if (subChild instanceof AssociationElement) {
						//						System.out.println("((AssociationElement)assoElem).getFormalPart() "+((AssociationElement)subChild).getFormalPart());
						//						System.out.println("((AssociationElement)assoElem) "+((AssociationElement)subChild).toString()+ " LOC "+subChild.getLocation());
						//						System.out.println("inputPort  "+inputPort);
						AssociationElement assoElem = (AssociationElement) subChild;
						if (assoElem.getFormalPart() != null) {
							if (assoElem.getActualPart() != null ) { //ActualPart NULL => OPEN
								if (assoElem.getFormalPart().toString().trim().equalsIgnoreCase(inputPort.getId())) {
									return (AssociationElement)assoElem;
								}
							}
						} else {
							// case per number
							for (int h = 0; h < numAssoElem; h++) {
								assoElem = (AssociationElement) child.getChild(h);
								if (assoElem.getActualPart().toString().equalsIgnoreCase(inputPort.getId())) {
									return assoElem;
									//									InterfaceDeclaration interfaceDeclaration = hdlComponentInstan.getInterfaceDeclaration(h);
									//									if (interfaceDeclaration != null && interfaceDeclaration.getDir() == OIDir.IN) {
									//										return interfaceDeclaration;
									//									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void addVolationPreservationName(Object signalSource, 
			ViolationPreservationName violationPreservationName) {

		if (signalSource instanceof ClockSource) {
			((ClockSource)signalSource).addViolationPreservationName(
					violationPreservationName);
		} else if (signalSource instanceof ResetSource) {
			((ResetSource)signalSource).addViolationPreservationName(
					violationPreservationName);
		}

	}

	private void addToSignalSource(VHDLNode child, Object signalSource, int cmptHierar, int nbHierarchie, String type) {
		if (signalSource instanceof ClockSource) {
			((ClockSource)signalSource).addReadClockSource(new ClockSourceRead(child, entityName, getId()));
		} else if (signalSource instanceof ResetSource) {
			((ResetSource)signalSource).addReadResetSource(new ResetSourceRead(child, entityName, getId()));
		} else if (signalSource instanceof RegisterInput  ||
				signalSource instanceof Register  ||
				signalSource instanceof Input) {
			RegisterInputRead registerInputRead = new RegisterInputRead(child, hdlEntity, getId(), ((RegisterInput)signalSource).toString(), cmptHierar);
			int result = ((RegisterInput)signalSource).addReadRegisterInput(registerInputRead);
			if (type.equalsIgnoreCase("Input Port")) {
				registerInputRead.setStopCondition(StopConditionE.IO_PAD);
			} else if (type.equalsIgnoreCase("Instance Output")) {
				registerInputRead.setStopCondition(StopConditionE.IO);
			} else if (type.equalsIgnoreCase("sequential if")) {
				// do nothing
			} else {
				continueSearchRead(registerInputRead, cmptHierar, nbHierarchie);
			}
		} else if (signalSource instanceof RegisterInputRead) {
			RegisterInputRead registerInputRead = new RegisterInputRead(child, hdlEntity, getId(), ((RegisterInputRead)signalSource).getTarget(), ((RegisterInputRead)signalSource).getParent(), cmptHierar);
			int result = ((RegisterInputRead)signalSource).addReadRegisterInput(registerInputRead);
			if (type.equalsIgnoreCase("Input Port")) {
				registerInputRead.setStopCondition(StopConditionE.IO_PAD);
			} else if (type.equalsIgnoreCase("Instance Output")) {
				registerInputRead.setStopCondition(StopConditionE.IO);
			} else if (type.equalsIgnoreCase("sequential if")) {
				// do nothing
			} else {
				continueSearchRead(registerInputRead, cmptHierar, nbHierarchie);
			}

		} else {
			System.out.println("signalSource "+signalSource.getClass().getSimpleName());
		}
	}

	private void continueSearchRead(RegisterInputRead registerInputRead, int cmptHierar, int nbHierarchie) {
		if (registerInputRead.isUsed()) {
			System.out.println("continueSearchRead  isUsed");
			return;
		}
		Target target = registerInputRead.getTarget();
		if (target == null) {
			registerInputRead.setStopCondition(StopConditionE.CONSTANT_ASSIGNMENT);
			return;
		}
		if (target.getName().toString().equalsIgnoreCase(registerInputRead.toString())) {
			registerInputRead.setStopCondition(StopConditionE.STATE_MACHINE);
			return;
		}
//		if (registerInputRead.getOperation().contains(target.getName().toString())) {
//			return;
//		}
		if (registerInputRead.isSameParent()) {
			registerInputRead.setStopCondition(StopConditionE.STATE_MACHINE);
			return;
		}
		
		registerInputRead.setUsed();
		registerInputRead.getHdlEntity().searchReadSignalSource(registerInputRead, getVectorName(target.getName().toString()), cmptHierar +1, nbHierarchie);
}

	private String getId() {
		return id;
	}

	private void searchSignalInProcess(SequentialProcess process,
			String signalName, Object signalSource, String entityName, int cmptHierar, int nbHierarchie) {
		int numChildren = process.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode sequence = process.getChild(i);
			searchSignal(sequence, signalName, signalSource, entityName, cmptHierar, nbHierarchie);
		}
	}

	private void searchSignal(VHDLNode node, String signalName, Object signalSource, String entityName, int cmptHierar, int nbHierarchie) {
		if (node == null) {
			return;
		}

		if (node instanceof SequenceOfStatements || node instanceof SequentialIf) {
			int numChildren = node.getNumChildren();
			for (int i = 0; i < numChildren; i++) {
				VHDLNode child = node.getChild(i);
				searchSignal(child, signalName, signalSource, entityName, cmptHierar, nbHierarchie);
			}
		} else if (node instanceof SequentialSignalAssignment) {
			if (((SequentialSignalAssignment)node).toString().contains(signalName)) {
				Waveform waweForm = (Waveform) node.getChild(1);
				WaveformElement waweFormElement = (WaveformElement) waweForm.getChild(0);
				// traiter les diff cas
				VHDLNode childWaveForm = waweFormElement.getChild(0);
				if (childWaveForm instanceof OperationLogic) {
					if (childWaveForm.toString().contains(signalName)) {
						int numOpName = childWaveForm.getNumChildren();
						for (int i = 0; i < numOpName; i++) {
							VHDLNode opName = childWaveForm.getChild(i);
							if (opName instanceof OperationName) {
								VHDLNode name = opName.getChild(0);
								SearchSignalInName(node, name, signalName, signalSource, cmptHierar, nbHierarchie);
							}
						}
					}
				} else if (childWaveForm instanceof OperationName) {
					if (childWaveForm.toString().equalsIgnoreCase(signalName)) {
						addToSignalSource(node, signalSource, cmptHierar, nbHierarchie, "OperationName");
					} else {
						String vectorName =  getVectorName(childWaveForm.toString());
						if (signalName.equalsIgnoreCase(vectorName)) {
							addToSignalSource(node, signalSource, cmptHierar, nbHierarchie, "OperationName");
						}
					}
				} else if (childWaveForm instanceof OperationMath) {
					searchSignalInOpMath(node, childWaveForm, signalSource, signalName, entityName, cmptHierar, nbHierarchie);
					
				} else if (childWaveForm != null) {
				}
			}
		} else if (node instanceof OperationCompare){
			if (((OperationCompare)node).getOperandB().toString().trim().equalsIgnoreCase(signalName) || 
					((OperationCompare)node).getOperandA().toString().trim().equalsIgnoreCase(signalName)) {
				if ((VHDLNode)node.getParent() instanceof SequentialIf) {
					if (((OperationCompare)node).getOperandB().toString().trim().equalsIgnoreCase(signalName)) {
						searchReadInSequentialIf((VHDLNode) node.getParent(), signalSource, cmptHierar, nbHierarchie, ((OperationCompare)node).getOperandB());
					} else {
						searchReadInSequentialIf((VHDLNode) node.getParent(), signalSource, cmptHierar, nbHierarchie, ((OperationCompare)node).getOperandA());
					}
				} else {
					addToSignalSource(node, signalSource, cmptHierar, nbHierarchie, "OperationCompare");
				}
			}
		} else if (node instanceof OperationName){
			if (((OperationName)node).toString().contains(signalName)) {

				VHDLNode name = node.getChild(0);
				VHDLNode nameExtension = name.getChild(0);
				VHDLNode range = nameExtension.getChild(0);
				if (range != null && range.toString().trim().equalsIgnoreCase(signalName)) {
					addToSignalSource(node, signalSource, cmptHierar, nbHierarchie, "OperationName");
				} else if (name.toString().replace(nameExtension.toString(), "").trim().equalsIgnoreCase(signalName)){
					addToSignalSource(node, signalSource, cmptHierar, nbHierarchie, "OperationName");
				}
			}
		} else if (node instanceof OperationLogic||
					node instanceof SequentialVariableAssignment){
			searchSignalInOpLogic(node, node, signalSource, signalName, entityName, cmptHierar, nbHierarchie);
		} else if (node instanceof SequentialCase){
			searchSignalInSequentialCase(node, signalSource, signalName, entityName, cmptHierar, nbHierarchie);
		} else if (node instanceof Name ||
				//	node instanceof SequentialVariableAssignment ||
				node instanceof VariableDeclaration){
			// do nothing
		} else if (node instanceof Range) {
			searchSignalInOpLogic(node, node, signalSource, signalName, entityName, cmptHierar, nbHierarchie);
		} else {
			System.out.println("OTHER IN PROCESS  node "+node+ " TYPE "+node.getClass().getSimpleName()+
					" LOCATION "+node.getLocation());
		}
	}

	private void SearchSignalInName(VHDLNode node, VHDLNode name, String signalName, Object signalSource, int cmptHierar, int nbHierarchie) {
		if (name.getNumChildren() == 0) {
			if (name.toString().trim().equalsIgnoreCase(signalName)) {
				addToSignalSource(node, signalSource, cmptHierar, nbHierarchie, "SearchSignalInName");
			} else {
				String vectorName =  getVectorName(name.toString());
				if (signalName.equalsIgnoreCase(vectorName)) {
					addToSignalSource(node, signalSource, cmptHierar, nbHierarchie, "SearchSignalInName");
				}
			}
		} else {
			VHDLNode nameExtR1 = name.getChild(0);
			String signalStart = name.toString().replace(nameExtR1.toString(), "");
			if (signalStart.trim().equalsIgnoreCase(signalName)) {
				addToSignalSource(node, signalSource, cmptHierar, nbHierarchie, "SearchSignalInName");
			} else {
				String vectorName =  getVectorName(signalStart);
				if (signalName.equalsIgnoreCase(vectorName)) {
					addToSignalSource(node, signalSource, cmptHierar, nbHierarchie, "SearchSignalInName");
				}
			}
			
			VHDLNode range = nameExtR1.getChild(0);
			int numChildren = range.getNumChildren();
			for (int i = 0; i < numChildren; i++) {
				VHDLNode childRange = range.getChild(i);
				if (childRange instanceof Name) {
					SearchSignalInName(node, childRange, signalName, signalSource, cmptHierar, nbHierarchie);
				}
			}
		}
		}

	private void searchReadInSequentialIf(VHDLNode node, Object signalSource, int cmptHierar, int nbHierarchie, Operation operation) {
		// addToSignalSource(node, signalSource);
		int numChildren = node.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = node.getChild(i);
			if (child instanceof SequentialIf) {
				searchReadInSequentialIf(child, signalSource, cmptHierar, nbHierarchie, operation);
			} else if (child instanceof SequenceOfStatements) {
				searchReadInSequentialIf(child, signalSource, cmptHierar, nbHierarchie, operation);
			} else if (child instanceof SequentialSignalAssignment) {
				SequentialSignalAssignment assign = (SequentialSignalAssignment) child;
				if (signalSource instanceof RegisterInput  ||
						signalSource instanceof Register  ||
						signalSource instanceof Input) {
					if (!((RegisterInput)signalSource).ListReadRegisterInputContains(assign)) {
						addToSignalSource(assign, signalSource, cmptHierar, nbHierarchie, "SequentialSignalAssignment");
					}
				} else if (signalSource instanceof RegisterInputRead) {
					if (!((RegisterInputRead)signalSource).ListReadRegisterInputContains(assign)) {
						addToSignalSource(assign, signalSource, cmptHierar, nbHierarchie, "SequentialSignalAssignment");
					}
				}
			} else if (child instanceof OperationCompare) {
			} else if (child!= null) {
//				System.out.println("searchReadInSequentialIf "+child.toString()+ " type "+ child.getClass().getSimpleName()+ " loc "+child.getLocation());
			}
		}
	}

	private static String getVectorName(String name) {
		return (name.indexOf("(") == -1 ? 
				(name.indexOf(".") == -1 ? name : name.substring(0, name.indexOf("."))) 
				: name.substring(0, name.indexOf("(")));
	}

	private void searchSignalInSequentialCase(VHDLNode node, Object signalSource,
			String signalName, String entityName, int cmptHierar, int nbHierarchie) {
		int numChildren = node.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = node.getChild(i);
			if (child instanceof OperationName) {
				if (child.toString().equalsIgnoreCase(signalName)) {
					searchReadInSequentialCase((VHDLNode) child.getParent(), signalSource, cmptHierar, nbHierarchie);
				}
			} else if (child instanceof OperationLogic) {
				if (checkSignalInOpLogic(child, signalSource, signalName, entityName)) {
					searchReadInSequentialCase((VHDLNode) child.getParent(), signalSource, cmptHierar, nbHierarchie);
				}
			} else if (child != null && child.getClass().getSimpleName().equalsIgnoreCase("Alternative")) {
				int numChildren2 = child.getNumChildren();
				for (int j = 0; j < numChildren2; j++) {
					VHDLNode child2 = child.getChild(j);
					if (child2 == null) { continue;}
					searchSignal(child2, signalName, signalSource, entityName, cmptHierar, nbHierarchie);
				}

			} else if (child != null) {
				System.out.println("OTHER IN CASE node "+child+ " TYPE "+child.getClass().getSimpleName()+
						" LOCATION "+child.getLocation());
			}
		}
	}

	private boolean checkSignalInOpLogic(VHDLNode node,
			Object signalSource, String signalName, String entityName2) {

		int numChildren = node.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = node.getChild(i);
			if (child instanceof OperationLogic) {
				return checkSignalInOpLogic(child, signalSource, signalName, entityName);
			} else if (child instanceof OperationName) {
				if (child.toString().equalsIgnoreCase(signalName)) {
					return true;
				} else {
					String vectorName = getVectorName(child.toString());
					if (vectorName.equalsIgnoreCase(signalName)) {
						return true;
					}
				}
			} else if (child instanceof OperationCompare) {
				return checkSignalInOpLogic( child, signalSource, signalName, entityName);
			} else if (child instanceof OperationLiteral) {
				//do nothing
			} else if (child != null) {
//				logger.debug("OTHER IN OPERATION LOGIC "+child+ " TYPE "+child.getClass().getSimpleName()+ " LOCATION "+ child.getLocation());
			}
		}

	
		return false;
	}

	private void searchReadInSequentialCase(VHDLNode node, Object signalSource, int cmptHierar, int nbHierarchie) {
		// addToSignalSource(node, signalSource);
		int numChildren = node.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = node.getChild(i);
			if (child instanceof SequentialIf) {
				searchReadInSequentialIf(child, signalSource, cmptHierar, nbHierarchie, null);
			} else if (child instanceof SequenceOfStatements) {
				searchReadInSequentialCase(child, signalSource, cmptHierar, nbHierarchie);
			} else if (child != null && child.getClass().getSimpleName().equalsIgnoreCase("Alternative")) {
				searchReadInSequentialCase(child, signalSource, cmptHierar, nbHierarchie);
			} else if (child instanceof SequentialSignalAssignment) {
				SequentialSignalAssignment assign = (SequentialSignalAssignment) child;
				if (signalSource instanceof RegisterInput  ||
						signalSource instanceof Register  ||
						signalSource instanceof Input) {
					if (!((RegisterInput)signalSource).ListReadRegisterInputContains(assign)) {
						addToSignalSource(assign, signalSource, cmptHierar, nbHierarchie, "SequentialSignalAssignment");
					} else {
						((RegisterInput)signalSource).setStopCondition(StopConditionE.STATE_MACHINE);
					}
				} else if (signalSource instanceof RegisterInputRead) {
					if (!((RegisterInputRead)signalSource).ListReadRegisterInputContains(assign)) {
						addToSignalSource(assign, signalSource, cmptHierar, nbHierarchie, "SequentialSignalAssignment");
					} else {
						((RegisterInputRead)signalSource).setStopCondition(StopConditionE.STATE_MACHINE);
					}
				}
//			} else if (child instanceof OperationCompare) {
//			} else if (child!= null) {
//				System.out.println("searchReadInSequentialCase "+child.toString()+ " type "+ child.getClass().getSimpleName()+ " loc "+child.getLocation());
			}
		}
	}

	private void searchSignalInOpMath(VHDLNode parent, VHDLNode node, Object signalSource,
			String signalName, String entityName, int cmptHierar, int nbHierarchie) {
		if (node == null) { return;}
		int numChildren = node.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = node.getChild(i);
			if (child instanceof OperationMath) {
				searchSignalInOpMath(parent, child, signalSource, signalName, entityName, cmptHierar, nbHierarchie);
			} else if (child instanceof OperationName){
				if (child.toString().equalsIgnoreCase(signalName)) {
					addToSignalSource(parent, signalSource, cmptHierar, nbHierarchie, "OperationName");
				} else {
					String vectorName = getVectorName(child.toString());
					if (vectorName.equalsIgnoreCase(signalName)) {
						addToSignalSource(parent, signalSource, cmptHierar, nbHierarchie, "OperationName");
					}
				}
			}
		}

	}
		private void searchSignalInOpLogic(VHDLNode parent, VHDLNode node, Object signalSource,
				String signalName, String entityName, int cmptHierar, int nbHierarchie) {
		int numChildren = node.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = node.getChild(i);
			if (child instanceof OperationLogic) {
				searchSignalInOpLogic(parent, child, signalSource, signalName, entityName, cmptHierar, nbHierarchie);
			} else if (child instanceof OperationName) {
				if (child.toString().equalsIgnoreCase(signalName)) {
					if (parent.getParent() instanceof SequentialIf) {
						searchReadInSequentialIf((VHDLNode) parent.getParent(), signalSource, cmptHierar, nbHierarchie, null);
					} else {
						addToSignalSource(parent, signalSource, cmptHierar, nbHierarchie, "OperationName");
					}
				} else {
					String vectorName = getVectorName(child.toString());
					if (vectorName.equalsIgnoreCase(signalName)) {
						if (parent.getParent() instanceof SequentialIf) {
							searchReadInSequentialIf((VHDLNode) parent.getParent(), signalSource, cmptHierar, nbHierarchie, null);
						} else {
							addToSignalSource(parent, signalSource, cmptHierar, nbHierarchie, "OperationName");
						}
					}
				}
			} else if (child instanceof OperationCompare) {
				searchSignalInOpLogic(parent, child, signalSource, signalName, entityName, cmptHierar, nbHierarchie);
			} else if (child instanceof OperationLiteral) {
				//do nothing
			} else if (child != null) {
//				logger.debug("OTHER IN OPERATION LOGIC "+child+ " TYPE "+child.getClass().getSimpleName()+ " LOCATION "+ child.getLocation());
			}
		}

	}

	protected boolean searchSignalInSignalAssignment(
			ConditionalSignalAssignment signalAssign, String signalName, Object signalSource, String entityName, int cmptHierar, int nbHierarchie) {
		if (! signalAssign.toString().contains(signalName)) { return false; }
			for (int i = 0; i < signalAssign.getNumConditionalWaveforms(); i++) {
				ConditionalWaveform conditionalWaveform = signalAssign.getConditionalWaveform(i);
				int numChildren = conditionalWaveform.getNumChildren();
				for (int j = 0; j < numChildren; j++) {
					VHDLNode subConditionalWaweForm = conditionalWaveform.getChild(j);
					if (subConditionalWaweForm instanceof Waveform){
						VHDLNode waveformElement = subConditionalWaweForm.getChild(0);
						VHDLNode subWaveformElement = waveformElement.getChild(0);
						if (subWaveformElement instanceof OperationName) {
							if (subConditionalWaweForm.toString().trim().equalsIgnoreCase(signalName)) {
								return true;
							} else {
								String vectorName =  getVectorName(subConditionalWaweForm.toString().trim());
								if (signalName.equalsIgnoreCase(vectorName)) {
									return true;
								}
							}
						} else if (subWaveformElement instanceof OperationLogic) {
							searchSignalInOpLogic(signalAssign, subWaveformElement, signalSource, signalName, entityName, cmptHierar, nbHierarchie);
						}
					} else if (subConditionalWaweForm instanceof OperationCompare) {
						int numOpName = subConditionalWaweForm.getNumChildren();
						for (int k = 0; k < numOpName; k++) {
							VHDLNode opName = subConditionalWaweForm.getChild(k);
							if (opName instanceof OperationName) {
								if (opName.toString().trim().equalsIgnoreCase(signalName)) {
									return true;
								} else {
									String vectorName =  getVectorName(opName.toString().trim());
									if (signalName.equalsIgnoreCase(vectorName)) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		return false;

	}

	public ClockSource isSignalRegister(SignalSource signalSource) {
		for (Process process : listProcess) {
			ClockSource clockSourceRegister = process.isSignalRegister(signalSource);
			if (clockSourceRegister != null) {
				return clockSourceRegister;
			}
		}

		return null;
	}

}
