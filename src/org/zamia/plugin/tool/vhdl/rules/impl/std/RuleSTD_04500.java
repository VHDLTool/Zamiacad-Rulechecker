package org.zamia.plugin.tool.vhdl.rules.impl.std;

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

public class RuleSTD_04500 extends RuleManager {

	// Clock Reassignment
	RuleE rule = RuleE.STD_04500;
	
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		String fileName = "";
		
		ListClockSource listClockSource;
		try {
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
		} catch (EntityException e) {
			logger.error("some exception message RuleSTD_04500", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		Element racine = initReportFile(ruleId, rule.getType(), rule.getRuleName());

		Integer cmptViolation = 0;

		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			System.out.println("clockSource "+clockSource.toString());
			System.out.println("SignalDeclaration "+clockSource.getSignalDeclaration().getClass().getSimpleName());
			if (clockSource.checkAffectation()) {
				cmptViolation++;
				addViolation(racine, clockSource);
				System.out.println("addViolation "+cmptViolation);
			}
		}
		
		if (cmptViolation != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType());
		}
		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		System.out.println("cmptViolation "+cmptViolation+"  fileName  "+fileName);
		return new Pair<Integer, String> (cmptViolation, fileName);

	}


	private void addViolation(Element racine, ClockSource clockSource) {
		Element clockSourceElement = document.createElement(NodeType.CLOCK_SOURCE.toString());
		racine.appendChild(clockSourceElement);
		
		clockSourceElement.appendChild(NewElement(document, "violationType"
				, "ClockReassignment"));
		
		clockSourceElement.appendChild(NewElement(document, NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, clockSource.getSignalDeclaration().getLocation().fSF.getLocalPath()));

		clockSourceElement.appendChild(NewElement(document, NodeType.ENTITY.toString()+NodeInfo.NAME.toString()
				, clockSource.getEntity()));

		clockSourceElement.appendChild(NewElement(document, NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString()
				, clockSource.getArchitecture()));

		clockSourceElement.appendChild(NewElement(document, NodeType.CLOCK_SOURCE.toString()+NodeInfo.NAME.toString()
				, clockSource.toString()));

		clockSourceElement.appendChild(NewElement(document, NodeType.CLOCK_SOURCE.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(clockSource.getSignalDeclaration().getLocation().fLine)));

	}


}
