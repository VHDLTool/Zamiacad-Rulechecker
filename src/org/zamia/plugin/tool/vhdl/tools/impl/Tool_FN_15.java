package org.zamia.plugin.tool.vhdl.tools.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.manager.ProcessManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_FN_15 extends ToolSelectorManager {

	// Clock Identification
	
	ToolE tool = ToolE.REQ_FEAT_FN15;

	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		String fileName = "";
		
		try {
			fileName = dumpXml(tool, parameterSource);
		} catch (Exception e) {
			logger.error("some exception message Tool_FN_15", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}

	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		Map<String, HdlFile> listHdlFile = ClockSignalManager.getClockSignal();
		ClockSignalSourceManager.getClockSourceSignal();

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) 
		{
			HdlFile hdlFile = entry.getValue();

			for (HdlEntity entity : hdlFile.getListHdlEntity()) 
			{
				for (HdlArchitecture architecture : entity.getListHdlArchitecture())
				{
					for (Process process : architecture.getListProcess()) 
					{
						for (ClockSignal clockSignal : process.getListClockSignal()) 
						{
							Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());
							
							Element fileElement = createFileTypeElement(hdlFile);
							Element entityElement = createEntityTypeElement(entity);
							Element architectureElement = createArchitectureTypeElement(architecture);
							Element processElement = createProcessTypeElement(process);
							Element clockSignalElement = createClockSignalTypeElement(clockSignal);
							
							logEntry.appendChild(fileElement);
							logEntry.appendChild(entityElement);
							logEntry.appendChild(architectureElement);
							logEntry.appendChild(processElement);
							logEntry.appendChild(clockSignalElement);
							
							racine.appendChild(logEntry);
						}
					}					
				}
			}
		}
	}

}
