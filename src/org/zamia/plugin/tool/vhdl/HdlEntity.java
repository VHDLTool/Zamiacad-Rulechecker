package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zamia.DMManager;
import org.zamia.DesignModuleStub;
import org.zamia.ZamiaException;
import org.zamia.ZamiaProject;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.plugin.tool.vhdl.manager.ArchitectureManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.AssociationElement;
import org.zamia.vhdl.ast.AssociationList;
import org.zamia.vhdl.ast.ComponentInstantiation;
import org.zamia.vhdl.ast.ConditionalSignalAssignment;
import org.zamia.vhdl.ast.ConditionalWaveform;
import org.zamia.vhdl.ast.DMUID.LUType;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.InterfaceList;
import org.zamia.vhdl.ast.Name;
import org.zamia.vhdl.ast.NameExtension;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationConcat;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.Range;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialCase;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.TypeDefinitionSubType;
import org.zamia.vhdl.ast.VHDLNode;
import org.zamia.vhdl.ast.Waveform;

public class HdlEntity  implements Cloneable {

	public static final String IO = "IO_";

	private Entity entity;

	private String fileLocalPath;

	private List<HdlArchitecture> listHdlArchitecture = new ArrayList<HdlArchitecture>();

	private InterfaceList genericInstantiation;

	private AssociationList genericInstantiationAssociationList;

	private List<InputOutput> listIO = new ArrayList<InputOutput>();

	public HdlEntity(Entity entity, String _fileLocalPath) {
		this.entity = entity;
		fileLocalPath = _fileLocalPath;
	}

	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

	public Entity getEntity() {
		return entity;
	}

	public List<InputOutput> getListIO() {
		return listIO;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public List<HdlArchitecture> getListHdlArchitecture() {
		return listHdlArchitecture;
	}

	public void setListHdlArchitecture(List<HdlArchitecture> listHdlArchitecture) {
		this.listHdlArchitecture = listHdlArchitecture;
	}

	public void addHdlArchitecture(HdlArchitecture hdlArchitecture) {
		if (listHdlArchitecture == null) {
			listHdlArchitecture = new ArrayList<HdlArchitecture>();
		}

		this.listHdlArchitecture.add(hdlArchitecture);
	}

	public void clearArcitecture() {
		listHdlArchitecture = new ArrayList<HdlArchitecture>();
	}

	public InterfaceDeclaration getInterfaceDeclaration(int numOutput) {
		int numPort = -1;
		int numChildren = entity.getNumChildren();
		InterfaceList generic = null;
		InterfaceList mapComponent = null;
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = entity.getChild(i);
			if (child instanceof InterfaceList) {
				if (mapComponent == null) {
					mapComponent = (InterfaceList) child;
				} else {
					generic = mapComponent;
					mapComponent = (InterfaceList) child;
				}
			}
		}

		int numInterfaces = mapComponent.getNumInterfaces();
		for (int j = 0; j < numInterfaces; j++) {
			InterfaceDeclaration interfaceDeclaration = mapComponent.get(j);
			VHDLNode typeDefinitionSubType = interfaceDeclaration.getChild(0);
			if (typeDefinitionSubType instanceof TypeDefinitionSubType) {
				VHDLNode name = typeDefinitionSubType.getChild(0);
				if (name instanceof Name) {
					int numExtensions = ((Name) name).getNumExtensions();
					if (numExtensions == 0) {
						numPort++;
					} else {
						NameExtension extension = ((Name) name).getExtension(0);
						VHDLNode extChild = extension.getChild(0);
						if (extChild instanceof Range) {
							Range range = (Range) extChild;
							int fLeft = ToolManager.getOp(range.getLeft(), this, listHdlArchitecture.get(0));
							int fRight = ToolManager.getOp(range.getRight(), this, listHdlArchitecture.get(0));
							int indexMax = 0;
							int indexMin = 0;
							if (range.isAscending()) {
								indexMax = fRight;
								indexMin = fLeft;
							} else {
								indexMax = fLeft;
								indexMin = fRight;
							}
							numPort+=(indexMax - indexMin +1);
						}

					}
					if (numOutput <= numPort) {
						return interfaceDeclaration;
					}
				}
			}
		}
		return null;
	}


