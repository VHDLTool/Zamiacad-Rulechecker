/* 
 * Copyright 2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jan 18, 2009
 */
package org.zamia.plugin.tool.vhdl.manager;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.EdgeE;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.VHDLNode;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class ClockSignalManager extends ToolManager {

	private boolean log = true;

	private boolean logFile = true;

	private static Process process;
	
	private static VHDLNode clockStatement;

	private static VHDLNode clockSequentialIf = null;

	private static boolean addSignal;

	private static ClockSignal signalFind;

	private static ListUpdateE info;

	private static HdlEntity hdlEntity;

	private static HdlArchitecture hdlArchitecture;

	private static HdlFile hdlFile;
	
	/**
	 * method is called by vhdl tool pull down menu
	 */
	public void run(IAction action) {
		init(log, logFile);

		zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return;
		}
		
		init(zPrj);
		
		try {
			getClockSignal();
//			dumpXml(listHdlFile, "REQ_FEAT_FN15", "Clock Identification");
			logger.info("Rule Checker: tool clock identification (REQ_FEAT_FN15) has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message ClockSignalManager", e);
		}
		

		close();
	}


	public static ClockSignal searchClock(VHDLNode node, boolean _addSignal) {
		signalFind = null;
		addSignal = _addSignal;
		searchClock(node);
		return signalFind;
	}
	
	private static void searchClock(VHDLNode node) {
		
		if (node == null) {return;}
		
		VHDLNode child;
		for (int i=0; i<node.getNumChildren(); i++){
			child = node.getChild(i);
			if (child instanceof SequenceOfStatements) {
				if (findClock(child)) {
					return; // on arrete la recherche
				}
			}
			if(child!= null) {
				searchClock(child);
			}
		}
		
	}

	private static boolean findClock(VHDLNode node) {

		clockStatement = node;
		VHDLNode child;
		for (int j=0; j<node.getNumChildren(); j++){
			child = node.getChild(j);
			if (child instanceof SequentialIf) {
				clockSequentialIf = child;
				VHDLNode subChild;
				for (int k=0; k<child.getNumChildren(); k++){
					subChild = child.getChild(k);
					if (subChild instanceof OperationName) {
						if (findClock(subChild, "Cas1"))
						return true;
					} else if (subChild instanceof OperationLogic) {
						if (findClock(subChild, "Cas2"))
						return true;
					}
				}

			}
		}

		return false;
	}

	private static boolean findClock(VHDLNode node, String cas) {
		switch (cas) {
		case "Cas1":

			if (node.toString().contains("RISING_EDGE") || node.toString().contains("FALLING_EDGE")) {
				for (int i = 0; i < ((OperationName)node).getName().getNumExtensions(); i++) {
					VHDLNode child = ((OperationName)node).getName().getExtension(i).getChild(0);
					if (child != null) {
						return addSignalClock(child, node.toString().contains("RISING_EDGE") ? EdgeE.RISING : EdgeE.FALLING);
					}
					
				}

			} else if (node.toString().contains("'EVENT")) {
				return addSignalClock(node, EdgeE.BOTH);
			}
			break;
		case "Cas2":
			List<Pair<VHDLNode, String>> listSignal = searchSignal(node);
			if (listSignal == null || listSignal.isEmpty()) {
				return false;
			}
			Pair<VHDLNode, String> signal = listSignal.get(0);
			if (signal == null) {
				return false;
			}
			if (!searchKeyWord(node, signal)) {
			
				VHDLNode child;
				for (int k=0; k<node.getNumChildren(); k++){
					child = node.getChild(k);
					if (child instanceof OperationName) {
						return findClock(child, "Cas1");
					} else if (child instanceof OperationLogic) {
						return findClock(child, "Cas2");
					}
				}

			
			}
			break;
		default:
			write("wrong Case");
			return false;
			
		}
		return false;
	}
	
	private static boolean addSignalClock(VHDLNode child, EdgeE edge) {
		ClockSignal clockSignal = new ClockSignal(child, edge, 
				hdlFile.getLocalPath(), hdlEntity, hdlArchitecture, process.getSequentialProcess(), clockSequentialIf);
		clockSignal.setDeclarationType(hdlEntity, hdlArchitecture);
		clockSignal.setSequenceOfStatementsParent(clockStatement);
		if (addSignal) {
			process.addClockSignalElement(clockSignal);
		} else {
			signalFind = clockSignal;
		}
		return true;
	}


/**
 * search key word EVENT, STABLE
 * @param node
 * @param signalPair 
 * @return 
 */
	public static boolean searchKeyWord(VHDLNode node, Pair<VHDLNode, String> signalPair) {
		if (signalPair == null) { return false;}
		
		VHDLNode signal = signalPair.getFirst();
		
		VHDLNode child;
		for (int j=0; j<node.getNumChildren(); j++){
			child = node.getChild(j);
			if (child instanceof OperationName) {
				if (child.toString().contains(signal.toString())) {
					
					if (child.toString().contains("'EVENT")) {
						return addSignalClock(signal, signalPair.getSecond().equalsIgnoreCase("0") ? EdgeE.FALLING : EdgeE.RISING);
					}
				}
			} else if (child instanceof OperationLogic) {
				VHDLNode subChild_0,subChild_1;
				if (child.getNumChildren() == 2){
					subChild_0 = child.getChild(0);
					subChild_1 = child.getChild(1);
					// verif du not : 
					if (subChild_1 == null && subChild_0 instanceof OperationName) {
						
						if (subChild_0.toString().contains("'STABLE")) {
							return addSignalClock(signal, signalPair.getSecond().equalsIgnoreCase("0") ? EdgeE.FALLING : EdgeE.RISING);
						}
					}
				}

			}
		}
		return false;
	}


	public static Process getProcessClock(Process _process) {
		addSignal = true;
		process = _process;
		searchClock(process.getSequentialProcess(), addSignal);
		return process;
	}


	public static Map<String, HdlFile> getClockSignal() throws EntityException {
		info = updateInfo(info);
	
		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}

		ProcessManager.getProcess();
		
		info = ListUpdateE.YES;
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					hdlEntity = hdlEntityItem;
					if (hdlEntityItem.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntity.getListHdlArchitecture()) {
							hdlArchitecture = hdlArchitectureItem;
							if (hdlArchitectureItem.getListProcess() != null) {
								for (Process processItem : hdlArchitectureItem.getListProcess()) {
									processItem.clearClockSignal();
									process = processItem;
									searchClock(process.getSequentialProcess(), true);
									processItem = process;
								}
							}
						}
					}
				}
			}

		}
		return listHdlFile;

	}

	public static void resetInfo() {
		info = ListUpdateE.NO;
	}


}
