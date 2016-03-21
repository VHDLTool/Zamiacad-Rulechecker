package org.zamia.plugin.tool.vhdl.manager;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.Register;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.Signal;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.Range;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialCase;
import org.zamia.vhdl.ast.SequentialFor;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.SequentialVariableAssignment;
import org.zamia.vhdl.ast.VHDLNode;

public class RegisterAffectationManager extends ToolManager {

	public static final String REG = "REG_";

	private boolean log = true;

	private boolean logFile = true;

	private static ListUpdateE info;

	private static HdlEntity hdlEntity;
	
	private static HdlArchitecture hdlArchitecture;

	private static int cmpt;

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
		cmpt = 0;
		try {
			getRegisterAffectation();
//			dumpXml(listHdlFile, "REQ_FEAT_REG_ID", "register Identification");
			logger.info("Rule Checker: tool register identification (REQ_FEAT_REG_ID) has been executed with success. "+cmpt);
		} catch (EntityException e) {
			logger.error("some exception message RegisterAffectationManager", e);
		}
		

		close();
	}

	public static Map<String, HdlFile> getRegisterAffectation() throws EntityException {
		info = updateInfo(info);
		
		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}

		ResetSignalManager.getResetSignal();
		ClockSignalSourceManager.getClockSourceSignal();
		
		info = ListUpdateE.YES;
		num = 0;
		
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
									if (!processItem.isSynchronous()) { continue;}
									
									for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
										if (clockSignalItem.getSequenceOfStatementsChild() != null) { 
											SequenceOfStatements sequenceOfStatementsChild = clockSignalItem.getSequenceOfStatementsChild();
											searchRegister(sequenceOfStatementsChild, clockSignalItem, clockSignalItem.getClockSource(), clockSignalItem);

										}
										if (clockSignalItem.hasSynchronousReset()) {
											for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
												SequenceOfStatements sequenceOfStatementsChild = resetSignalItem.getSequenceOfStatementsChild();
												searchRegister(sequenceOfStatementsChild, resetSignalItem, clockSignalItem.getClockSource(), clockSignalItem);

											}
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
	
	
	private static void searchRegister(
			VHDLNode sequenceOfStatementsChild, Signal signal, ClockSource clockSource, ClockSignal clockSignal) {
		int numChildren = sequenceOfStatementsChild.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			VHDLNode child = sequenceOfStatementsChild.getChild(i);
			if (child instanceof SequentialIf) {
				searchRegister(child, signal, clockSource, clockSignal);
			} else if (child instanceof SequenceOfStatements) {
				searchRegister(child, signal, clockSource, clockSignal);
			} else if (child instanceof SequentialCase) {
				searchRegister(child, signal, clockSource, clockSignal);
			} else if (child instanceof SequentialFor) {
				searchRegister(child, signal, clockSource, clockSignal);
			} else if (child != null && child.getClass().getSimpleName().equalsIgnoreCase("Alternative")) { // Alternative est une inner class de la classe  SequentialCase declarer en private
				searchRegister(child, signal, clockSource, clockSignal);
			} else if (child instanceof SequentialSignalAssignment) {
				cmpt++;
				if (signal instanceof ClockSignal) {
					num = signal.addRegister(new Register((SequentialSignalAssignment)child, hdlEntity, hdlArchitecture, clockSignal, clockSource, REG, num));
				} else {
					signal.addRegister(new Register((SequentialSignalAssignment)child, hdlEntity, hdlArchitecture, clockSignal, clockSource, REG, -1));
				}
			} else if (child instanceof SequentialVariableAssignment || 
					child instanceof OperationCompare || 
					child instanceof Range || 
					child instanceof OperationName) {
				// do nothing
			} else if (child != null){
//				logger.debug("########### searchRegister child "+child.getClass().getSimpleName()+ " loc "+child.getLocation());
			}
		}
		
	}


	public static void resetInfo() {
		info = ListUpdateE.NO;
	}

}
