package org.zamia.plugin.tool.vhdl.tools.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.InputOutput;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.NumberReportE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.RegisterInputRead;
import org.zamia.plugin.tool.vhdl.RegisterInputSource;
import org.zamia.plugin.tool.vhdl.StopConditionE;
import org.zamia.plugin.tool.vhdl.manager.InputCombinationalProcessManager;
import org.zamia.plugin.tool.vhdl.manager.InputOutputManager;
import org.zamia.plugin.tool.vhdl.manager.RegisterAffectationManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.IntParam;
import org.zamia.plugin.tool.vhdl.rules.StringParam;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.AssociationElement;
import org.zamia.vhdl.ast.InterfaceDeclaration;

public class Tool_LOGICAL_CONE extends ToolSelectorManager {

	//Logical Cone

	ToolE tool = ToolE.REQ_FEAT_AR3;
	private RegisterInput register;
	private HdlEntity hdlEntity;
	private HdlArchitecture hdlArchitecture;
	private InputOutput io;
	private RegisterInput input;
	private int nbHierarchie;
	private String signalTag;
	
	private Element racineFirst;
	private Element racineSecond;
	
	private Map<String, HdlFile> listHdlFile = null;
	
	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		String fileName = "";

		racineFirst = null;
		racineSecond = null;
		
		List<IHandbookParam> paramList = getXmlParameterFileConfig(zPrj, ruleId, ParameterSource.RULE_CHECKER);
		if (paramList == null)
		{
			// wrong param
			logger.info("Rule Checker: wrong parameter for rules "+tool.getIdReq()+ ".");
			return new Pair<Integer, String> (WRONG_PARAM, "");
		}

		// Specific use of params for tools
		for (IHandbookParam param : paramList)
		{
			if (param instanceof IntParam)
			{
				Integer value = ((IntParam)param).getValue();
				nbHierarchie = value != null? value.intValue(): 0;
			}
			else if (param instanceof StringParam)
			{
				signalTag = ((StringParam) param).getValue();
			}
		}
		
		// Retrieve specific data for xml report generation
		try {
			listHdlFile = RegisterAffectationManager.getRegisterAffectation();
			listHdlFile = InputOutputManager.getInputOutputComponent();
			listHdlFile = InputCombinationalProcessManager.getInputCombinationalProcess();

			if (signalTag.startsWith(HdlEntity.IO)) 
			{
				logger.info("IO");
				try 
				{
					getIO(signalTag);
				} 
				catch (EntityException e) 
				{
					logger.error("some exception message Tool_LOGICAL_CONE logicalConeIO", e);
				}
			} 
			else if (signalTag.startsWith(RegisterAffectationManager.REG)) 
			{
				logger.info("REG");
				try 
				{
					getRegister(signalTag);
				} 
				catch (EntityException e) 
				{
					logger.error("some exception message Tool_LOGICAL_CONE logicalConeREG", e);
				}
			} 
			else if (signalTag.startsWith(InputCombinationalProcessManager.INPUT)) 
			{
				logger.info("INPUT");
				try 
				{
					getInputComb(signalTag);
				} 
				catch (EntityException e) 
				{
					logger.error("some exception message Tool_LOGICAL_CONE logicalConeINPUT", e);
				}
			}		
			
		} catch (EntityException e) {
			logger.error("some exception message Tool_LOGICAL_CONE logicalCone", e);
		}

