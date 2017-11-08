package org.zamia.plugin.tool.vhdl.manager;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.zamia.SourceLocation;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.SignalSource;

public class RegisterSourceManager extends ToolManager {

	private boolean log = true;

	private boolean logFile = true;

	private static ListUpdateE info;

	
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
			getRegisterSource();
//			dumpXml(listHdlFile, "REQ_FEAT_REG_ID", "register Identification");
			logger.info("Rule Checker: tool register source identification has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message RegisterSourceManager", e);
		}
		

		close();
	}


	public static Map<String, HdlFile> getRegisterSource()  throws EntityException {
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
													for (String operand : register.getListOperand()) {
														searchRegisterOrigin(register, register.getLocation(), operand, hdlEntity, hdlArchitecture);
														
													}
												}
											}
											
											if (clockSignalItem.hasSynchronousReset()) {
												for (ResetSignal resetSignal : clockSignalItem.getListResetSignal()) {
													if (resetSignal.getListRegister() != null) {
														for (RegisterInput register : resetSignal.getListRegister()) {
															for (String operand : register.getListOperand()) {
																searchRegisterOrigin(register, register.getLocation(), operand, hdlEntity, hdlArchitecture);
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

		}

		return listHdlFile;

	}

	
	private static void searchRegisterOrigin(RegisterInput register, SourceLocation sourceLocation, String signalName, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
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
			
			if (signalSource != null && signalSource.getSignalDeclaration() != null) {
				HdlFile hdlFile2 = listHdlFile.get("/"+sourceLocation.fSF.getLocalPath());
				ClockSource clockSourceRegister = hdlFile2.isSignalRegister(signalSource);
				if (clockSourceRegister == null) {
					List<String> listOperand = signalSource.getListOperand();
					for (String operand : listOperand) {
						if (signalName.equalsIgnoreCase(operand)) { return;}
						searchRegisterOrigin(register, signalSource.getLocation(), operand, signalSource.getHdlEntity(), signalSource.getHdlArchitecture());
					}
				} else {
					signalSource.addClockSourceRegister(clockSourceRegister);
					register.addRegisterSource(signalSource);
				}
			}
		}
	}


	public static void resetInfo() {
		info = ListUpdateE.NO;
	}


}
