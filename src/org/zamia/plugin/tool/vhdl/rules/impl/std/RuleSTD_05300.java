package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.Sensitivity;
import org.zamia.plugin.tool.vhdl.manager.InputCombinationalProcessManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

public class RuleSTD_05300 extends RuleManager {

	//Sensitivity list for combinational processes
	
	RuleE rule = RuleE.STD_05300;

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
			hdlFiles = InputCombinationalProcessManager.getInputCombinationalProcess();
		} catch (EntityException e) {
			logger.error("some exception message RuleSTD_05300", e);
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
						System.out.println("############## PROCESS " +processItem.getLabel() + " loc "+ processItem.getLocation().fLine);
						if (processItem.isSynchronous()) { continue;}
						System.out.println("############## PROCESS " +processItem.getLabel() + " loc "+ processItem.getLocation().fLine);
						System.out.println("----------------- sensitivity ");
						for (Sensitivity sensitivity : processItem.getListSensitivity()) {
							System.out.println(sensitivity.toString()+ "  type "+sensitivity.getTypeS()+ " type "+ sensitivity.getType());
						}
						System.out.println("----------------- input ");
						for (RegisterInput input : processItem.getListInput()) {
							System.out.println(input.toString()+ "  type "+input.getTypeS()+ " type "+ input.getType());
						}
						System.out.println("############## END PROCESS " +processItem.getLabel() + " loc "+ processItem.getLocation().fLine);
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
			for (RegisterInput input : processItem.getListInput()) {
				if (checkSensitivityUsedInSignal(sensitivity, input)) {
					find = true;
				}
			}
			if (!find && (sensitivity.isVector() || sensitivity.isPartOfVector())) {
				// check partial used
				for (RegisterInput input : processItem.getListInput()) {
					if (input.getVectorName().equalsIgnoreCase(sensitivity.getVectorName())) {
						find = true;
					}
				}

				if (find) {
					for (int i = sensitivity.getIndexMin() ; i <= sensitivity.getIndexMax(); i++) {
						boolean findIndex = false;
						String vectorName = sensitivity.toString()+"("+i+")";
						for (RegisterInput input : processItem.getListInput()) {
							if (vectorName.toString().equalsIgnoreCase(input.toString())) {
								findIndex = true;
							}
						}
						if (!findIndex) {
							cmptViolation++;
							addViolation(racine, NOT_DEFINED, vectorName, sensitivity.getLocation().fLine,
									hdlFile.getLocalPath(), entity, architecture, processItem);
						}
					}
				}
				
			}
			if (!find) {
				cmptViolation++;
				addViolation(racine, NOT_USED, sensitivity.toString(), sensitivity.getLocation().fLine,
						hdlFile.getLocalPath(), entity, architecture, processItem);

			}
		}
		
	}


	private boolean checkSensitivityUsedInSignal(Sensitivity sensitivity, RegisterInput input) {
		boolean find = false;
			if (sensitivity.toString().equalsIgnoreCase(input.toString())) {
				find = true;
			}
		return find;
	}


	private void checkSensitivityList(Process processItem) {
		for (RegisterInput input : processItem.getListInput()) {
			checkSensitivityRegister(input, processItem.getListSensitivity(),
					processItem);
		}

	}

	private void checkSensitivityRegister(RegisterInput input, ArrayList<Sensitivity> listSensitivity,
			Process process) {
			boolean find = false;
			for (Sensitivity sensitivity : listSensitivity) {
				if (input.toString().equalsIgnoreCase(sensitivity.toString())) {
					find = true;
				}
			}
			// case not defined : partial used  (vector)
			if (!find && (input.isVector() || input.isPartOfVector())) {
				for (Sensitivity sensitivity : listSensitivity) {
					if (input.getVectorName().equalsIgnoreCase(sensitivity.toString())) {
						find = true;
					}
				}
			}
			// case not defined : partial defined  (vector)
			if (!find && (input.isVector()||input.isPartOfVector())) {
				// check partial defined
				for (Sensitivity sensitivity : listSensitivity) {
					if (input.getVectorName().equalsIgnoreCase(sensitivity.getVectorName())) {
						find = true;
					}
				}
				
				if (find) {
					int indexMin = 0;
					int indexMax = 0;
					if (input.isAscending()) {
						indexMin = input.getLeft();
						indexMax = input.getRight();
					} else {
						indexMax = input.getLeft();
						indexMin = input.getRight();
					}
					for (int i = indexMin ; i <= indexMax; i++) {
						boolean findIndex = false;
						String vectorName = input.toString()+"("+i+")";
						for (Sensitivity sensitivity : listSensitivity) {
							if (vectorName.toString().equalsIgnoreCase(sensitivity.toString())) {
								findIndex = true;
							}
						}
						if (!findIndex) {
							cmptViolation++;
							addViolation(racine, NOT_DEFINED, vectorName, input.getLocation().fLine,
									hdlFile.getLocalPath(), entity, architecture, process);
						}
					}
				}
			}

			if (!find) {
				cmptViolation++;
				addViolation(racine, NOT_DEFINED, input.toString(), input.getLocation().fLine,
						hdlFile.getLocalPath(), entity, architecture, process);
			}
	}

	
	private void addViolation(Element racine, String error, String sensitivityName, int sensitivityLoc,
			String fileName, Entity entity, Architecture architecture, 
			Process process) {
		
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

		Element registerNameElement = document.createElement(NodeType.SENSITIVITY.toString()+NodeInfo.NAME.toString());
		registerNameElement.setTextContent(sensitivityName);
		registerElement.appendChild(registerNameElement);

		Element registerLocElement = document.createElement(NodeType.SENSITIVITY.toString()+NodeInfo.LOCATION.toString());
		registerLocElement.setTextContent(String.valueOf(sensitivityLoc));
		registerElement.appendChild(registerLocElement);

	}
}
