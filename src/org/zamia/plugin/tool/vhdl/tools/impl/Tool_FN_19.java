package org.zamia.plugin.tool.vhdl.tools.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.manager.EntityManager;
import org.zamia.plugin.tool.vhdl.manager.HdlFileManager;
import org.zamia.plugin.tool.vhdl.manager.LibraryManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Use;

public class Tool_FN_19 extends ToolSelectorManager {

	// Package/Library Identification
	
	ToolE tool = ToolE.REQ_FEAT_FN19;

	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		String fileName = "";
		
		try {
			fileName = dumpXml(tool, parameterSource);
		} catch (Exception e) {
			logger.error("some exception message Tool_FN_19", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}

	
	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		Map<String, HdlFile> listHdlFile = EntityManager.getEntity();
		listHdlFile = LibraryManager.getLibrary();
		
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) 
		{
			HdlFile hdlFile = entry.getValue();

			for (Use useItem : hdlFile.getLibraries()) 
			{
				Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());

				Element fileElement = createFileTypeElement(hdlFile);
				Element libraryElement = createLibTypeElement(useItem);

				logEntry.appendChild(fileElement);
				logEntry.appendChild(libraryElement);

				racine.appendChild(logEntry);
			}
		}
	}

}
