package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.ViolationPreservationName;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalReadManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class RuleGEN_02400 extends RuleManager {

	//Preservation of Reset Name
	
	RuleE rule = RuleE.GEN_02400;

	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		String fileName = "";
		
		ListResetSource listResetSource;
		try {
			listResetSource = ResetSignalReadManager.getResetReadSignal();
		} catch (EntityException e) {
			logger.error("some exception message RuleGEN_02400", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		Element racine = initReportFile(ruleId, rule.getType(), rule.getRuleName());

		Integer cmptViolation = 0;
		
		for (ResetSource clockSource : listResetSource.getListResetSource()) {
			for (ViolationPreservationName violation : clockSource.getViolationPreservationName()) {
				cmptViolation++;
				addViolation(racine, violation);
			}
		}
		
		if (cmptViolation != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType());
		}
		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (cmptViolation, fileName);
	}

	private void addViolation(Element racine, ViolationPreservationName violation) {
		Element clockSignalElement = document.createElement(NodeType.RESET_SIGNAL.toString()+NodeInfo.NAME.toString());
		racine.appendChild(clockSignalElement);
		
		clockSignalElement.appendChild(NewElement(document, "violationType", "noPreservationName"));

		clockSignalElement.appendChild(NewElement(document, NodeType.ENTITY.toString()+NodeInfo.NAME.toString()
				, violation.getEntityName()));

		clockSignalElement.appendChild(NewElement(document, NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString()
				, violation.getArchiName()));

		clockSignalElement.appendChild(NewElement(document, NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, violation.getFileName()));

		clockSignalElement.appendChild(NewElement(document, NodeType.RESET_SIGNAL.toString()+NodeInfo.NAME.toString()+NodeInfo.BEFORE.toString()
				, violation.getSignalNameBefore()));

		clockSignalElement.appendChild(NewElement(document, NodeType.RESET_SIGNAL.toString()+NodeInfo.NAME.toString()+NodeInfo.AFTER.toString()
				, violation.getSignalNameAfter()));

		clockSignalElement.appendChild(NewElement(document, NodeType.INSTANCE.toString()+NodeInfo.NAME.toString()
				, violation.getComposantName()));

		clockSignalElement.appendChild(NewElement(document, NodeType.MAP.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(violation.getLocation().fLine)));

	}


}
