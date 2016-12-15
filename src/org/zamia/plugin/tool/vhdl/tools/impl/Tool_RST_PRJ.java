package org.zamia.plugin.tool.vhdl.tools.impl;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalSourceManager;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_RST_PRJ  extends ToolSelectorManager {

	// Reset Per Project
	
	ToolE tool = ToolE.REQ_FEAT_RST_PRJ;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String toolId) {
		String fileName = "";
		
		ListResetSource listResetSource;
		
		try {
			listResetSource = ResetSignalSourceManager.getResetSourceSignal();
		} catch (EntityException e) {
			logger.error("some exception message Tool_RST_PRJ", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		Element racine = initReportFile(toolId, tool.getType(), tool.getRuleName());

		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			addReport(racine, resetSource);
		}

		fileName = createReportFile(toolId, tool.getRuleName(), tool.getType(), "tool");
		
		return new Pair<Integer, String> (0, fileName);
	}
	
	private void addReport(Element racine, ResetSource resetSource) {
		Element resetSourceElement = document.createElement(NodeType.RESET_SOURCE.toString());
		racine.appendChild(resetSourceElement);

		Element clockSourceTagElement = document.createElement(NodeType.RESET_SOURCE.toString()+NodeInfo.TAG.toString());
		clockSourceTagElement.setTextContent(resetSource.getTag());
		resetSourceElement.appendChild(clockSourceTagElement);

		Element fileNameElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
		fileNameElement.setTextContent(resetSource.getSignalDeclaration().getLocation().fSF.getLocalPath());
		resetSourceElement.appendChild(fileNameElement);

		Element entityNameElement = document.createElement(NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
		entityNameElement.setTextContent(resetSource.getEntity());
		resetSourceElement.appendChild(entityNameElement);

		Element archiNameElement = document.createElement(NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString());
		archiNameElement.setTextContent(resetSource.getArchitecture());
		resetSourceElement.appendChild(archiNameElement);

		Element resetSourceNameElement = document.createElement(NodeType.RESET_SOURCE.toString()+NodeInfo.NAME.toString());
		resetSourceNameElement.setTextContent(resetSource.toString());
		resetSourceElement.appendChild(resetSourceNameElement);

		Element resetSourceTypeElement = document.createElement(NodeType.RESET_SOURCE.toString()+NodeInfo.TYPE.toString());
		resetSourceTypeElement.setTextContent(resetSource.getType());
		resetSourceElement.appendChild(resetSourceTypeElement);

		Element resetSourceLocElement = document.createElement(NodeType.RESET_SOURCE.toString()+NodeInfo.LOCATION.toString());
		resetSourceLocElement.setTextContent(String.valueOf(resetSource.getSignalDeclaration().getLocation().fLine));
		resetSourceElement.appendChild(resetSourceLocElement);
		
		Element resetSourceSigDecElement = document.createElement(NodeType.RESET_SOURCE.toString()+"declaration");
		resetSourceSigDecElement.setTextContent(String.valueOf(resetSource.getSignalDeclaration().toString()));
		resetSourceElement.appendChild(resetSourceSigDecElement);
	}


}