	public InterfaceDeclaration getInterfaceDeclaration(String inputName) {
		int numChildren = entity.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = entity.getChild(i);
			if (child instanceof InterfaceList) {
				int numInterfaces = ((InterfaceList) child).getNumInterfaces();
				if (numInterfaces <= 1) {
					continue;
				}
				if (!child.toString().contains(inputName)) {
					continue;
				}
				for (int j = 0; j < numInterfaces; j++) {
					InterfaceDeclaration interfaceDeclaration = ((InterfaceList) child).get(j);
					String typeDefinitionSubType = interfaceDeclaration.getId();
					if (typeDefinitionSubType.equalsIgnoreCase(inputName)) {
						return interfaceDeclaration;
					}
				}
			}
		}
		return null;
	}

	public boolean isOutPutBlackBox(HdlComponentInstantiation componentInst, int numOutput) {
		return false;

	}

	public List<VHDLNode> searchOriginIncomponent(int numOutput) {
		List<VHDLNode> listResult = new ArrayList<VHDLNode>();
		String idPort = getInterfaceDeclaration(numOutput).getId();
		for (HdlArchitecture hdlArchitecture : listHdlArchitecture) {
			listResult.addAll(hdlArchitecture.searchOrigin(idPort));
		}
		return listResult;
	}

	public List<VHDLNode> searchOriginIncomponent(String signalName) {
		List<VHDLNode> listResult = new ArrayList<VHDLNode>();
		for (HdlArchitecture hdlArchitecture : listHdlArchitecture) {
			listResult.addAll(hdlArchitecture.searchOrigin(signalName));
		}
		return listResult;
	}


	@Override
	public String toString() {
		return entity != null ? entity.getId() : "NULL";
	}

	public Pair<InterfaceDeclaration, Integer> searchSignalInPortOut(String signalName, Object clockSource) {
		int numChild = entity.getNumChildren();
		for (int i = 0; i < numChild; i++) {
			VHDLNode interfaceList = entity.getChild(i);
			if (interfaceList instanceof InterfaceList) {
				if (interfaceList.toString().contains(signalName)) {
					int numInterface = interfaceList.getNumChildren();
					for (Integer numOutput = 0; numOutput < numInterface; numOutput++) {
						InterfaceDeclaration interfaceElem = (InterfaceDeclaration) interfaceList.getChild(numOutput);
						if (interfaceElem.getId().equalsIgnoreCase(signalName) && interfaceElem.getDir() == OIDir.OUT) {
							return new Pair<InterfaceDeclaration, Integer>(interfaceElem, numOutput);
						}
					}
				}
			}

		}
		return null;
	}

	public HdlComponentInstantiation searchInstantiation() {
		
		Map<String, HdlFile> listHdlFile;
		try {
			listHdlFile = ArchitectureManager.getArchitecture();
		} catch (EntityException e) {
			return null;
		}
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() == null) { continue;}
			for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
				if (hdlEntityItem.getListHdlArchitecture() == null) { continue;}
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListComponent() == null) { continue;}
					for (HdlComponentInstantiation hdlComponentItem : hdlArchitectureItem.getListComponent()) {
						if (hdlComponentItem.getName().equalsIgnoreCase(entity.getId())) {
							return hdlComponentItem;
						}
					}
				}
			}
		}
		return new HdlComponentInstantiation(null, null);
	}

	public void searchReadSignalSource(Object signalSource, String signalName, int cmptHierar, int nbHierarchie) {
		if (cmptHierar > nbHierarchie) {
			if (signalSource instanceof RegisterInput  ||
					signalSource instanceof Register  ||
					signalSource instanceof Input ||
					signalSource instanceof InputOutput) {
				((RegisterInput)signalSource).setStopCondition(StopConditionE.MAX_STAGE);
			} else if (signalSource instanceof RegisterInputRead) {
				((RegisterInputRead)signalSource).setStopCondition(StopConditionE.MAX_STAGE);
			}
			return;
		}
		
		for (HdlArchitecture hdlArchitectureItem : getListHdlArchitecture()) {
			hdlArchitectureItem.searchUseSignal(signalName, signalSource, getEntity().getId(), cmptHierar, nbHierarchie);

		}

		Pair<InterfaceDeclaration, Integer> searchSignalInPortOut = searchSignalInPortOut(signalName, signalSource);
		if (searchSignalInPortOut != null) {
			HdlComponentInstantiation componentInstantiation = searchInstantiation();
			if (componentInstantiation != null) {
				if (componentInstantiation.getComponentInstantiation() != null) {
					String portConnexionName = componentInstantiation.getPortConnexionName(searchSignalInPortOut.getSecond(), signalName);
					for (HdlEntity hdlEntity : componentInstantiation.getUseInEntity()) {
						hdlEntity.searchReadSignalSource(signalSource, portConnexionName, cmptHierar, nbHierarchie);
					}

				} else {
					// case output TOP
					if (signalSource instanceof RegisterInput  ||
							signalSource instanceof Register  ||
							signalSource instanceof Input ||
							signalSource instanceof InputOutput) {
						System.out.println(StopConditionE.IO_PAD.toString()+"  "+((RegisterInput)signalSource).toString()+"  "+cmptHierar);

						((RegisterInput)signalSource).setStopCondition(StopConditionE.IO_PAD);
					} else if (signalSource instanceof RegisterInputRead) {
						System.out.println(StopConditionE.IO_PAD.toString()+"  "+((RegisterInputRead)signalSource).toString()+"  "+cmptHierar);

						((RegisterInputRead)signalSource).setStopCondition(StopConditionE.IO_PAD);
					}
					addToSignalSource(searchSignalInPortOut.getFirst(), signalSource, "");
				}
			}
		}

	}

	private void addToSignalSource(VHDLNode child, Object signalSource, String architectureName) {
		if (signalSource instanceof ClockSource) {
			((ClockSource)signalSource).addReadClockSource(new ClockSourceRead(child, getEntity().getId(), architectureName));
		} else if (signalSource instanceof ResetSource) {
			((ResetSource)signalSource).addReadResetSource(new ResetSourceRead(child, getEntity().getId(), architectureName));
		} else if (signalSource instanceof RegisterInput  ||
				signalSource instanceof Register  ||
				signalSource instanceof Input ||
				signalSource instanceof InputOutput) {
			((RegisterInput)signalSource).addReadRegisterInput(new RegisterInputRead(child, this, architectureName, ((RegisterInput)signalSource).toString(), 0));
		} else if (signalSource instanceof RegisterInputRead) {
			((RegisterInputRead)signalSource).addReadRegisterInput(new RegisterInputRead(child, this, architectureName, ((RegisterInputRead)signalSource).getTarget(), ((RegisterInputRead)signalSource).getParent(), 0));
		}
	}

	public boolean useClock() {
		for (HdlArchitecture hdlArchitecture : listHdlArchitecture) {
			if (hdlArchitecture.getListProcess() == null) { continue;}
			for (Process process : hdlArchitecture.getListProcess()) {
				if (process.isSynchronous()) {
					return true;
				}
			}
		}
		return false;
	}

	public AssociationList setGenericAssociationList(ComponentInstantiation componentInstantiation) {
		int numChildren = entity.getNumChildren();
		int cmpt = 0;
		genericInstantiationAssociationList = null;
		for (Integer i = 0; i < numChildren; i++) {
			VHDLNode child = entity.getChild(i);
			if (child instanceof AssociationList) {
				cmpt++;
				if (genericInstantiationAssociationList == null) {
					genericInstantiationAssociationList = (AssociationList)child;
				}
			}
		}
		if (cmpt<=1) {
			genericInstantiationAssociationList = null;
		}
		return genericInstantiationAssociationList;
	}
	public InterfaceList setGeneric(ComponentInstantiation componentInstantiation) {
		int numChildren = entity.getNumChildren();
		int cmpt = 0;
		//		System.out.println("setGeneric  entity "+entity.getId()+ " LOC "+entity.getLocation());
		genericInstantiation = null;
		for (Integer i = 0; i < numChildren; i++) {
			VHDLNode child = entity.getChild(i);
			if (child instanceof InterfaceList) {
				cmpt++;
				if (genericInstantiation == null) {
					genericInstantiation = (InterfaceList)child;
					//					System.out.println("setGeneric "+genericInstantiation.toString());
				}
			}
		}
		if (cmpt<=1) {
			genericInstantiation = null;
		}
		return genericInstantiation;
	}

	public ClockSource isSignalRegister(SignalSource signalSource) {
		for (HdlArchitecture hdlArchitecture : listHdlArchitecture) {
			ClockSource clockSourceRegister = hdlArchitecture.isSignalRegister(signalSource);
			if (clockSourceRegister != null) {
				return clockSourceRegister;
			}
		}

		return null;
	}

	public void setArchitecture(ZamiaProject zPrj) {
		DMManager dum = zPrj.getDUM();

		int m = dum.getNumStubs();

		for (int j = 0; j < m; j++) {
			DesignModuleStub stub = dum.getStub(j);

			if (stub.getDUUID().getLibId().equalsIgnoreCase("WORK")) { // pour prendre les fichiers de travail
				if (stub.getDUUID().getType() == LUType.Architecture) {
					try {
						Architecture architecture = (Architecture)zPrj.getDUM().getDM(stub.getDUUID());
						if (architecture != null) {
							String localPath = "\\"+architecture.getSource().getLocalPath();
							if (fileLocalPath.equalsIgnoreCase(localPath)) {
								if (architecture.getEntityName().toString().equalsIgnoreCase(getEntity().getId())) {
									addHdlArchitecture(new HdlArchitecture(architecture, this));
								}
							}
						}
					} catch (ZamiaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public int setInputOutput(int num) {
		
		
		int numChildren = entity.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = entity.getChild(i);
			if (child instanceof InterfaceList) {
				listIO = new ArrayList<InputOutput>();
				int numChildren2 = child.getNumChildren();
				for (int j = 0; j < numChildren2; j++) {
					InterfaceDeclaration interfaceDeclaration = ((InterfaceList) child).get(j);
					InputOutput inputOutput = new InputOutput(interfaceDeclaration, IO + num);
					listIO.add(inputOutput);
					num ++;
				}
			}
		}
		
		return num;
		
	}

	public void searchSourceSignal(Object register,
			HdlEntity hdlEntity, HdlArchitecture hdlArchitecture,
			int cmptHierar, int nbHierarchie) {
		cmptHierar++;
		if (cmptHierar >= nbHierarchie) { 
			if (register instanceof RegisterInput  ||
					register instanceof Register  ||
					register instanceof Input) {
				((RegisterInput)register).setStopCondition(StopConditionE.MAX_STAGE);
			} else if (register instanceof RegisterInputSource) {
				((RegisterInputSource)register).setStopCondition(StopConditionE.MAX_STAGE);
			}

			return;
		}
		
		VHDLNode vhdlNode = null;
		if (register instanceof RegisterInput  ||
				register instanceof Register  ||
				register instanceof Input) {
			vhdlNode = ((RegisterInput)register).getVhdlNode();
		} else if (register instanceof RegisterInputSource) {
			vhdlNode = ((RegisterInputSource)register).getVhdlNode();
		}
		searchParent(register, vhdlNode, hdlEntity, hdlArchitecture, cmptHierar, nbHierarchie, register.toString());

		List<VHDLNode> searchOriginIncomponent = searchOriginIncomponent(register.toString());
		if (searchOriginIncomponent.isEmpty()) {
			searchOriginIncomponent = searchOriginIncomponent(ToolManager.getVectorName(register.toString()));
		}
		for (VHDLNode originIncomponent : searchOriginIncomponent) {
			VHDLNode child = originIncomponent.getChild(1);
			VHDLNode child2 = child.getChild(0);
			searchInOp2(register, child2, hdlEntity, hdlArchitecture, cmptHierar, nbHierarchie, register.toString());
		}
		if (searchOriginIncomponent.isEmpty()) {
			List<SignalSource> listSearchSignalOrigin = ToolManager.searchSignalOrigin(register.toString(), hdlEntity, hdlArchitecture, true);
			if (listSearchSignalOrigin.isEmpty()) {
				String structName = (register.toString().indexOf("(") == -1 ? 
						(register.toString().indexOf(".") == -1 ? register.toString() : register.toString().substring(0, register.toString().indexOf("."))) 
						: register.toString().substring(0, register.toString().indexOf("(")));
				if (! structName.equalsIgnoreCase(register.toString())) {
					listSearchSignalOrigin = ToolManager.searchSignalOrigin(structName, hdlEntity, hdlArchitecture, true);
				}
			}
			if (listSearchSignalOrigin.isEmpty()) {
				if (register instanceof RegisterInput  ||
						register instanceof Register  ||
						register instanceof Input) {
					((RegisterInput)register).setStopCondition(StopConditionE.CONSTANT_ASSIGNMENT);
				} else if (register instanceof RegisterInputSource) {
					((RegisterInputSource)register).setStopCondition(StopConditionE.CONSTANT_ASSIGNMENT);
				}
			}
			for (SignalSource signalSource : listSearchSignalOrigin) {
				if (signalSource.getSignalDeclaration().getVhdlNode() instanceof InterfaceDeclaration) {
					RegisterInputSource registerInputSource = new RegisterInputSource(signalSource.getSignalDeclaration().getVhdlNode(), hdlEntity, hdlEntity.getEntity().getId(), ((RegisterInputSource)register).toString(), cmptHierar);
					((RegisterInputSource)register).addSourceRegisterInput(registerInputSource);
					if (signalSource.getType().equalsIgnoreCase("Instance Output")) {
						registerInputSource.setStopCondition(StopConditionE.IO);
					} else if (signalSource.getType().equalsIgnoreCase("Input Port")) {
						registerInputSource.setStopCondition(StopConditionE.IO_PAD);
					}
				}
				if (! signalSource.getListOperand().isEmpty()) {
					addToSignalSearch(register, signalSource.getSignalDeclaration().getVhdlNode(), signalSource.getHdlEntity(), signalSource.getHdlArchitecture(), cmptHierar, nbHierarchie);
				} else {
					if (register instanceof RegisterInput  ||
							register instanceof Register  ||
							register instanceof Input) {
						((RegisterInput)register).setStopCondition(StopConditionE.CONSTANT_ASSIGNMENT);
					} else if (register instanceof RegisterInputSource) {
						((RegisterInputSource)register).setStopCondition(StopConditionE.CONSTANT_ASSIGNMENT);
					}

				}
			}
		}
		
	}


	
	private boolean search(Object register, String signalName, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int cmptHierar, int nbHierarchie) {
		boolean find = false;

			List<VHDLNode> listSearchOriginIncomponent = hdlEntity.searchOriginIncomponent(signalName);
			for (VHDLNode searchOriginIncomponent : listSearchOriginIncomponent) {
				if (searchOriginIncomponent!= null) {
					VHDLNode waveform;
					VHDLNode WaveformElement = null;
					if (searchOriginIncomponent instanceof ConditionalSignalAssignment) {
						VHDLNode child = searchOriginIncomponent.getChild(0);
						waveform = child.getChild(1);
						WaveformElement = waveform.getChild(0);
					} else {
						waveform = searchOriginIncomponent.getChild(1);
						WaveformElement = waveform.getChild(0);
					}
					searchInOp2(register, WaveformElement, hdlEntity, hdlArchitecture, cmptHierar, nbHierarchie, signalName);
					find = true;
				}
			}
		return find;
	}

	private void searchParent(Object register, VHDLNode vhdlNode,
			HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int cmptHierrar, int nbHierarchie, String signalName) {
		VHDLNode parent = (VHDLNode) vhdlNode.getParent();
		if (parent == null) { return;}
		if (parent instanceof SequentialProcess ||
				parent instanceof AssociationList ||
				parent instanceof Architecture) { return;}

		if (parent instanceof SequentialIf) {
			searchInOp(register, parent, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie, signalName);
		} else if (parent instanceof SequenceOfStatements ||
				parent instanceof SequentialCase ||
				(parent.getClass().getSimpleName().equalsIgnoreCase("Alternative"))) {

		} else {
		}
		searchParent(register, parent, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie, signalName);
	}

	private void searchInOp(Object register, VHDLNode child,
			HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int cmptHierrar, int nbHierarchie, String signalName) {
		if (child == null) { return;}

		int numChildren2 = child.getNumChildren();
		for (int j = 0; j < numChildren2; j++) {
			VHDLNode child2 = child.getChild(j);
			if (child2 instanceof OperationCompare) {
				searchInOp(register, child2, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie, signalName);
			} else if (child2 instanceof OperationLogic) {
				searchInOp(register, child2, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie, signalName);
			}else  if (child2 instanceof OperationName) {
				if (! child2.toString().equalsIgnoreCase(signalName)) {
					
					searchOriginAndAddToSignalSource(child2.toString(), hdlEntity, hdlArchitecture, register, cmptHierrar, nbHierarchie);
//					register.addSource( new SignalSource(new VhdlSignalDeclaration(child2), hdlEntity, hdlArchitecture));
//					searchRegisterOrigin(register, child2.toString(), hdlEntity, hdlArchitecture, i);
				}
			} else if (child2 instanceof OperationConcat) {
				searchInOp(register, child2, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie, signalName);
			}

		}
	}

	private void searchInOp2(Object register, VHDLNode child,
			HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int cmptHierrar, int nbHierarchie, String signalName) {
		if (child == null) { return;}

		int numChildren2 = child.getNumChildren();
		for (int j = 0; j < numChildren2; j++) {
			VHDLNode child2 = child.getChild(j);
			if (child2 instanceof OperationCompare) {
				searchInOp2(register, child2, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie, signalName);
			} else if (child2 instanceof OperationLogic) {
				searchInOp2(register, child2, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie, signalName);
			}else  if (child2 instanceof OperationName) {
				if (! child2.toString().equalsIgnoreCase(signalName)) {
//					addToSignalSource(child2, register, cmptHierrar, nbHierarchie, hdlEntity, hdlArchitecture);
//					searchOriginAndAddToSignalSource(child2.toString(), hdlEntity, hdlArchitecture, register, cmptHierrar, nbHierarchie);
//					searchRegisterOrigin(register, child2.toString(), hdlEntity, hdlArchitecture, i);
					addToSignalSearch(register, child2, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
				}
			} else if (child2 instanceof OperationConcat) {
				searchInOp2(register, child2, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie, signalName);
			}

		}
	}

	private void addToSignalSearch(Object register, VHDLNode child, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int cmptHierrar, int nbHierarchie) {
		if (register instanceof RegisterInput  ||
				register instanceof Register  ||
				register instanceof Input) {
			RegisterInputSource registerInputSource = new RegisterInputSource(child, hdlEntity, hdlEntity.getEntity().getId(), ((RegisterInput)register).toString(), cmptHierrar);
			((RegisterInput)register).addSourceRegisterInput(registerInputSource);
			if (! (registerInputSource.toString().equalsIgnoreCase(((RegisterInput)register).toString()) ||
					ToolManager.getVectorName(registerInputSource.toString()).equalsIgnoreCase(((RegisterInput)register).toString()) ||
					registerInputSource.toString().equalsIgnoreCase(ToolManager.getVectorName(((RegisterInput)register).toString())))) {
				searchSourceSignal(registerInputSource, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
			} else {
				((RegisterInput)register).setStopCondition(StopConditionE.STATE_MACHINE);
			}
		} else if (register instanceof RegisterInputSource) {
			if (child instanceof SequentialSignalAssignment) {
				Name target = ((SequentialSignalAssignment)child).getTarget().getName();
				RegisterInputSource registerInputSource = new RegisterInputSource(target, hdlEntity, hdlEntity.getEntity().getId(), ((RegisterInputSource)register).toString(), cmptHierrar);
				((RegisterInputSource)register).addSourceRegisterInput(registerInputSource);
				
				Waveform waveform = ((SequentialSignalAssignment)child).getValue();
				VHDLNode waveformElement = waveform.getChild(0);
				addToSignalSearchInOp(waveformElement, registerInputSource, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
			} else if (child instanceof ConditionalSignalAssignment) { 
				Name target = ((ConditionalSignalAssignment)child).getTarget().getName();
				RegisterInputSource registerInputSource = new RegisterInputSource(target, hdlEntity, hdlEntity.getEntity().getId(), ((RegisterInputSource)register).toString(), cmptHierrar);
				if (register.toString().equalsIgnoreCase(registerInputSource.toString())) {
					
					int numChildren = child.getNumChildren();
					for (int i = 0; i < numChildren; i++) {
						VHDLNode child2 = child.getChild(i);
						if (child2 instanceof ConditionalWaveform) {
							addToSignalSearchInOp(child2, (RegisterInputSource)register, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
						}
					}

				} else {
					
					((RegisterInputSource)register).addSourceRegisterInput(registerInputSource);
					
					int numChildren = child.getNumChildren();
					for (int i = 0; i < numChildren; i++) {
						VHDLNode child2 = child.getChild(i);
						if (child2 instanceof ConditionalWaveform) {
							addToSignalSearchInOp(child2, registerInputSource, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
						}
					}
				}

			} else {
				RegisterInputSource registerInputSource = new RegisterInputSource(child, hdlEntity, hdlEntity.getEntity().getId(), ((RegisterInputSource)register).toString(), cmptHierrar);
				((RegisterInputSource)register).addSourceRegisterInput(registerInputSource);
				if (! (registerInputSource.toString().equalsIgnoreCase(((RegisterInputSource)register).toString()) ||
						ToolManager.getVectorName(registerInputSource.toString()).equalsIgnoreCase(((RegisterInputSource)register).toString()) ||
						registerInputSource.toString().equalsIgnoreCase(ToolManager.getVectorName(((RegisterInputSource)register).toString())))) {
					searchSourceSignal(registerInputSource, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
				} else {
					System.out.println(StopConditionE.STATE_MACHINE.toString()+"  "+registerInputSource.toString()+ "  " +cmptHierrar);

					registerInputSource.setStopCondition(StopConditionE.STATE_MACHINE);
				}
			}
		}
	}

	private void addToSignalSearchInOp(VHDLNode waveformElement, RegisterInputSource registerInputSource, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int cmptHierrar, int nbHierarchie) {
		int numChildren = waveformElement.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			 VHDLNode child = waveformElement.getChild(i);
			 if (child instanceof OperationName) {
					RegisterInputSource registerInputSource2 = new RegisterInputSource(child, hdlEntity, hdlEntity.getEntity().getId(), ((RegisterInputSource)registerInputSource).toString(), cmptHierrar);
					((RegisterInputSource)registerInputSource).addSourceRegisterInput(registerInputSource2);

				 if (! (registerInputSource.toString().equalsIgnoreCase(((RegisterInputSource)registerInputSource2).toString()) ||
							ToolManager.getVectorName(registerInputSource.toString()).equalsIgnoreCase(((RegisterInputSource)registerInputSource2).toString()) ||
							registerInputSource.toString().equalsIgnoreCase(ToolManager.getVectorName(((RegisterInputSource)registerInputSource2).toString())))) {
						searchSourceSignal(registerInputSource2, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
					} else {
						registerInputSource.setStopCondition(StopConditionE.STATE_MACHINE);
					}
			 } else if (child instanceof OperationLogic) {
				 addToSignalSearchInOp(child, registerInputSource, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
			 } else if (child instanceof OperationCompare) {
				 addToSignalSearchInOp(child, registerInputSource, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
			 } else if (child instanceof Waveform) {
				 VHDLNode child2 = child.getChild(0);
				 addToSignalSearchInOp(child2, registerInputSource, hdlEntity, hdlArchitecture, cmptHierrar, nbHierarchie);
			 }
		}
		
	}

	private void searchOriginAndAddToSignalSource(String name, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture,
			Object register, int cmptHierrar, int nbHierrar) {
		if (name.contains("'")) {
			name = name.substring(0, name.indexOf("'"));
		}
		List<SignalSource> listSearchSignalOrigin = ToolManager.searchSignalOrigin(name, hdlEntity, hdlArchitecture, true);
		if (listSearchSignalOrigin.isEmpty()) {
			String structName = (name.toString().toString().indexOf("(") == -1 ? 
					(name.toString().toString().indexOf(".") == -1 ? name.toString().toString() : name.toString().toString().substring(0, name.toString().toString().indexOf("."))) 
					: name.toString().toString().substring(0, name.toString().toString().indexOf("(")));
				listSearchSignalOrigin = ToolManager.searchSignalOrigin(structName, hdlEntity, hdlArchitecture, true);
		}
		for (SignalSource signalSource : listSearchSignalOrigin) {
			addToSignalSource(signalSource.getSignalDeclaration().getVhdlNode(),
					register, cmptHierrar, nbHierrar, signalSource.getHdlEntity(),
					signalSource.getHdlArchitecture(), signalSource.getType());
		}
	}

	private void addToSignalSource(VHDLNode child, Object signalSource, int cmptHierar,
			int nbHierarchie, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, String type) {
		if (signalSource instanceof RegisterInput  ||
				signalSource instanceof Register  ||
				signalSource instanceof Input) {
			RegisterInputSource registerInputSource = new RegisterInputSource(child, hdlEntity, hdlEntity.getEntity().getId(), ((RegisterInput)signalSource).toString(), cmptHierar);
			((RegisterInput)signalSource).addSourceRegisterInput(registerInputSource);
			if (type.equalsIgnoreCase("Instance Output")) {
				registerInputSource.setStopCondition(StopConditionE.IO);
			} else if (type.equalsIgnoreCase("Input Port")) {
				registerInputSource.setStopCondition(StopConditionE.IO_PAD);
			} else {
				continueSearchRead(registerInputSource, cmptHierar, nbHierarchie, hdlEntity, hdlArchitecture);
			}
		} else if (signalSource instanceof RegisterInputSource) {
			RegisterInputSource registerInputSource = new RegisterInputSource(child, hdlEntity, hdlEntity.getEntity().getId(), ((RegisterInputSource)signalSource).getTarget(), ((RegisterInputSource)signalSource).getParent(), cmptHierar);
			((RegisterInputSource)signalSource).addSourceRegisterInput(registerInputSource);
			if (type.equalsIgnoreCase("Instance Output")) {
				registerInputSource.setStopCondition(StopConditionE.IO);
			} else if (type.equalsIgnoreCase("Input Port")) {
				registerInputSource.setStopCondition(StopConditionE.IO_PAD);
			} else {
				continueSearchRead(registerInputSource, cmptHierar, nbHierarchie, hdlEntity, hdlArchitecture);
			}
		} else {
		}
	}
	private void continueSearchRead(RegisterInputSource registerInputSource,
			int cmptHierar, int nbHierarchie, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
			cmptHierar++;
		if (cmptHierar >= nbHierarchie) {
			return;
		}
		
		if (registerInputSource.getRead() instanceof InterfaceDeclaration){
			System.out.println(StopConditionE.IO_PAD.toString()+"  "+registerInputSource.toString()+"  "+cmptHierar);
			registerInputSource.setStopCondition(StopConditionE.IO_PAD);
			return;
		} else if (registerInputSource.getRead() instanceof AssociationElement) {
			System.out.println(StopConditionE.IO.toString()+"  "+registerInputSource.toString()+"  "+cmptHierar);
			registerInputSource.setStopCondition(StopConditionE.IO);
			return;
		}
			 List<String> listOperand = registerInputSource.getListOperand();

			for (String operand : listOperand) {
				if (!search(registerInputSource, operand, hdlEntity, hdlArchitecture, cmptHierar, nbHierarchie)) {
					if (! search(registerInputSource, ToolManager.getVectorName(operand), hdlEntity, hdlArchitecture, cmptHierar, nbHierarchie)) {
						List<SignalSource> listSearchSignalOrigin = ToolManager.searchSignalOrigin(operand, hdlEntity, hdlArchitecture, true);
						if (listSearchSignalOrigin.isEmpty()) {
							String structName = (operand.toString().indexOf("(") == -1 ? 
									(operand.toString().indexOf(".") == -1 ? operand.toString() : operand.toString().substring(0, operand.toString().indexOf("."))) 
									: operand.toString().substring(0, operand.toString().indexOf("(")));
							if (! structName.equalsIgnoreCase(operand)) {
								listSearchSignalOrigin = ToolManager.searchSignalOrigin(structName, hdlEntity, hdlArchitecture, true);
//							} else {
//								System.out.println(StopConditionE.STATE_MACHINE.toString()+"  "+registerInputSource.toString()+ "  " +cmptHierar);
//								registerInputSource.setStopCondition(StopConditionE.STATE_MACHINE);
							}
						}

					}
				}
			}

	}


	
}
