package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class RuleSTD_04700 extends RuleManager {

	// Number of clock domains per modules
	RuleE rule = RuleE.STD_04700;
	private int cmptViolation;
	
	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		String fileName = "";
		
		List<List<Object>> listParam = new ArrayList<List<Object>>();
		List<Object> param = new ArrayList<Object>(); 
		param.add("nbClockDomain");
		param.add(Integer.class);
		listParam.add(param);

		List<List<Object>> xmlParameterFileConfig = getXmlParameterFileConfig(zPrj, ruleId, listParam);
		if (xmlParameterFileConfig == null) {
			// wrong param
			logger.info("Rule Checker: wrong parameter for rules "+rule.getIdReq()+ ".");
			return new Pair<Integer, String> (WRONG_PARAM, "");
		}

		// get param
		List<Object> listParam1 = xmlParameterFileConfig.get(0);

		Integer nbMaxClockDomaine = 0;
		try {
			nbMaxClockDomaine = Integer.valueOf((String)listParam1.get(2));
		} catch (Exception e) {
			logger.error("some exception message RuleSTD_04700", e);
			logger.info("Rule Checker: wrong parameter for rules "+rule.getIdReq()+ ".");
			return new Pair<Integer, String> (WRONG_PARAM, "");
		}

		ListClockSource listClockSource;
		Map<String, HdlFile> listHdlFile;
		try {
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
			listHdlFile = ClockSignalManager.getClockSignal();
		} catch (EntityException e) {
			logger.error("some exception message RuleSTD_04700 ClockSignalSourceManager.getClockSourceSignal", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		Element racine = initReportFile(ruleId, rule.getType(), rule.getRuleName());

		cmptViolation = 0;
		
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					if (hdlFile.getListHdlEntity() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
							ListClockSource listClockSourceEntity = createListClockSource(listClockSource, hdlEntityItem, hdlArchitectureItem);
							if (listClockSourceEntity.getListClockSource().size() > nbMaxClockDomaine) {
								addViolation(racine, hdlFile, hdlEntityItem, hdlArchitectureItem, listClockSourceEntity);
							}
						}
					}
				}
			}
		}

		if (cmptViolation != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType());
		}
		return new Pair<Integer, String> (cmptViolation, fileName);

	}
	
	
	private ListClockSource createListClockSource(
			ListClockSource listClockSource, HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem) {
		ListClockSource listClockSourceEntity = new ListClockSource();
		
		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			if (hdlArchitectureItem.getListProcess() != null) {
				for (Process processItem : hdlArchitectureItem.getListProcess()) {
					if (processItem.isSynchronous()) {
						for (ClockSignal clockSignal : processItem.getListClockSignal()) {
							if (clockSource.equals(clockSignal.getClockSource())) {
								listClockSourceEntity.add(clockSource, clockSignal);
							}
						}
					}
				}
			}
		}
		return listClockSourceEntity;
	}


	private void addViolation(Element racine, HdlFile hdlFile, HdlEntity hdlEntityItem,
			HdlArchitecture hdlArchitectureItem, ListClockSource listClockSourceEntity) {

		for (ClockSource clockSource : listClockSourceEntity.getListClockSource()) {
			cmptViolation++;
			
			Element entityElement = document.createElement(NodeType.ENTITY.toString());
			racine.appendChild(entityElement);
		
			entityElement.appendChild(NewElement(document, "violationType"
					, "numberClockDomaineViolation"));
	
			Element fileNameElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
			fileNameElement.setTextContent(hdlFile.getLocalPath());
			entityElement.appendChild(fileNameElement);
	
			Element entityNameElement = document.createElement(NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
			entityNameElement.setTextContent(hdlEntityItem.getEntity().getId());
			entityElement.appendChild(entityNameElement);
	
			Element archiNameElement = document.createElement(NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString());
			archiNameElement.setTextContent(hdlArchitectureItem.getArchitecture().getId());
			entityElement.appendChild(archiNameElement);

			Element clockSourceTagElement = document.createElement(NodeType.CLOCK_SOURCE.toString()+NodeInfo.TAG.toString());
			clockSourceTagElement.setTextContent(clockSource.getTag());
			entityElement.appendChild(clockSourceTagElement);
			
			Element clockSourceNameElement = document.createElement(NodeType.CLOCK_SOURCE.toString()+NodeInfo.NAME.toString());
			clockSourceNameElement.setTextContent(clockSource.toString());
			entityElement.appendChild(clockSourceNameElement);
			
			Element clockSourceTypeElement = document.createElement(NodeType.CLOCK_SOURCE.toString()+NodeInfo.TYPE.toString());
			clockSourceTypeElement.setTextContent(clockSource.getType());
			entityElement.appendChild(clockSourceTypeElement);
			
			Element clockSourceLocElement = document.createElement(NodeType.CLOCK_SOURCE.toString()+NodeInfo.LOCATION.toString());
			clockSourceLocElement.setTextContent(String.valueOf(clockSource.getSignalDeclaration().getLocation().fLine));
			entityElement.appendChild(clockSourceLocElement);
		}
	
	}

}
