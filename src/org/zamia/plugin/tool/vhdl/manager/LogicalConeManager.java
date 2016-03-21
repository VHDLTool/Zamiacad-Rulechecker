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
import org.zamia.plugin.tool.vhdl.Input;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.Register;
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.RegisterInputRead;
import org.zamia.plugin.tool.vhdl.RegisterInputSource;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.SignalSource;
import org.zamia.plugin.tool.vhdl.VhdlSignalDeclaration;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.AssociationList;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationConcat;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialCase;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.VHDLNode;

public class LogicalConeManager extends ToolManager {


	private boolean log = true;

	private boolean logFile = true;

	private static ListUpdateE info;

	final static Integer NB_HIERARCHIE = 5;

	final static Integer NB_HIERARCHIE_INT = 1;

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
			getLogicalCone();
			//			dumpXml(listHdlFile, "REQ_FEAT_REG_ID", "register Identification");
			logger.info("Rule Checker: tool register source identification has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message LogicalConeManager", e);
		}


		close();
	}

	private Map<String, HdlFile> getLogicalCone()  throws EntityException {
		info = updateInfo(info);

		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}

		RegisterAffectationManager.getRegisterAffectation();

		info = ListUpdateE.YES;

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntity : hdlFile.getListHdlEntity()) {
					if (hdlEntity.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitecture : hdlEntity.getListHdlArchitecture()) {
							if (hdlArchitecture.getListProcess() != null) {
								for (Process processItem : hdlArchitecture.getListProcess()) {
									if (processItem.isSynchronous()) {
										for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
											if (clockSignalItem.getListRegister() != null) {
												for (RegisterInput register : clockSignalItem.getListRegister()) {
														System.out.println("############register "+register.toString()+ "  loc "+register.getLocation());
														searchOrigin(register, hdlEntity, hdlArchitecture, NB_HIERARCHIE);
														System.out.println("############ END register "+register.toString()+ "  loc "+register.getLocation());
												}
											}

											if (clockSignalItem.hasSynchronousReset()) {
												for (ResetSignal resetSignal : clockSignalItem.getListResetSignal()) {
													if (resetSignal.getListRegister() != null) {
														for (RegisterInput register : resetSignal.getListRegister()) {
																System.out.println("############register "+register.toString()+ "  loc "+register.getLocation());
																searchOrigin(register, hdlEntity, hdlArchitecture, NB_HIERARCHIE);
																System.out.println("############ END register "+register.toString()+ "  loc "+register.getLocation());
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
				}
			}

		}




		return listHdlFile;

	}


	public static void searchOrigin(RegisterInput register, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int nbHierarchie) {
		Integer cmpt = 0;
		searchParent(register, register.getVhdlNode(), hdlEntity, hdlArchitecture, 0, register.toString());
		for (String operand : register.getListOperand()) {
			searchRegisterOrigin(register, operand, hdlEntity, hdlArchitecture, 0);
		}
		cmpt++;

		for (int i = cmpt; i < nbHierarchie; i++) {
			List<SignalSource> source = register.getSource();

			register.clearSource();

			for (SignalSource signalSource : source) {
				if (signalSource.getSignalDeclaration().getType().equalsIgnoreCase("Input Port") ||
						signalSource.getSignalDeclaration().getType().equalsIgnoreCase("Instance Output")) {
					register.addSource(signalSource);
					continue;
				}
				// cas registre meme clock
				if (register.isSameClock(signalSource)) { continue;}
				searchRegisterOrigin(register, signalSource.toString(), signalSource.getHdlEntity(), signalSource.getHdlArchitecture(), 0);
			}

		}
		
//		for (SignalSource signalSource : register.getSource()) {
//			System.out.println("SOURCE  "+ signalSource.toString()+ " loc "+signalSource.getLocation()+" type "+signalSource.getSignalDeclaration().getType());
//		}

	}


	private static void searchRegisterOrigin(RegisterInput register, String signalName,
			HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int cmpt) {
		if (register.toString().equalsIgnoreCase(signalName)) {
			return;
		}
		List<SignalSource> listSearchSignalOrigin = searchSignalOrigin(signalName, hdlEntity, hdlArchitecture, true);
		if (listSearchSignalOrigin.isEmpty()) {
			String structName = (signalName.toString().indexOf("(") == -1 ? 
					(signalName.toString().indexOf(".") == -1 ? signalName.toString() : signalName.toString().substring(0, signalName.toString().indexOf("."))) 
					: signalName.toString().substring(0, signalName.toString().indexOf("(")));
			if (! structName.equalsIgnoreCase(signalName)) {
				listSearchSignalOrigin = searchSignalOrigin(structName, hdlEntity, hdlArchitecture, true);
			}
		}
		for (SignalSource signalSource : listSearchSignalOrigin) {

			if (signalSource.getSignalDeclaration().getType().equalsIgnoreCase("Input Port")) {
				//SOURCE
				register.addSource(signalSource);
				continue;
			}
			if (signalSource.getSignalDeclaration().getType().equalsIgnoreCase("Instance Output")) {
				//SOURCE
				register.addSource(signalSource);
				continue;
			}
			if (!(cmpt < NB_HIERARCHIE_INT)) {
				register.addSource(signalSource);
				continue;
			}
			searchParent(register, signalSource.getSignalDeclaration().getVhdlNode(), hdlEntity, hdlArchitecture, cmpt, signalName);
			if (signalSource != null && signalSource.getSignalDeclaration() != null && cmpt < NB_HIERARCHIE_INT) {
				List<String> listOperand = signalSource.getListOperand();
				for (String operand : listOperand) {

					if (signalName.equalsIgnoreCase(operand)) { return;}
					searchRegisterOrigin(register, operand, signalSource.getHdlEntity(), signalSource.getHdlArchitecture(), cmpt++);
				}
			}
		}

	}


	private static void searchParent(RegisterInput register, VHDLNode vhdlNode,
			HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int i, String signalName) {
		VHDLNode parent = (VHDLNode) vhdlNode.getParent();
		if (parent == null) { return;}
		if (parent instanceof SequentialProcess ||
				parent instanceof AssociationList ||
				parent instanceof Architecture) { return;}

		if (parent instanceof SequentialIf) {
			searchInOp(register, parent, hdlEntity, hdlArchitecture, i, signalName);
		} else if (parent instanceof SequenceOfStatements ||
				parent instanceof SequentialCase ||
				(parent.getClass().getSimpleName().equalsIgnoreCase("Alternative"))) {

		} else {
			System.out.println("parent "+parent.toString()+"  type "+parent.getClass().getSimpleName()+ "  loc "+parent.getLocation());
		}
		searchParent(register, parent, hdlEntity, hdlArchitecture, i, signalName);
	}

	private static void searchInOp(RegisterInput register, VHDLNode child,
			HdlEntity hdlEntity, HdlArchitecture hdlArchitecture, int i, String signalName) {
		if (child == null) { return;}

		int numChildren2 = child.getNumChildren();
		for (int j = 0; j < numChildren2; j++) {
			VHDLNode child2 = child.getChild(j);
			if (child2 instanceof OperationCompare) {
				searchInOp(register, child2, hdlEntity, hdlArchitecture, j, signalName);
			} else if (child2 instanceof OperationLogic) {
				searchInOp(register, child2, hdlEntity, hdlArchitecture, j, signalName);
			}else  if (child2 instanceof OperationName) {
				System.out.println("searchInOp "+child2.toString()+"  loc "+ child2.getLocation());
				if (! child2.toString().equalsIgnoreCase(signalName)) {
					searchRegisterOrigin(register, child2.toString(), hdlEntity, hdlArchitecture, i);
				}
			} else if (child2 instanceof OperationConcat) {
				searchInOp(register, child2, hdlEntity, hdlArchitecture, j, signalName);
			}

		}
	}

	public static void resetInfo() {
		info = ListUpdateE.NO;
	}


}
