package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.Sensitivity;
import org.zamia.plugin.tool.vhdl.Signal;
import org.zamia.plugin.tool.vhdl.manager.RegisterAffectationManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

public class RuleSTD_05000 extends RuleManager {

	//Sensitivity List for Synchronous Processes

	RuleE rule = RuleE.STD_05000;

	private int cmptViolation;

	private Element racine;

	private Entity entity;

	private Architecture architecture;

	private HdlFile hdlFile;


	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		String fileName = "";

		Map<String, HdlFile> hdlFiles;

		try {
			hdlFiles = RegisterAffectationManager.getRegisterAffectation();
		} catch (EntityException e) {
			logger.error("some exception message RuleSTD_05000", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		racine = initReportFile(ruleId, rule.getType(), rule.getRuleName());

		cmptViolation = 0;
		for(Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
			HdlFile hdlFileItem = entry.getValue();
			if (hdlFileItem.getListHdlEntity() == null) { continue;}
			hdlFile = hdlFileItem;
			for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
				entity = hdlEntityItem.getEntity();
				if (hdlEntityItem.getListHdlArchitecture() == null) { continue;}
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					architecture = hdlArchitectureItem.getArchitecture();
					if (hdlArchitectureItem.getListProcess() == null) { continue;}
					for (Process processItem : hdlArchitectureItem.getListProcess()) {
						if (!processItem.isSynchronous()) { continue;}
						checkSensitivityList(processItem);
						checkSensitivityUsed(processItem);
					}
				}
			}

		}
		if (cmptViolation != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType());
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (cmptViolation, fileName);


	}


	private void checkSensitivityUsed(Process processItem) {
		for (Sensitivity sensitivity : processItem.getListSensitivity()) {
			boolean find = false;
			for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
				if (checkSensitivityUsedInSignal(sensitivity, clockSignalItem)) {
					find = true;
				}

				if (!find && clockSignalItem.hasSynchronousReset()) {
					for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
						if (checkSensitivityUsedInSignal(sensitivity, resetSignalItem)) {
							find = true;
						}
					}
				}
			}
			if (!find && (sensitivity.isVector() || sensitivity.isPartOfVector())) {
				for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
					if (sensitivity.getVectorName().equalsIgnoreCase(clockSignalItem.getVectorName())) {
						find = true;
					}

					if (!find && clockSignalItem.hasSynchronousReset()) {
						for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
							if (sensitivity.getVectorName().equalsIgnoreCase(resetSignalItem.getVectorName())) {
								find = true;
							}
						}
					}
				}
				
				if (find) {
					for (int i = sensitivity.getIndexMin() ; i <= sensitivity.getIndexMax(); i++) {
						boolean findIndex = false;
						String vectorName = sensitivity.toString()+"("+i+")";
						for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
							if (vectorName.toString().equalsIgnoreCase(clockSignalItem.toString())) {
								findIndex = true;
							}
							if (!find && clockSignalItem.hasSynchronousReset()) {
								for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
									if (vectorName.toString().equalsIgnoreCase(resetSignalItem.toString())) {
										findIndex = true;
									}
								}
							}
						}
						
						if (!findIndex) {
							cmptViolation++;
							addViolation(racine, NOT_DEFINED, vectorName, sensitivity.getLocation().fLine,
									hdlFile.getLocalPath(), entity, architecture, processItem);
						}
					}
					
				}
				System.out.println("checkSensitivityUsed  "+ " type "+sensitivity.getType() );
			}
			if (!find) {
				cmptViolation++;
				addViolation(racine, NOT_USED, sensitivity.toString(), sensitivity.getLocation().fLine,
						hdlFile.getLocalPath(), entity, architecture, processItem);

			}
		}

	}


	private boolean checkSensitivityUsedInSignal(Sensitivity sensitivity, Signal signal) {
		boolean find = false;
		if (sensitivity.toString().equalsIgnoreCase(signal.toString())) {
			find = true;
		}
		return find;
	}


	private void checkSensitivityList(Process processItem) {
		for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
			checkSensitivityRegister(clockSignalItem, processItem.getListSensitivity(),
					processItem, clockSignalItem);

			if (clockSignalItem.hasSynchronousReset()) {
				for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
					checkSensitivityRegister(resetSignalItem, processItem.getListSensitivity(),
							processItem, clockSignalItem, resetSignalItem);
				}
			}
		}

	}

	private void checkSensitivityRegister(Signal signal, ArrayList<Sensitivity> listSensitivity,
			Process process, ClockSignal clockSignal) {
		checkSensitivityRegister(signal, listSensitivity, process, clockSignal, null);
	}

	private void checkSensitivityRegister(Signal signal, ArrayList<Sensitivity> listSensitivity,
			Process process, ClockSignal clockSignal, ResetSignal resetSignal) {
		boolean find = false;
		for (Sensitivity sensitivity : listSensitivity) {
			if (signal.toString().equalsIgnoreCase(sensitivity.toString())) {
				find = true;
			}
		}
		// case not defined : partial used  (vector)
		if (!find && (signal.isVector() || signal.isPartOfVector())) {
			System.out.println("test part signal "+ signal.toString()+ "  vector name "+signal.getVectorName());
			for (Sensitivity sensitivity : listSensitivity) {
				if (signal.getVectorName().equalsIgnoreCase(sensitivity.toString())) {
					find = true;
				}
			}
		}
		// case not defined : partial defined  (vector)
		if (!find && (signal.isVector() || signal.isPartOfVector())) {

			for (Sensitivity sensitivity : listSensitivity) {
				System.out.println("sensitivity "+sensitivity.toString()+ "  vectorname "+sensitivity.getVectorName());
				if (signal.getVectorName().equalsIgnoreCase(sensitivity.getVectorName())) {
					find = true;
				}
			}

			if (find) {

				System.out.println("test part signal "+ signal.toString()+ "  vector name "+signal.getVectorName());
				int indexMin = 0;
				int indexMax = 0;
				if (signal.isAscending()) {
					indexMin = signal.getLeft();
					indexMax = signal.getRight();
				} else {
					indexMax = signal.getLeft();
					indexMin = signal.getRight();
				}
				for (int i = indexMin ; i <= indexMax; i++) {
					boolean findIndex = false;
					String vectorName = signal.toString()+"("+i+")";
					for (Sensitivity sensitivity : listSensitivity) {
						System.out.println("sensitivity "+sensitivity.toString()+ "  vectorname "+sensitivity.getVectorName());
						if (vectorName.toString().equalsIgnoreCase(sensitivity.toString())) {
							findIndex = true;
							find = true;
						}
					}
					if (!findIndex) {
						cmptViolation++;
						addViolation(racine, NOT_DEFINED, vectorName, signal.getLocation().fLine,
								hdlFile.getLocalPath(), entity, architecture, process, clockSignal, resetSignal);
					}
				}
			}
		}

		if (!find) {
			cmptViolation++;
			addViolation(racine, NOT_DEFINED, signal.toString(), signal.getLocation().fLine,
					hdlFile.getLocalPath(), entity, architecture, process, clockSignal, resetSignal);
		}
	}

	private void addViolation(Element racine, String error, String sensitivityName, int sensitivityLoc,
			String fileName, Entity entity, Architecture architecture, 
			Process process) {
		addViolation(racine, error, sensitivityName, sensitivityLoc, fileName, entity, architecture, process, null);
	}

	private void addViolation(Element racine, String error, String sensitivityName, int sensitivityLoc,
			String fileName, Entity entity, Architecture architecture, 
			Process process, ClockSignal clockSignalItem) {
		addViolation(racine, error, sensitivityName, sensitivityLoc, fileName, entity, architecture,
				process, clockSignalItem, null);
	}

	private void addViolation(Element racine, String error, String sensitivityName, int sensitivityLoc,
			String fileName, Entity entity, Architecture architecture, 
			Process process, ClockSignal clockSignalItem, ResetSignal resetSignalItem) {
		Element registerElement = document.createElement(NodeType.SENSITIVITY.toString());
		racine.appendChild(registerElement);

		registerElement.appendChild(NewElement(document, "violationType"
				, error));

		Element fileNameElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
		fileNameElement.setTextContent(fileName);
		registerElement.appendChild(fileNameElement);

		Element entityNameElement = document.createElement(NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
		entityNameElement.setTextContent(entity.getId());
		registerElement.appendChild(entityNameElement);

		Element archiNameElement = document.createElement(NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString());
		archiNameElement.setTextContent(architecture.getId());
		registerElement.appendChild(archiNameElement);

		Element processNameElement = document.createElement(NodeType.PROCESS.toString()+NodeInfo.NAME.toString());
		processNameElement.setTextContent(process.getLabel());
		registerElement.appendChild(processNameElement);

		Element processLocElement = document.createElement(NodeType.PROCESS.toString()+NodeInfo.LOCATION.toString());
		processLocElement.setTextContent(String.valueOf(process.getLocation().fLine));
		registerElement.appendChild(processLocElement);

		if (clockSignalItem != null) {
			Element clockNameElement = document.createElement(NodeType.CLOCK_SIGNAL.toString()+NodeInfo.NAME.toString());
			clockNameElement.setTextContent(clockSignalItem.toString());
			registerElement.appendChild(clockNameElement);
			if (resetSignalItem != null) {
				Element resetNameElement = document.createElement(NodeType.RESET_SIGNAL.toString()+NodeInfo.NAME.toString());
				resetNameElement.setTextContent(resetSignalItem.toString());
				registerElement.appendChild(resetNameElement);
			}
		}

		Element registerNameElement = document.createElement(NodeType.SENSITIVITY.toString()+NodeInfo.NAME.toString());
		registerNameElement.setTextContent(sensitivityName);
		registerElement.appendChild(registerNameElement);

		Element registerLocElement = document.createElement(NodeType.SENSITIVITY.toString()+NodeInfo.LOCATION.toString());
		registerLocElement.setTextContent(String.valueOf(sensitivityLoc));
		registerElement.appendChild(registerLocElement);

	}
}
