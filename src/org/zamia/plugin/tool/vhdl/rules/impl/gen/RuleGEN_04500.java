package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.ArrayList;
import java.util.List;
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
import org.zamia.plugin.tool.vhdl.Violation;
import org.zamia.plugin.tool.vhdl.manager.RegisterAffectationManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

public class RuleGEN_04500 extends RuleManager {

	//Reset Registers

	RuleE rule = RuleE.GEN_04500;
	
	private Integer cmptViolation;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {

		String fileName = "";
		
		Map<String, HdlFile> hdlFiles;
		
		try {
			hdlFiles = RegisterAffectationManager.getRegisterAffectation();
		} catch (EntityException e) {
			logger.error("some exception message RuleGEN_04500", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		Element racine = initReportFile(ruleId, rule.getType(), rule.getRuleName());

		cmptViolation = 0;
		listViolation = new ArrayList<Violation>();
		
		for(Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() == null) { continue;}
			for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
				if (hdlEntityItem.getListHdlArchitecture() == null) { continue;}
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListProcess() == null) { continue;}
					for (Process processItem : hdlArchitectureItem.getListProcess()) {
						if (processItem.getListClockSignal() == null) { continue;}
						for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
							if (clockSignalItem.hasSynchronousReset()) {
								checkInitialization(clockSignalItem, hdlFile, hdlEntityItem,
										hdlArchitectureItem, processItem);
							}
						}
					}
				}
			}
		
		}
		
		List<Process> listProcess = new ArrayList<Process>();
		
		for (Violation violation : listViolation) {
			if (violation.getClockSignalItem().registerNotInitialized()) {continue;}
			
			if (listProcess.contains(violation.getProcessItem())) { continue;}
			
			listProcess.add(violation.getProcessItem());
			
			cmptViolation++;
			addViolation(racine, "resetRegisters", violation.getName(), violation.getfLine(), 
					violation.getLocalPath(), violation.getEntity(), violation.getArchitecture(),
					violation.getProcessItem());
		}
		if (cmptViolation != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType());
		}
		
		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (cmptViolation, fileName);


	}

	private void addViolation(Element racine, String error, String registerName, int registerLoc,
			String fileName, Entity entity, Architecture architecture, 
			Process process) {
		Element registerElement = document.createElement(NodeType.REGISTER.toString());
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


	}

}
