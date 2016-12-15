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
import org.zamia.plugin.tool.vhdl.InputOutput;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.manager.InputCombinationalProcessManager;
import org.zamia.plugin.tool.vhdl.manager.InputOutputManager;
import org.zamia.plugin.tool.vhdl.manager.RegisterAffectationManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_OBJ_ID extends ToolSelectorManager {
	
	// Object Identification
	
	ToolE tool = ToolE.REQ_FEAT_OBJ_ID;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		String fileName = "";
		
		try {

			fileName = dumpXml(tool, parameterSource);
		} catch (Exception e) {
			logger.error("some exception message Tool_OBJ_ID", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}

	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		Map<String, HdlFile> listHdlFile = RegisterAffectationManager.getRegisterAffectation();
		listHdlFile = InputOutputManager.getInputOutputComponent();
		listHdlFile = InputCombinationalProcessManager.getInputCombinationalProcess();

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
					Element signalElement = createSignalTypeElement(inputOutput);
					
					logEntry.appendChild(fileElement);
					logEntry.appendChild(entityElement);
					logEntry.appendChild(signalElement);
					
					racine.appendChild(logEntry);
				}
				
				for (HdlArchitecture architecture : entity.getListHdlArchitecture()) {
					if (architecture.getListProcess() != null && !architecture.getListProcess().isEmpty()) {
						for (Process process : architecture.getListProcess()) {
							if (process.getListClockSignal() != null && !process.getListClockSignal().isEmpty()) {
								for (ClockSignal clockSignal : process.getListClockSignal()) {
									if (clockSignal.getListRegister() != null && !clockSignal.getListRegister().isEmpty()) {
										for (RegisterInput register : clockSignal.getListRegister()) 
										{
											Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());
											
											Element fileElement = createFileTypeElement(hdlFile);
											Element entityElement = createEntityTypeElement(entity);
											Element signalElement = createSignalTypeElement(register);
											
											logEntry.appendChild(fileElement);
											logEntry.appendChild(entityElement);
											logEntry.appendChild(signalElement);
											
											racine.appendChild(logEntry);
										}
									}

								}
							} 
							else
							{
								for (RegisterInput register : process.getListInput()) 
								{
									Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq());
									
									Element fileElement = createFileTypeElement(hdlFile);
									Element entityElement = createEntityTypeElement(entity);
									Element signalElement = createSignalTypeElement(register);
									
									logEntry.appendChild(fileElement);
									logEntry.appendChild(entityElement);
									logEntry.appendChild(signalElement);
									
									racine.appendChild(logEntry);
								}
							}
						}
					}

				}

			}
		}
	}
	
}
