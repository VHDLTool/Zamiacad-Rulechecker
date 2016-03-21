package org.zamia.plugin.tool.vhdl.rules.impl.std;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class RuleSTD_03700 extends RuleManager {

	// Reset Assertion and Deassertion
	
	RuleE rule = RuleE.STD_03700;
	ZamiaProject zPrj;
	ResetSignal currentResetSignal;
	
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		this.zPrj = zPrj;
		String fileName = "";
		
		ListResetSource listResetSource;
		
		try {
			listResetSource = ResetSignalSourceManager.getResetSourceSignal();
		} catch (EntityException e) {
			logger.error("some exception message RuleSTD_03700", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		Element racine = initReportFile(ruleId, rule.getType(), rule.getRuleName());

		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			addReport(racine, resetSource);
		}
		
		
		fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType());
		
		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);

	}

	private void addReport(Element racine, ResetSource resetSource) {
		Element resetSourceElement = document.createElement(NodeType.RESET_SOURCE.toString());
		racine.appendChild(resetSourceElement);

		resetSourceElement.appendChild(NewElement(document, NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, resetSource.getSignalDeclaration().getLocation().fSF.getLocalPath()));

		resetSourceElement.appendChild(NewElement(document, NodeType.ENTITY.toString()+NodeInfo.NAME.toString()
				, resetSource.getEntity()));

		resetSourceElement.appendChild(NewElement(document, NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString()
				, resetSource.getArchitecture()));

		resetSourceElement.appendChild(NewElement(document, NodeType.RESET_SOURCE.toString()+NodeInfo.NAME.toString()
				, resetSource.toString()));

		resetSourceElement.appendChild(NewElement(document, NodeType.RESET_SOURCE.toString()+NodeInfo.TYPE.toString()
				, resetSource.getType()));

		resetSourceElement.appendChild(NewElement(document, NodeType.RESET_SOURCE.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(resetSource.getSignalDeclaration().getLocation().fLine)));

	}

}
