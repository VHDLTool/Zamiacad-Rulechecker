package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.ClockSourceRead;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalReadManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class RuleGEN_04900 extends RuleManager {

	//Use of clock signal

	RuleE rule = RuleE.GEN_04900;
	private Element racine;
	private Integer cmptViolation;

	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		String fileName = "";
		ListClockSource listClockRead;
		try {
			listClockRead = ClockSignalReadManager.getClockReadSignal();
		} catch (EntityException e) {
			logger.error("some exception message RuleGEN_04900", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		racine = initReportFile(ruleId, rule.getType(), rule.getRuleName());

//		try {
//			ToolManager.searchCompoment();
//		} catch (EntityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		cmptViolation = 0;

		for (ClockSource clockSource : listClockRead.getListClockSource()) {
			for (ClockSourceRead clockRead : clockSource.getListReadClockSource()) {
				if (clockRead.isWrongUsesClock()) {
					cmptViolation++;
					addViolation(racine, clockSource, "wrong uses clock", clockRead.getRead().getLocation().fSF.getLocalPath()
							, clockRead);
				}
			}
		}


		if (cmptViolation != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType());
		}
		
		if (cmptViolation == -1) {
			 ClockSignalReadManager.resetClockReadSignal();
		}
		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (cmptViolation, fileName);
	}




	private void addViolation(Element racine, ClockSource clockSource, String error,
			String localPath, ClockSourceRead clockRead) {

		Element clockSignalElement = document.createElement(NodeType.CLOCK_SOURCE.toString());
		racine.appendChild(clockSignalElement);

		clockSignalElement.appendChild(NewElement(document, "violationType"
				, error));

		clockSignalElement.appendChild(NewElement(document, NodeType.CLOCK_SOURCE.toString()+NodeInfo.NAME.toString()
				, clockSource.toString()));

		clockSignalElement.appendChild(NewElement(document, NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, localPath));

		clockSignalElement.appendChild(NewElement(document, NodeType.ENTITY.toString()+NodeInfo.NAME.toString()
				, clockRead.getEntityName()));

		clockSignalElement.appendChild(NewElement(document, NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString()
				, clockRead.getArchitectureName()));

		clockSignalElement.appendChild(NewElement(document, NodeType.CLOCK_SIGNAL.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(clockRead.getRead().getLocation().fLine)));
	}


}
