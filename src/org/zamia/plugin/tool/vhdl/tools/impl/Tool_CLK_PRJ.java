package org.zamia.plugin.tool.vhdl.tools.impl;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_CLK_PRJ  extends ToolSelectorManager {

	// Clock Per Project
	
	ToolE tool = ToolE.REQ_FEAT_CLK_PRJ;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String toolId) {
		String fileName = "";
		
		ListClockSource listClockSource;
		
		try {
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
			
		} catch (EntityException e) {
			logger.error("some exception message Tool_CLK_PRJ", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		Element racine = initReportFile(toolId, tool.getType(), tool.getRuleName());

		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			addReport(racine, clockSource, clockSource.getTag());
		}

		fileName = createReportFile(toolId, tool.getRuleName(), tool.getType(), "tool");
//		createreportClockSignalClockSource();
		return new Pair<Integer, String> (0, fileName);
	}

//	private void createreportClockSignalClockSource() {
//		ToolE tool = ToolE.REQ_FEAT_FN15b;
//		try {
//			Map<String, HdlFile> listHdlFile = ClockSignalManager.getClockSignal();
//			ClockSignalSourceManager.getClockSourceSignal();
//			ToolManager.dumpXml(listHdlFile, tool.getIdReq(), tool.getRuleName());
//		} catch (EntityException e) {
//			
//		}
//
//	}

	private void addReport(Element racine, ClockSource clockSource, String tag) {
		Element clockSourceElement = document.createElement(NodeType.CLOCK_SOURCE.toString());
		racine.appendChild(clockSourceElement);

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
