package org.zamia.plugin.tool.vhdl.manager;

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
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.RegisterInputRead;

public class LogicalConeReadManager extends ToolManager {


	private boolean log = true;

	private boolean logFile = true;

	private static ListUpdateE info;

	public final static Integer NB_HIERARCHIE = 10;

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
			getLogicalConeRead();
			//			dumpXml(listHdlFile, "REQ_FEAT_REG_ID", "register Identification");
			logger.info("Rule Checker: tool register source identification has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message LogicalConeReadManager", e);
		}


		close();
	}

	private Map<String, HdlFile> getLogicalConeRead()  throws EntityException {
		info = updateInfo(info);

		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}

		RegisterAffectationManager.getRegisterAffectation();
		InputCombinationalProcessManager.getInputCombinationalProcess();
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
													if (register.toString().equalsIgnoreCase("xmit_bit_sel") ){//&& register.getLocation().fLine == 107) {
														System.out.println("############register "+register.toString()+ "  loc "+register.getLocation()+ " entity "+hdlEntity.getEntity().getId());
														searchRead(register, hdlEntity);
														System.out.println("############ END register "+register.toString()+ "  loc "+register.getLocation());
													}
												}
											}

//											if (clockSignalItem.hasSynchronousReset()) {
//												for (ResetSignal resetSignal : clockSignalItem.getListResetSignal()) {
//													if (resetSignal.getListRegister() != null) {
//														for (RegisterInput register : resetSignal.getListRegister()) {
////															if (register.toString().equalsIgnoreCase("SYNCHRO_CLOCK")){// && register.getLocation().fLine == 107) {
//																System.out.println("############register "+register.toString()+ "  loc "+register.getLocation()+ " entity "+hdlEntity.getEntity().getId());
//																searchRead(register, hdlEntity);
//																System.out.println("############ END register "+register.toString()+ "  loc "+register.getLocation());
////															}
//														}
//													}
//
//												}
//											}
										}
//									} else {
//										for (RegisterInput registerInput : processItem.getListInput()) {
//											System.out.println("############register "+registerInput.toString()+ "  loc "+registerInput.getLocation()+ " entity "+hdlEntity.getEntity().getId());
//											searchRead(registerInput, hdlEntity);
//											System.out.println("############ END register "+registerInput.toString()+ "  loc "+registerInput.getLocation());
//										}
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





	private void searchRead(RegisterInput register, HdlEntity hdlEntity) {
		hdlEntity.searchReadSignalSource(register, getVectorName(register.toString()), 1, NB_HIERARCHIE);
//		List<RegisterInputRead> listReadRegisterInputToUsed = new ArrayList<RegisterInputRead>();
//		for (int i = 2; i <= NB_HIERARCHIE; i++) {
//			List<RegisterInputRead> listReadRegisterInput = register.getListReadRegisterInputClone();
//			for (int j = 1; j < i; j++) {
//				
//			}
//			for (RegisterInputRead registerInputRead : listReadRegisterInput) {
//				if (registerInputRead.isUsed()) {
//					continue;
//				}
//				Target target = registerInputRead.getTarget();
//				if (target == null) {
//					continue;
//				}
//				if (target.getName().toString().equalsIgnoreCase(register.toString())) {
//					continue;
//				}
//				registerInputRead.setUsed();
//				listReadRegisterInputToUsed.add(registerInputRead);
//				registerInputRead.getHdlEntity().searchReadSignalSource(registerInputRead, getVectorName(target.getName().toString()));
//			}
//			register.updateListReadRegisterInput(listReadRegisterInputToUsed);
//		}
		
		
		
		if (register.getListReadRegisterInput().size() == 0) {
			System.out.println("PB READ ****************************");
		}
		
		for (RegisterInputRead registerInputRead : register.getListReadRegisterInput()) {
			System.out.println("READ "+registerInputRead.toString()+" loc "+registerInputRead.getLocation());
			dump(registerInputRead, 0);
		}
	}

	private String getIncrPrefix(int incr)
	{
		String pref="";
		for (int i=0; i<incr ; i++){
			pref=pref+" ";
		}
		return pref;
	}


	private void dump(RegisterInputRead registerInputRead, int incr) {
		incr++;
		for (RegisterInputRead registerRead : registerInputRead.getListReadRegisterInput()) {
			System.out.println(getIncrPrefix(incr)+"READ "+registerRead.toString()+" loc "+registerRead.getLocation());
			dump(registerRead, incr);
		}
		
	}

	public static void resetInfo() {
		info = ListUpdateE.NO;
	}

}
