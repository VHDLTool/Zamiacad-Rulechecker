package org.zamia.plugin.tool.vhdl.manager;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.Input;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.RegisterTypeE;
import org.zamia.vhdl.ast.ConditionalSignalAssignment;
import org.zamia.vhdl.ast.ConditionalWaveform;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationConcat;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.Range;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialCase;
import org.zamia.vhdl.ast.SequentialFor;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.SequentialVariableAssignment;
import org.zamia.vhdl.ast.VHDLNode;
import org.zamia.vhdl.ast.Waveform;
import org.zamia.vhdl.ast.WaveformElement;

public class InputCombinationalProcessManager extends ToolManager {

	public static final String INPUT = "INPUT_COMB_";

	private boolean log = true;

	private boolean logFile = true;

	private static ListUpdateE info;

	private static HdlEntity hdlEntity;
	
	private static HdlArchitecture hdlArchitecture;

	private static int num;
	
	@Override
	public void run(IAction arg0) {
		init(log, logFile);

		zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return;
		}
		
		init(zPrj);
		

		try {
			getInputCombinationalProcess();
//			dumpXml(listHdlFile, "REQ_FEAT_REG_ID", "register Identification");
			logger.info("Rule Checker: tool register identification (REQ_FEAT_REG_ID) has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message InputCombinationalProcessManager", e);
		}
		

		close();
	}

	public static Map<String, HdlFile> getInputCombinationalProcess() throws EntityException {
		info = updateInfo(info);

		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}

		ClockSignalManager.getClockSignal();
		info = ListUpdateE.YES;
		num = 0;
		
		long debut = System.currentTimeMillis();
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					hdlEntity = hdlEntityItem;
					if (hdlEntityItem.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
							hdlArchitecture = hdlArchitectureItem;
							if (hdlArchitectureItem.getListProcess() != null) {
								for (Process processItem : hdlArchitectureItem.getListProcess()) {
									if (processItem.isSynchronous()) { continue;}
									searchInput(processItem.getSequentialProcess(), processItem);
								}
							}
						}
					}
				}
			}

		}
		long fin = System.currentTimeMillis();
		System.out.println("temps ecoule : "+(fin-debut));
		return listHdlFile;

	}
	
	
	private static void searchInput(
			VHDLNode node, Process processItem) {
		int numChildren = node.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = node.getChild(i);
		 if (child instanceof SequenceOfStatements) {
			searchInput(child, processItem);
		 } else if (child instanceof SequentialIf) {
			 searchInOp(child, processItem);
		 } else if (child instanceof ConditionalSignalAssignment) {
				int numChildren2 = child.getNumChildren();
				for (int j = 0; j < numChildren2; j++) {
					VHDLNode child2 = child.getChild(j);
					if (child2 instanceof ConditionalWaveform) {
						searchInOp(child2, processItem);
					}
				}
			} else if (child instanceof SequentialCase) {
				int numChildren2 = child.getNumChildren();
				for (int j = 0; j < numChildren2; j++) {
					VHDLNode child2 = child.getChild(j);
					if (child2 instanceof OperationCompare) {
						searchInOp(child2, processItem);
					} else if (child2 instanceof OperationLogic) {
						searchInOp(child2, processItem);
					}else  if (child2 instanceof OperationName) {
						addNewInput(child2, processItem);
					} else if (child2 instanceof OperationConcat) {
						searchInOp(child2, processItem);
					} else if (child2 != null && child2.getClass().getSimpleName().equalsIgnoreCase("Alternative")) {
						searchInput(child2, processItem);
					}
				}
				searchInput(child, processItem);
			} else if (child instanceof SequentialFor) {
				searchInput(child, processItem);
			} else if (child instanceof SequentialSignalAssignment) {
				VHDLNode child2 = child.getChild(1);
				if (child2 instanceof Waveform) {
					VHDLNode child3 = child2.getChild(0);
					if (child3 instanceof WaveformElement) {
						VHDLNode child4 = child3.getChild(0);
						if (child4 instanceof OperationName) {
							addNewInput(child4, processItem);
						} else if (child4 instanceof OperationConcat) {
							searchInOpConcat(child4, processItem);
						}
					}
				}
				searchInput(child, processItem);
//				processItem.addInput(new Input((SequentialSignalAssignment)child, hdlEntity, hdlArchitecture));
			} else if (child instanceof SequentialVariableAssignment) {
				searchInOp(child, processItem);
			} else if (child instanceof Range || 
					child instanceof OperationName) {
				// do nothing
			} else if (child != null){
//				System.out.println("########### searchRegister child "+child.getClass().getSimpleName()+ " loc "+child.getLocation());
			}
		}
		
	}


	private static void searchInOp(VHDLNode child, Process processItem) {
		int numChildren2 = child.getNumChildren();
		for (int j = 0; j < numChildren2; j++) {
			VHDLNode child2 = child.getChild(j);
			if (child2 instanceof OperationCompare) {
				searchInOp(child2, processItem);
			} else if (child2 instanceof OperationLogic) {
				searchInOp(child2, processItem);
			}else  if (child2 instanceof OperationName) {
				addNewInput(child2, processItem);
			} else if (child2 instanceof OperationConcat) {
				searchInOp(child2, processItem);
			}

		}
 }


	private static void addNewInput(VHDLNode node, Process processItem) {
		Input input = new Input((OperationName)node, hdlEntity, hdlArchitecture, INPUT, num);
			System.out.println("addNewInput  "+((OperationName)node).toString()+" getType  "+input.getType());
		if (!input.getType().equals(RegisterTypeE.NAN)) {
			num = processItem.addInput(input);
			
		}
	}

	private static void searchInOpConcat(VHDLNode node, Process processItem) {
		if (node == null) { return;}
		int numChildren = node.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = node.getChild(i);
			if (child instanceof OperationName) {
				addNewInput(child, processItem);
			} else if (child instanceof OperationConcat) {
				searchInOpConcat(child, processItem);
			}
		}

		
	}

	public static void resetInfo() {
		info = ListUpdateE.NO;
	}


}
