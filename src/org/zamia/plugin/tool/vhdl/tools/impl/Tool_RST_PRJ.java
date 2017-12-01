package org.zamia.plugin.tool.vhdl.tools.impl;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalSourceManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_RST_PRJ  extends ToolSelectorManager {

	// Reset Per Project
	
	ToolE tool = ToolE.REQ_FEAT_RST_PRJ;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String toolId, ParameterSource parameterSource) {
		String fileName = "";
		
		try {
			fileName = dumpXml(tool, parameterSource);
		} catch (Exception e) {
			logger.error("some exception message Tool_RST_PRJ", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		return new Pair<Integer, String> (0, fileName);
	}
	
	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		ListResetSource listResetSource;
		
		listResetSource = ResetSignalSourceManager.getResetSourceSignal();

		for (ResetSource resetSource : listResetSource.getListResetSource()) 
		{
			Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());

			Element clockSourceTagElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SOURCE.toString()+NodeInfo.TAG.toString());
			clockSourceTagElement.setTextContent(resetSource.getTag());
			logEntry.appendChild(clockSourceTagElement);
			
			Element entityElement = createEntityArchitectureTypeElement(resetSource.getSignalDeclaration().getLocation().fSF.getLocalPathWithPointSlash(), resetSource.getEntity(), resetSource.getArchitecture());
			logEntry.appendChild(entityElement);

			Element resetSourceNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SOURCE.toString()+NodeInfo.NAME.toString());
			resetSourceNameElement.setTextContent(resetSource.toString());
			logEntry.appendChild(resetSourceNameElement);

			Element resetSourceTypeElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SOURCE.toString()+NodeInfo.TYPE.toString());
			resetSourceTypeElement.setTextContent(resetSource.getType());
			logEntry.appendChild(resetSourceTypeElement);

			Element resetSourceLocElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SOURCE.toString()+NodeInfo.LOCATION.toString());
			resetSourceLocElement.setTextContent(String.valueOf(resetSource.getSignalDeclaration().getLocation().fLine));
			logEntry.appendChild(resetSourceLocElement);
			
			Element resetSourceSigDecElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SOURCE.toString()+"Declaration");
			resetSourceSigDecElement.setTextContent(String.valueOf(resetSource.getSignalDeclaration().toString()));
			logEntry.appendChild(resetSourceSigDecElement);
			
			racine.appendChild(logEntry);
		}
		
	}

}
