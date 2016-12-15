package org.zamia.plugin.tool.vhdl.tools.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.InputOutput;
import org.zamia.plugin.tool.vhdl.manager.HdlFileManager;
import org.zamia.plugin.tool.vhdl.manager.InputOutputManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_IO_PRJ extends ToolSelectorManager {

	// Input Output Identification
	
	ToolE tool = ToolE.REQ_FEAT_IO_PRJ;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		String fileName = "";
		
		try {
			fileName = dumpXml(tool, parameterSource);
		} catch (Exception e) {
			logger.error("some exception message Tool_IO_PRJ", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}

	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		Map<String, HdlFile> listHdlFile = InputOutputManager.getInputOutputComponent();

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) 
		{
			HdlFile hdlFile = entry.getValue();

			for (HdlEntity entity : hdlFile.getListHdlEntity()) 
			{
				for (InputOutput inputOutput : entity.getListIO()) 
				{
					Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());
					
					Element fileElement = createFileTypeElement(hdlFile);
					Element entityElement = createEntityTypeElement(entity);
					Element ioElement = createIOTypeElement(inputOutput);
					
					logEntry.appendChild(fileElement);
					logEntry.appendChild(entityElement);
					logEntry.appendChild(ioElement);
					
					racine.appendChild(logEntry);
				}				
			}
		}
	}
}
