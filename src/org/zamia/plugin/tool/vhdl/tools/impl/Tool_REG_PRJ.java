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
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.SignalSource;
import org.zamia.plugin.tool.vhdl.manager.RegisterAffectationManager;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalSourceManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_REG_PRJ extends ToolSelectorManager {
	
	// register Identification
	
	ToolE tool = ToolE.REQ_FEAT_REG_PRJ;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		String fileName = "";
		
		try {
			fileName = dumpXml(tool, parameterSource);
		} catch (Exception e) {
			logger.error("some exception message Tool_REG_PRJ", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}

	
	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		Map<String, HdlFile> listHdlFile = RegisterAffectationManager.getRegisterAffectation();

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
							for (RegisterInput register : clockSignal.getListRegister()) 
							{
								logRegisterInput(racine, hdlFile, entity, architecture, process, clockSignal, register);
							}
						}
					}					
				}
			}
		}
	}
	
	private void logRegisterInput(Element racine, HdlFile hdlFile, HdlEntity entity, HdlArchitecture architecture, Process process, ClockSignal clockSignal, RegisterInput register)
	{
		if (register.getListRegisterSource().isEmpty())
		{
			logEntryRegisterInput(racine, hdlFile, entity, architecture, process, clockSignal, register, null);
		}
		else
		{
			for (SignalSource registerSource : register.getListRegisterSource()) 
			{
				logEntryRegisterInput(racine, hdlFile, entity, architecture, process, clockSignal, register, registerSource);
			}
		}
	}

	private void logEntryRegisterInput(Element racine, HdlFile hdlFile, HdlEntity entity, HdlArchitecture architecture, Process process, ClockSignal clockSignal, RegisterInput register, SignalSource registerSource)
	{
		Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());
		
		Element fileElement = createFileTypeElement(hdlFile);
		Element entityElement = createEntityTypeElement(entity);
		Element architectureElement = createArchitectureTypeElement(architecture);
		Element processElement = createProcessTypeElement(process);
		Element clockSignalElement = createClockSignalTypeElement(clockSignal);
		Element registerIdElement = createRegisterIdTypeElement(register, registerSource);
		
		logEntry.appendChild(fileElement);
		logEntry.appendChild(entityElement);
		logEntry.appendChild(architectureElement);
		logEntry.appendChild(processElement);
		logEntry.appendChild(clockSignalElement);
		logEntry.appendChild(registerIdElement);
		
		racine.appendChild(logEntry);
	}


}