		try {
			// dump File 1
			fileName = dumpXml(tool, parameterSource, NumberReportE.FIRST);
		} catch (Exception e) {
			logger.error("some exception message Tool_LOGICAL_CONE", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		try {
			// dump File 2
			fileName = dumpXml(tool, parameterSource, NumberReportE.SECOND);
		} catch (Exception e) {
			logger.error("some exception message Tool_LOGICAL_CONE", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		return new Pair<Integer, String> (0, fileName);
	}
	
	
	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		if (racineFirst == null)
		{
			racineFirst = racine;
		}
		else
		{
			racineSecond = racine;
		}

		if (signalTag.startsWith(HdlEntity.IO)) 
		{
			logger.info("IO");
			if (io != null) 
			{
				if (racineSecond == null)
				{
					// File 1
					logicalConeReadIO();
				}
				else
				{
					// File 2
					logicalConeSourceIO();
				}
			}

		} 
		else if (signalTag.startsWith(RegisterAffectationManager.REG)) 
		{
			logger.info("REG");
			if (register != null) 
			{
				if (racineSecond == null)
				{
					// File 1
					logicalConeReadREG();
				}
				else
				{
					// File 2
					logicalConeSourceREG();
				}
			}
		} 
		else if (signalTag.startsWith(InputCombinationalProcessManager.INPUT)) 
		{
			logger.info("INPUT");
			if (input != null) 
			{
				if (racineSecond == null)
				{
					// File 1
					logicalConeReadInputComb();
				}
				else
				{
					// File 2
					logicalConeSourceInputComb();
				}
			}
		}		
	}
	
	private String searchID(String fileName, String signalName) {
		String id = "";
		fileName = File.separator+fileName;
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (!hdlFile.getLocalPath().equalsIgnoreCase(fileName)){
				continue;
			}
			
			if (!hdlFile.getListHdlEntity().isEmpty()) {
				for (HdlEntity entity : hdlFile.getListHdlEntity()) {
					if (entity.getListIO() != null && !entity.getListIO().isEmpty()) {
						for (InputOutput inputOutput : entity.getListIO()) {
							if (inputOutput.getNameS().equalsIgnoreCase(signalName)) {
								id = inputOutput.getTag();
							}
						}
					}
					if (entity.getListHdlArchitecture() != null && !entity.getListHdlArchitecture().isEmpty()) {
						for (HdlArchitecture architecture : entity.getListHdlArchitecture()) {
							if (architecture.getListProcess() != null && !architecture.getListProcess().isEmpty()) {
								for (Process process : architecture.getListProcess()) {
									if (process.getListClockSignal() != null && !process.getListClockSignal().isEmpty()) {
										for (ClockSignal clockSignal : process.getListClockSignal()) {
											if (clockSignal.getListRegister() != null && !clockSignal.getListRegister().isEmpty()) {
												for (RegisterInput register : clockSignal.getListRegister()) {
													if (register.toString().equalsIgnoreCase(signalName)) {
														if (id.length() != 0) { id+=";";}
														id += register.getTag();
													}
												}
											}

										}
									} else if (process.getListInput() != null && !process.getListInput().isEmpty()) {
										for (RegisterInput input : process.getListInput()) {
											if (input.toString().equalsIgnoreCase(signalName)) {
												if (id.length() != 0) { id+=";";}
												id += input.getTag();
											}
										}
									}

								}
							}

						}
					}
				}
			}
		}
		
		return id;
	}

	private void getIO(String signalTag) throws EntityException {
		Map<String, HdlFile> listHdlFile = InputOutputManager.getInputOutputComponent();

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					if (hdlEntityItem.getListIO() != null) {
						for (InputOutput ioItem : hdlEntityItem.getListIO()) {
							if (ioItem.getTag().equalsIgnoreCase(signalTag)) {
								io = ioItem;
								hdlEntity = hdlEntityItem;
								if (hdlEntity.getListHdlArchitecture().size()>0) {
									hdlArchitecture = hdlEntity.getListHdlArchitecture().get(0);
								}
								return;
							}
						}
					}
				}
			}
		}

	}

	private void getRegister(String signalTag) throws EntityException {

		Map<String, HdlFile> listHdlFile = RegisterAffectationManager.getRegisterAffectation();

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					if (hdlEntityItem.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
							if (hdlArchitectureItem.getListProcess() != null) {
								for (Process processItem : hdlArchitectureItem.getListProcess()) {
									if (processItem.isSynchronous()) {
										for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
											if (clockSignalItem.getListRegister() != null) {
												for (RegisterInput registerItem : clockSignalItem.getListRegister()) {
													if (registerItem.getTag().equalsIgnoreCase(signalTag)) {
														register = registerItem;
														hdlEntity = hdlEntityItem;
														hdlArchitecture = hdlArchitectureItem;
														return;
													}
												}
											}

										}
									}
								}
							}

						}
					}
				}
			}

		}
	}

	private void getInputComb(String signalTag) throws EntityException {
		Map<String, HdlFile> listHdlFile = InputCombinationalProcessManager.getInputCombinationalProcess();

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					if (hdlEntityItem.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
							if (hdlArchitectureItem.getListProcess() != null) {
								for (Process processItem : hdlArchitectureItem.getListProcess()) {
									if (!processItem.isSynchronous()) {
										if (processItem.getListInput() != null) {
											for (RegisterInput inputItem : processItem.getListInput()) {
												if (inputItem.getTag().equalsIgnoreCase(signalTag)) {
													input = inputItem;
													hdlEntity = hdlEntityItem;
													hdlArchitecture = hdlArchitectureItem;
													return;
												}
											}
										}

									}
								}
							}

						}
					}
				}
			}

		}
	}


	private void logicalConeSourceREG() {
		hdlEntity.searchSourceSignal(register, hdlEntity, hdlArchitecture, 0, nbHierarchie);
		addElementSource(register, 0);
		for (RegisterInputSource registerInputSource : register.getListSourceRegisterInput()) {
			addElementSource(registerInputSource, register, 1);
			dumpSource( registerInputSource, 1);
		}
	}

	private void addElementSource(RegisterInputSource registerInputSource, RegisterInput register, int level) 
	{
		Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T2");
		
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, register.toString()));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(registerInputSource.getLocation().fSF.getLocalPath(), register.toString())));
		String sinkName = "" ;
		if (registerInputSource.getTarget() != null) 
		{
			sinkName = registerInputSource.getTarget().getName().toString();
		} 
		else if (registerInputSource.getRead() instanceof InterfaceDeclaration) 
		{
			InterfaceDeclaration interDec = (InterfaceDeclaration) registerInputSource.getRead();
			sinkName = interDec.getId();
		}
		else if (registerInputSource.getRead() instanceof AssociationElement) 
		{
			AssociationElement interDec = (AssociationElement) registerInputSource.getRead();
			sinkName = interDec.getFormalPart() != null ? interDec.getFormalPart().toString() : (interDec.getActualPart() != null ? interDec.getActualPart().toString() : "NULL");
		} 
		else
		{
			sinkName = registerInputSource.toString();
		}

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, sinkName));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, searchID(registerInputSource.getLocation().fSF.getLocalPath(), sinkName)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, registerInputSource.getLocation().fSF.getLocalPath()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(registerInputSource.getLocation().fLine)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.TYPE.toString()
				, registerInputSource.getType()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(-level)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
					, registerInputSource.getListSourceRegisterInput().isEmpty()? registerInputSource.getStopCondition().toString(): "None"));

		racineSecond.appendChild(logEntry);
	}

	private void logicalConeSourceInputComb() {
		hdlEntity.searchSourceSignal(input, hdlEntity, hdlArchitecture, 0, nbHierarchie);
		addElementSource(input, 0);
		for (RegisterInputSource registerInputSource : input.getListSourceRegisterInput()) {
			addElementSource(registerInputSource, input, 1);
			dumpSource( registerInputSource, 1);
		}
	}
	
	private void logicalConeReadREG() {
		hdlEntity.searchReadSignalSource(register, ToolManager.getVectorName(register.toString()), 1, nbHierarchie);
		addElement(register, 0);
		for (RegisterInputRead registerInputRead : register.getListReadRegisterInput()) {
			addElement(register, registerInputRead, 1);
			dump( registerInputRead, 1);
		}
	}

	private void addElement(RegisterInput register, int level) 
	{
		Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T1");
		
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, ""));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, ""/*searchID(fileName, signalName)*/));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, register.toString()));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(register.getLocation().fSF.getLocalPath(),  register.toString())));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, register.getLocation().fSF.getLocalPath()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(register.getLocation().fLine)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.TYPE.toString()
				, ""));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(level)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
					, register.getListReadRegisterInput().isEmpty()? register.getStopCondition().toString(): "None"));

		racineFirst.appendChild(logEntry);
	}

	private void addElementSource(RegisterInput register, int level) 
	{
		Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T2");
		
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, ""));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.ID.toString()
				, ""/*searchID(fileName, signalName)*/));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, register.toString()));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, signalTag));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, register.getLocation().fSF.getLocalPath()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(register.getLocation().fLine)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.TYPE.toString()
				, ""));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(-level)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
				, register.getListSourceRegisterInput().isEmpty()? register.getStopCondition().toString(): "None"));
		
		racineSecond.appendChild(logEntry);
	}

	private void addElement(RegisterInputRead registerInputRead, RegisterInputRead registerRead, int level) 
	{
		Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T1");
		
		String signalName = registerInputRead.getTarget() != null ? registerInputRead.getTarget().getName().toString() : "NULL";
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, signalName));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, searchID(registerRead.getLocation().fSF.getLocalPath(), signalName)));

		String sinkName = "" ;
		if (registerRead.getTarget() != null) 
		{
			sinkName = registerRead.getTarget().getName().toString();
		} 
		else if (registerRead.getRead() instanceof InterfaceDeclaration) 
		{
			InterfaceDeclaration interDec = (InterfaceDeclaration) registerRead.getRead();
			sinkName = interDec.getId();
		} 
		else 
		{
			sinkName = "NULL";
		}
		
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, sinkName));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(registerRead.getLocation().fSF.getLocalPath(), sinkName)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, registerRead.getLocation().fSF.getLocalPath()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(registerRead.getLocation().fLine)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.TYPE.toString()
				, registerRead.getType()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(level)));

		if (registerRead.getListReadRegisterInput().isEmpty()) 
		{
			if (registerRead.getStopCondition().toString().equalsIgnoreCase("") && registerRead.getType().equalsIgnoreCase("IO")) 
			{
				logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
						, StopConditionE.IO_PAD.toString()));
			} 
			else if (registerRead.getStopCondition().toString().equalsIgnoreCase("") && registerRead.getType().equalsIgnoreCase("process")) 
			{
				logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
						, StopConditionE.CONSTANT_ASSIGNMENT.toString()));
			} 
			else 
			{
				logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
						, registerRead.getStopCondition().toString()));
			}
		}
		else
		{
			logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
					, "None"));
		}

		racineFirst.appendChild(logEntry);
	}
	
	private void addElement(RegisterInput registerInput, RegisterInputRead registerRead, int level) 
	{
		Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T1");
		
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, registerInput.toString() ));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, searchID(registerRead.getLocation().fSF.getLocalPath(), registerInput.toString()) ));

		String sinkName = "" ;
		if (registerRead.getTarget() != null) 
		{
			sinkName = registerRead.getTarget().getName().toString();
		} 
		else if (registerRead.getRead() instanceof InterfaceDeclaration) 
		{
			InterfaceDeclaration interDec = (InterfaceDeclaration) registerRead.getRead();
			sinkName = interDec.getId();
		} 
		else 
		{
			sinkName = "NULL";
		}
		
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, sinkName));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(registerRead.getLocation().fSF.getLocalPath(), sinkName)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, registerRead.getLocation().fSF.getLocalPath()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(registerRead.getLocation().fLine)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.TYPE.toString()
				, registerRead.getType()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(level)));

		if (registerRead.getListReadRegisterInput().isEmpty()) 
		{
			if (registerRead.getStopCondition().toString().equalsIgnoreCase("") && registerRead.getType().equalsIgnoreCase("IO")) {
				logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
						, StopConditionE.IO_PAD.toString()));
			} 
			else 
			{
				logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
						, registerRead.getStopCondition().toString()));
			}
		}
		else
		{
			logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
					, "None"));
		}

		racineFirst.appendChild(logEntry);
	}
	
	private void addElementSource(RegisterInputSource registerInputSource,
			RegisterInputSource registerSource, int level) 
	{
		
		Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T2");
		
		String signalName = registerInputSource.getTarget() != null ? registerInputSource.getTarget().getName().toString() :registerInputSource.toString();
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, signalName));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(registerSource.getLocation().fSF.getLocalPath(), signalName)));

		String sinkName = "" ;
		if (registerSource.getTarget() != null) 
		{
			sinkName = registerSource.getTarget().getName().toString();
		} 
		else if (registerSource.getRead() instanceof InterfaceDeclaration) 
		{
			InterfaceDeclaration interDec = (InterfaceDeclaration) registerSource.getRead();
			sinkName = interDec.getId();
		} 
		else if (registerSource.getRead() instanceof AssociationElement) 
		{
			AssociationElement assoElem = (AssociationElement) registerSource.getRead();
			sinkName = assoElem.getFormalPart().toString();
		} 
		else 
		{
			sinkName = registerSource.toString();
		}
		
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, sinkName));
		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, searchID(registerSource.getLocation().fSF.getLocalPath(), sinkName)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, registerSource.getLocation().fSF.getLocalPath()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(registerSource.getLocation().fLine)));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.SOURCE.toString()+NodeInfo.TYPE.toString()
				, registerSource.getType()));

		logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(-level)));

		if (registerSource.getListSourceRegisterInput().isEmpty()) 
		{
			logEntry.appendChild(NewElement(document, NAMESPACE_PREFIX + NodeType.STOP_CONDITION.toString()
					, registerSource.getListSourceRegisterInput().isEmpty()? registerSource.getStopCondition().toString(): "None"));
		}

		racineSecond.appendChild(logEntry);
	}

	private void logicalConeReadInputComb() {
		hdlEntity.searchReadSignalSource(input, ToolManager.getVectorName(input.toString()), 0, nbHierarchie);
		addElement(input, 0);
		for (RegisterInputRead registerInputRead : input.getListReadRegisterInput()) {
			dump( registerInputRead, 0);
		}
	}
	
	private void logicalConeSourceIO() {
		hdlEntity.searchSourceSignal(io, hdlEntity, hdlArchitecture, 0, nbHierarchie);
		addElementSource(io, 0);
		for (RegisterInputSource registerInputSource : io.getListSourceRegisterInput()) {
			addElementSource(registerInputSource, io, 1);
			dumpSource( registerInputSource, 1);
		}
	}

	private void logicalConeReadIO() 
	{
		hdlEntity.searchReadSignalSource(io, ToolManager.getVectorName(io.toString()), 0, nbHierarchie);
		addElement(io, 0);
		for (RegisterInputRead registerInputRead : io.getListReadRegisterInput()) {
			dump(registerInputRead,  0);
		}
	}


	private void dump(RegisterInputRead registerInputRead, int incr) {
		incr++;
		for (RegisterInputRead registerRead : registerInputRead.getListReadRegisterInput()) {
			addElement(registerInputRead, registerRead, incr);
			dump(registerRead, incr);
		}
		
	}

	private void dumpSource(RegisterInputSource registerInputSource, int incr) {
		incr++;
		for (RegisterInputSource registerSource : registerInputSource.getListSourceRegisterInput()) {
			addElementSource(registerInputSource, registerSource, incr);
			dumpSource(registerSource, incr);
		}
	}

}
