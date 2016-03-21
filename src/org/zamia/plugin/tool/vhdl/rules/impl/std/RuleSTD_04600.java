package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class RuleSTD_04600 extends RuleManager {

	// Clock domain number in the design
	RuleE rule = RuleE.STD_04600;
	

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
			logger.error("some exception message RuleSTD_04600 Integer.valueOf", e);
			logger.info("Rule Checker: wrong parameter for rules "+rule.getIdReq()+ ".");
			return new Pair<Integer, String> (WRONG_PARAM, "");
		}

		ListClockSource listClockSource;
		try {
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
		} catch (EntityException e) {
			logger.error("some exception message RuleSTD_04600 ClockSignalSourceManager.getClockSourceSignal()", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		Element racine = initReportFile(ruleId, rule.getType(), rule.getRuleName());

		Integer cmptViolation = 0;

		if (listClockSource.getListClockSource().size() <= nbMaxClockDomaine) {
			return new Pair<Integer, String> (cmptViolation, fileName);
		}
		
		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			cmptViolation++;
			addViolation(racine, "numberClockDomaineViolation", clockSource, clockSource.getTag());
		}
		
		if (cmptViolation != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType());
		}
		return new Pair<Integer, String> (cmptViolation, fileName);

	}


	private void addViolation(Element racine, String violationType, ClockSource clockSource, String tag) {

		Element clockSourceElement = document.createElement(NodeType.CLOCK_SOURCE.toString());
		racine.appendChild(clockSourceElement);
		
		clockSourceElement.appendChild(NewElement(document, "violationType"
				, violationType));

		Element clockSourceTagElement = document.createElement(NodeType.CLOCK_SOURCE.toString()+NodeInfo.TAG.toString());
		clockSourceTagElement.setTextContent(tag);
		clockSourceElement.appendChild(clockSourceTagElement);

		Element fileNameElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
		fileNameElement.setTextContent(clockSource.getSignalDeclaration().getLocation().fSF.getLocalPath());
		clockSourceElement.appendChild(fileNameElement);

		Element entityNameElement = document.createElement(NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
		entityNameElement.setTextContent(clockSource.getEntity());
		clockSourceElement.appendChild(entityNameElement);

		Element archiNameElement = document.createElement(NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString());
		archiNameElement.setTextContent(clockSource.getArchitecture());
		clockSourceElement.appendChild(archiNameElement);

		Element clockSourceNameElement = document.createElement(NodeType.CLOCK_SOURCE.toString()+NodeInfo.NAME.toString());
		clockSourceNameElement.setTextContent(clockSource.toString());
		clockSourceElement.appendChild(clockSourceNameElement);

		Element clockSourceTypeElement = document.createElement(NodeType.CLOCK_SOURCE.toString()+NodeInfo.TYPE.toString());
		clockSourceTypeElement.setTextContent(clockSource.getType());
		clockSourceElement.appendChild(clockSourceTypeElement);

		Element clockSourceLocElement = document.createElement(NodeType.CLOCK_SOURCE.toString()+NodeInfo.LOCATION.toString());
		clockSourceLocElement.setTextContent(String.valueOf(clockSource.getSignalDeclaration().getLocation().fLine));
		clockSourceElement.appendChild(clockSourceLocElement);
	
	}

}
