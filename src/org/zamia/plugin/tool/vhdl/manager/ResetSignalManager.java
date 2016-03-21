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
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.VHDLNode;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class ResetSignalManager extends ToolManager {

	private boolean log = true;

	private boolean logFile = true;

	private static ClockSignal clockSignal;

	private static VHDLNode clockSequentialIf = null;

	private static ListUpdateE info;

	private static HdlEntity hdlEntity;

	private static HdlArchitecture hdlArchitecture;

	private static Process process;

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
			getResetSignal();
//			dumpXml(listHdlFile, "REQ_FEAT_FN18", "Reset Identification");
			logger.info("Rule Checker: tool reset identification (REQ_FEAT_FN18) has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message ResetSignalManager", e);
		}


		close();
	}

	/**
	 * get reset signal 
	 * this method call clock signal method for find synchronous process
	 * @return
	 * @throws EntityException 
	 * @throws CloneNotSupportedException 
	 */
	public static Map<String, HdlFile> getResetSignal() throws EntityException {
		info = updateInfo(info);

		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}
		ClockSignalManager.getClockSignal();
		
		info = ListUpdateE.YES;


		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					hdlEntity = hdlEntityItem;
					if (hdlEntityItem.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
							hdlArchitecture = hdlArchitectureItem;
							if (hdlArchitectureItem.getListProcess() != null) {
								for (Process processItem : hdlArchitectureItem.getListProcess()) {
									process = processItem;
									if (processItem.getListClockSignal() != null) {
										for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
											clockSignalItem.clearListResetSignal();
											clockSignal = clockSignalItem;
											searchResetSignal();
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return listHdlFile;

	}

	/**
	 *search reset signal in sequence of statement 
	 */
	private static void searchResetSignal() {
		VHDLNode parent = clockSignal.getSequenceOfStatementsParent();
		while (!(parent instanceof SequentialProcess)) {
			parent =  (VHDLNode) parent.getParent();
		}

		VHDLNode node;
		for (int i = 0; i < parent.getNumChildren(); i++) {
			node = parent.getChild(i);
			if (node instanceof SequenceOfStatements) {
				VHDLNode child;
				for (int j = 0; j < node.getNumChildren(); j++) {
					child = node.getChild(j);
					search(child);
				}
			}
		}

	}

	/**
	 * search reset signal per recursivity
	 * node struct of vhdl
	 * SequenceOfStatements  >> Child : SequentialIf >> Child : OperationCompare
	 * @param node
	 */
	private static void search(VHDLNode node) {
		if (node != null){

			if (node instanceof SequentialIf) {
				clockSequentialIf = node;
				List<Pair<VHDLNode, String>> listSearchSignal = searchSignal(node);
				if (listSearchSignal == null) { return;}
				for (Pair<VHDLNode, String> signalFind : listSearchSignal) {
					if (signalFind != null) {
						if (signalFind.getFirst().toString().equalsIgnoreCase(clockSignal.toString())) { continue;}
						ResetSignal signal = new ResetSignal(signalFind.getFirst(), signalFind.getSecond(),
								hdlFile.getLocalPath(), hdlEntity, hdlArchitecture, process.getSequentialProcess(),
								clockSignal, clockSequentialIf);
						
						signal.setDeclarationType(hdlEntity, hdlArchitecture);
						ClockSignal searchClock = ClockSignalManager.searchClock(node, false);
						if (searchClock == null) {
							clockSignal.addResetSignalElement(signal);
						} else if ( searchClock.toString().equalsIgnoreCase(clockSignal.toString())){
							clockSignal.addResetSignalElement(signal);
							VHDLNode child;
							for (int i=0; i<node.getNumChildren(); i++){
								child = node.getChild(i);
								search(child);
							}

						}
					}
				}
				

			} else if (node instanceof SequenceOfStatements) {

				VHDLNode child;
				for (int i=0; i<node.getNumChildren(); i++){
					child = node.getChild(i);
					search(child);
				}

			}

		}

	}

	public static void resetInfo() {
		info = ListUpdateE.NO;
	}

}
