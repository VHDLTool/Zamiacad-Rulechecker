package org.zamia.plugin.tool.vhdl.tools.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.manager.HdlFileManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_FN_20 extends ToolSelectorManager {

	// Line Counter
	
	ToolE tool = ToolE.REQ_FEAT_FN20;

	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		String fileName = "";
		
		try {
			fileName = dumpXml(tool, parameterSource);
			
		} catch (Exception e) {
			logger.error("some exception message Tool_FN_20", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}

	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		Map<String, HdlFile> listHdlFile = HdlFileManager.getHdlFile();

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) 
		{
			HdlFile hdlFile = entry.getValue();

			Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());

			Element fileElement = createFileTypeElement(hdlFile);
			
			logEntry.appendChild(fileElement);
			
			racine.appendChild(logEntry);
		}
	}

	
}
