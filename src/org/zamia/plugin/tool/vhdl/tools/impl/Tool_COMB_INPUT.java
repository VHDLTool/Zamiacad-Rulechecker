package org.zamia.plugin.tool.vhdl.tools.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.manager.InputCombinationalProcessManager;
import org.zamia.plugin.tool.vhdl.manager.ProcessManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_COMB_INPUT extends ToolSelectorManager {

	
	ToolE tool = ToolE.REQ_FEAT_COMB_INPUT;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		String fileName = "";
		
		try {
			fileName = dumpXml(tool, parameterSource);
		} catch (Exception e) {
			logger.error("some exception message Tool_COMB_INPUT", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}

	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		Map<String, HdlFile> listHdlFile = InputCombinationalProcessManager.getInputCombinationalProcess();

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) 
		{
			HdlFile hdlFile = entry.getValue();

			for (HdlEntity entity : hdlFile.getListHdlEntity()) 
			{
				for (HdlArchitecture architecture : entity.getListHdlArchitecture())
				{
					for (Process process : architecture.getListProcess()) 
					{
						for (RegisterInput input : process.getListInput()) 
						{
							Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());
							
							Element fileElement = createFileTypeElement(hdlFile);
							Element entityElement = createEntityTypeElement(entity);
							Element architectureElement = createArchitectureTypeElement(architecture);
							Element processElement = createProcessTypeElement(process);
							Element inputElement = createInputTypeElement(input);
							
							logEntry.appendChild(fileElement);
							logEntry.appendChild(entityElement);
							logEntry.appendChild(architectureElement);
							logEntry.appendChild(processElement);
							logEntry.appendChild(inputElement);
							
							racine.appendChild(logEntry);
							
						}
					}					
				}
			}
		}
	}

}
