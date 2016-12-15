package org.zamia.plugin.tool.vhdl.tools.impl;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalSourceManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_CLK_PRJ  extends ToolSelectorManager {

	// Clock Per Project
	
	ToolE tool = ToolE.REQ_FEAT_CLK_PRJ;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String toolId, ParameterSource parameterSource) {
		String fileName = "";
		
		try {
			fileName = dumpXml(tool, parameterSource);
		} catch (Exception e) {
			logger.error("some exception message Tool_CLK_PRJ", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}

	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		ListClockSource listClockSource;
		
		listClockSource = ClockSignalSourceManager.getClockSourceSignal();

		for (ClockSource clockSource : listClockSource.getListClockSource()) 
		{
			Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());

			Element clockSourceTagElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SOURCE.toString()+NodeInfo.TAG.toString());
			clockSourceTagElement.setTextContent(clockSource.getTag());
			logEntry.appendChild(clockSourceTagElement);
			
			Element entityElement = createEntityArchitectureTypeElement(clockSource.getSignalDeclaration().getLocation().fSF.getLocalPath(), clockSource.getEntity(), clockSource.getArchitecture());
			logEntry.appendChild(entityElement);

			Element clockSourceNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SOURCE.toString()+NodeInfo.NAME.toString());
			clockSourceNameElement.setTextContent(clockSource.toString());
			logEntry.appendChild(clockSourceNameElement);

			Element clockSourceTypeElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SOURCE.toString()+NodeInfo.TYPE.toString());
			clockSourceTypeElement.setTextContent(clockSource.getType());
			logEntry.appendChild(clockSourceTypeElement);

			Element clockSourceLocElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SOURCE.toString()+NodeInfo.LOCATION.toString());
			clockSourceLocElement.setTextContent(String.valueOf(clockSource.getSignalDeclaration().getLocation().fLine));
			logEntry.appendChild(clockSourceLocElement);
			
			racine.appendChild(logEntry);
		}
		
	}

}
