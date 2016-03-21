package org.zamia.plugin.tool.vhdl.tools.impl;

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
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.AssociationElement;
import org.zamia.vhdl.ast.InterfaceDeclaration;

public class Tool_LOGICAL_CONE extends ToolSelectorManager {

	//Logical Cone

	ToolE tool = ToolE.REQ_FEAT_LOGICAL_CONE;
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
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		String fileName = "";

		try {
			listHdlFile = RegisterAffectationManager.getRegisterAffectation();
			listHdlFile = InputOutputManager.getInputOutputComponent();
			listHdlFile = InputCombinationalProcessManager.getInputCombinationalProcess();
			
		} catch (EntityException e) {
			logger.error("some exception message Tool_REG_INPUT_COMB_IO_PRJ", e);
		}

		List<List<Object>> listParam = new ArrayList<List<Object>>();
		List<Object> param = new ArrayList<Object>(); 
		param.add("nbStage");
		param.add(Integer.class);
		listParam.add(param);

		param = new ArrayList<Object>(); 
		param.add("signalTag");
		param.add(String.class);
		listParam.add(param);


		List<List<Object>> xmlParameterFileConfig = getXmlParameterFileConfig(zPrj, ruleId, listParam);
		if (xmlParameterFileConfig == null) {
			// wrong param
			logger.info("Rule Checker: wrong parameter for rules "+tool.getIdReq()+ ".");
			return new Pair<Integer, String> (WRONG_PARAM, "");
		}

		// get param
		List<Object> listParam1 = xmlParameterFileConfig.get(0);

		nbHierarchie = 0;
		try {
			nbHierarchie = Integer.valueOf((String)listParam1.get(2));
		} catch (Exception e) {
			logger.error("some exception message Tool_LOGICAL_CONE", e);
			logger.info("Rule Checker: wrong parameter for rules "+tool.getIdReq()+ ".");
			return new Pair<Integer, String> (WRONG_PARAM, "");
		}
		

		List<Object> listParam2 = xmlParameterFileConfig.get(1);
		signalTag = (String) listParam2.get(2);
		
		racineFirst = initReportFile(ruleId, tool.getType(), tool.getRuleName(), NumberReportE.FIRST);
		racineSecond = initReportFile(ruleId, tool.getType(), tool.getRuleName(), NumberReportE.SECOND);

		System.out.println("TAG "+signalTag + " nbHierarchie "+nbHierarchie);
		
		if (signalTag.startsWith(HdlEntity.IO)) {
			logger.info("IO");
			try {
				logicalConeIO(signalTag);
			} catch (EntityException e) {
				logger.error("some exception message Tool_LOGICAL_CONE logicalConeIO", e);
				return new Pair<Integer, String> (NO_BUILD, fileName);
			}
		} else if (signalTag.startsWith(RegisterAffectationManager.REG)) {
			logger.info("REG");
			try {
				logicalConeREG(signalTag);
			} catch (EntityException e) {
				logger.error("some exception message Tool_LOGICAL_CONE logicalConeREG", e);
				return new Pair<Integer, String> (NO_BUILD, fileName);
			}
		} else if (signalTag.startsWith(InputCombinationalProcessManager.INPUT)) {
			logger.info("INPUT");
			try {
				logicalConeINPUT(signalTag);
			} catch (EntityException e) {
				logger.error("some exception message Tool_LOGICAL_CONE logicalConeINPUT", e);
				return new Pair<Integer, String> (NO_BUILD, fileName);
			}
		}		
		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		
		
		fileName = createReportFile(ruleId, tool.getRuleName(), tool.getType(), "rule", NumberReportE.FIRST);

		fileName = createReportFile(ruleId, tool.getRuleName(), tool.getType(), "rule", NumberReportE.SECOND);

		return new Pair<Integer, String> (0, fileName);
	}

	private void logicalConeIO(String signalTag)  throws EntityException {
		
		getIO(signalTag);
		if (io != null) {
			
			logicalConeSourceIO();
			logicalConeReadIO();
		}
	}

	private String searchID(String fileName, String signalName) {
		String id = "";
		fileName = "\\"+fileName;
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

	private void logicalConeREG(String signalTag) throws EntityException {
		getRegister(signalTag);
		if (register != null) {
	
			logicalConeSourceREG();
			logicalConeReadREG();
		}
	}

	private void logicalConeINPUT(String signalTag)  throws EntityException {
		getInputComb(signalTag);
		if (input != null) {
			logicalConeSourceInputComb();
			logicalConeReadInputComb();
		}
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
System.out.println("getInputComb");
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
		addElementSource(register, racineSecond, documentSecond, 0);
		for (RegisterInputSource registerInputSource : register.getListSourceRegisterInput()) {
			addElementSource(registerInputSource, register, racineSecond, documentSecond, 1);
			dumpSource( registerInputSource, racineSecond, documentSecond, 1);
		}
		

	}


	private void addElement(RegisterInputSource registerInputSource,
			Element racine, Document document, int level) {

		Element element = document.createElement(NodeType.REGISTER.toString());
		racine.appendChild(element);
		
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, register.toString()));
		String sinkName = "" ;
		if (registerInputSource.getTarget() != null) {
			sinkName = registerInputSource.getTarget().getName().toString();
		} else if (registerInputSource.getRead() instanceof InterfaceDeclaration) {
			InterfaceDeclaration interDec = (InterfaceDeclaration) registerInputSource.getRead();
			sinkName = interDec.getId();
		} else if (registerInputSource.getRead() instanceof AssociationElement) {
			AssociationElement interDec = (AssociationElement) registerInputSource.getRead();
			sinkName = interDec.getFormalPart() != null ? interDec.getFormalPart().toString() : (interDec.getActualPart() != null ? interDec.getActualPart().toString() : "NULL");
		} else {
			sinkName = registerInputSource.toString();
		}

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, sinkName));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.LOCATION.toString()
				, registerInputSource.getLocation().fSF.getLocalPath()));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NB_LINE.toString()
				, String.valueOf(registerInputSource.getLocation().fLine)));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.TYPE.toString()
				, registerInputSource.getType()));

		element.appendChild(NewElement(document, NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(level)));

//		if (registerInputSource.getListSourceRegisterInput().isEmpty()) {
			element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
					, registerInputSource.getStopCondition().toString()));
//		}

		
	}

	private void addElementSource(RegisterInputSource registerInputSource, RegisterInput register,
			Element racine, Document document, int level) {

		Element element = document.createElement(NodeType.REGISTER.toString());
		racine.appendChild(element);
		
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, register.toString()));
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(registerInputSource.getLocation().fSF.getLocalPath(), register.toString())));
		String sinkName = "" ;
		if (registerInputSource.getTarget() != null) {
			sinkName = registerInputSource.getTarget().getName().toString();
		} else if (registerInputSource.getRead() instanceof InterfaceDeclaration) {
			InterfaceDeclaration interDec = (InterfaceDeclaration) registerInputSource.getRead();
			sinkName = interDec.getId();
		} else if (registerInputSource.getRead() instanceof AssociationElement) {
			AssociationElement interDec = (AssociationElement) registerInputSource.getRead();
			sinkName = interDec.getFormalPart() != null ? interDec.getFormalPart().toString() : (interDec.getActualPart() != null ? interDec.getActualPart().toString() : "NULL");
		} else {
			sinkName = registerInputSource.toString();
		}

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, sinkName));
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, searchID(registerInputSource.getLocation().fSF.getLocalPath(), sinkName)));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.LOCATION.toString()
				, registerInputSource.getLocation().fSF.getLocalPath()));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NB_LINE.toString()
				, String.valueOf(registerInputSource.getLocation().fLine)));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.TYPE.toString()
				, registerInputSource.getType()));

		element.appendChild(NewElement(document, NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(-level)));

//		if (registerInputSource.getListSourceRegisterInput().isEmpty()) {
			element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
					, registerInputSource.getStopCondition().toString()));
//		}

		
	}

	private void logicalConeSourceInputComb() {
		hdlEntity.searchSourceSignal(input, hdlEntity, hdlArchitecture, 0, nbHierarchie);
		addElementSource(input, racineSecond, documentSecond, 0);
		for (RegisterInputSource registerInputSource : input.getListSourceRegisterInput()) {
			addElementSource(registerInputSource, input,  racineSecond, documentSecond, 1);
			dumpSource( registerInputSource, racineSecond, documentSecond, 1);
		}

	}
	
	private void logicalConeReadREG() {
		hdlEntity.searchReadSignalSource(register, ToolManager.getVectorName(register.toString()), 1, nbHierarchie);
		addElement(register, racineFirst, document, 0);
		for (RegisterInputRead registerInputRead : register.getListReadRegisterInput()) {
			addElement(register, registerInputRead, racineFirst, document, 1);
			dump( registerInputRead, racineFirst, 1);
		}

	}


	private void addElement(RegisterInput register,
			Element racine, Document document, int level) {
		Element element = document.createElement(NodeType.REGISTER.toString());
		racine.appendChild(element);
		
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, ""));
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, ""/*searchID(fileName, signalName)*/));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, register.toString()));
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(register.getLocation().fSF.getLocalPath(),  register.toString())));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.LOCATION.toString()
				, register.getLocation().fSF.getLocalPath()));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NB_LINE.toString()
				, String.valueOf(register.getLocation().fLine)));

//		element.appendChild(NewElement(document, NodeType.ALL.toString()+NodeInfo.TAG.toString()
//				, ));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.TYPE.toString()
				, ""));

		element.appendChild(NewElement(document, NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(level)));

		if (register.getListReadRegisterInput().isEmpty()) {
			element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
					, register.getStopCondition().toString()));
		}

	}

	private void addElementSource(RegisterInput register,
			Element racine, Document document, int level) {
		Element element = document.createElement(NodeType.REGISTER.toString());
		racine.appendChild(element);
		
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, ""));
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.ID.toString()
				, ""/*searchID(fileName, signalName)*/));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, register.toString()));
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, signalTag));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.LOCATION.toString()
				, register.getLocation().fSF.getLocalPath()));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NB_LINE.toString()
				, String.valueOf(register.getLocation().fLine)));

//		element.appendChild(NewElement(document, NodeType.ALL.toString()+NodeInfo.TAG.toString()
//				, signalTag));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.TYPE.toString()
				, ""));

		element.appendChild(NewElement(document, NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(-level)));

		if (register.getListSourceRegisterInput().isEmpty()) {
			element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
					, register.getStopCondition().toString()));
		}
	}

	private void addElement(RegisterInputRead registerInputRead,
			RegisterInputRead registerRead, Element racineFirst, Document document, int level) {
		Element element = document.createElement(NodeType.REGISTER.toString());
		racineFirst.appendChild(element);
		
		String signalName = registerInputRead.getTarget() != null ? registerInputRead.getTarget().getName().toString() : "NULL";
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, signalName));
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, searchID(registerRead.getLocation().fSF.getLocalPath(), signalName)));

		String sinkName = "" ;
		if (registerRead.getTarget() != null) {
			sinkName = registerRead.getTarget().getName().toString();
		} else if (registerRead.getRead() instanceof InterfaceDeclaration) {
			InterfaceDeclaration interDec = (InterfaceDeclaration) registerRead.getRead();
			sinkName = interDec.getId();
		} else {
			sinkName = "NULL";
		}
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, sinkName));
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(registerRead.getLocation().fSF.getLocalPath(), sinkName)));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.LOCATION.toString()
				, registerRead.getLocation().fSF.getLocalPath()));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NB_LINE.toString()
				, String.valueOf(registerRead.getLocation().fLine)));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.TYPE.toString()
				, registerRead.getType()));

		element.appendChild(NewElement(document, NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(level)));

		if (registerRead.getListReadRegisterInput().isEmpty()) {
			if (registerRead.getStopCondition().toString().equalsIgnoreCase("") && registerRead.getType().equalsIgnoreCase("IO")) {
				element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
						, StopConditionE.IO_PAD.toString()));
			} else if (registerRead.getStopCondition().toString().equalsIgnoreCase("") && registerRead.getType().equalsIgnoreCase("process")) {
				element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
						, StopConditionE.CONSTANT_ASSIGNMENT.toString()));
			} else {
				element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
						, registerRead.getStopCondition().toString()));
			}
		}

	}
	private void addElement(RegisterInput registerInput,
			RegisterInputRead registerRead, Element racineFirst, Document document, int level) {
		Element element = document.createElement(NodeType.REGISTER.toString());
		racineFirst.appendChild(element);
		
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, registerInput.toString() ));
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, searchID(registerRead.getLocation().fSF.getLocalPath(), registerInput.toString()) ));

		String sinkName = "" ;
		if (registerRead.getTarget() != null) {
			sinkName = registerRead.getTarget().getName().toString();
		} else if (registerRead.getRead() instanceof InterfaceDeclaration) {
			InterfaceDeclaration interDec = (InterfaceDeclaration) registerRead.getRead();
			sinkName = interDec.getId();
		} else {
			sinkName = "NULL";
		}
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, sinkName));
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(registerRead.getLocation().fSF.getLocalPath(), sinkName)));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.LOCATION.toString()
				, registerRead.getLocation().fSF.getLocalPath()));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NB_LINE.toString()
				, String.valueOf(registerRead.getLocation().fLine)));

		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.TYPE.toString()
				, registerRead.getType()));

		element.appendChild(NewElement(document, NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(level)));

		if (registerRead.getListReadRegisterInput().isEmpty()) {
			if (registerRead.getStopCondition().toString().equalsIgnoreCase("") && registerRead.getType().equalsIgnoreCase("IO")) {
				element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
						, StopConditionE.IO_PAD.toString()));
			} else {
				element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
						, registerRead.getStopCondition().toString()));
			}
		}

	}
	
	private void addElementSource(RegisterInputSource registerInputSource,
			RegisterInputSource registerSource, Element racine, Document document, int level) {
		Element element = document.createElement(NodeType.REGISTER.toString());
		racine.appendChild(element);
		
		String signalName = registerInputSource.getTarget() != null ? registerInputSource.getTarget().getName().toString() :registerInputSource.toString();
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.NAME.toString()
				, signalName));
		element.appendChild(NewElement(document, NodeType.SINK.toString()+NodeInfo.ID.toString()
				, searchID(registerSource.getLocation().fSF.getLocalPath(), signalName)));

		String sinkName = "" ;
		if (registerSource.getTarget() != null) {
			sinkName = registerSource.getTarget().getName().toString();
		} else if (registerSource.getRead() instanceof InterfaceDeclaration) {
			InterfaceDeclaration interDec = (InterfaceDeclaration) registerSource.getRead();
			sinkName = interDec.getId();
		} else if (registerSource.getRead() instanceof AssociationElement) {
			AssociationElement assoElem = (AssociationElement) registerSource.getRead();
			sinkName = assoElem.getFormalPart().toString();
		} else {
			sinkName = registerSource.toString();
		}
		
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NAME.toString()
				, sinkName));
		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.ID.toString()
				, searchID(registerSource.getLocation().fSF.getLocalPath(), sinkName)));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.LOCATION.toString()
				, registerSource.getLocation().fSF.getLocalPath()));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.NB_LINE.toString()
				, String.valueOf(registerSource.getLocation().fLine)));

		element.appendChild(NewElement(document, NodeType.SOURCE.toString()+NodeInfo.TYPE.toString()
				, registerSource.getType()));

		element.appendChild(NewElement(document, NodeType.STAGE.toString()+NodeInfo.LEVEL.toString()
				, String.valueOf(-level)));

		if (registerSource.getListSourceRegisterInput().isEmpty()) {
			element.appendChild(NewElement(document, NodeType.STOP_CONDITION.toString()
					, registerSource.getStopCondition().toString()));
		}

	}

	private void logicalConeReadInputComb() {
		hdlEntity.searchReadSignalSource(input, ToolManager.getVectorName(input.toString()), 0, nbHierarchie);
		addElement(input, racineFirst, document, 0);
		for (RegisterInputRead registerInputRead : input.getListReadRegisterInput()) {
			dump( registerInputRead, racineFirst, 0);
		}
	}
	
	private void logicalConeSourceIO() {
		
		if (io.getDir() == OIDir.IN) {
			return;
		}
		System.out.println("logicalConeSourceIO");
		hdlEntity.searchSourceSignal(io, hdlEntity, hdlArchitecture, 0, nbHierarchie);
		addElementSource(io, racineSecond, documentSecond, 0);
		for (RegisterInputSource registerInputSource : io.getListSourceRegisterInput()) {
			addElementSource(registerInputSource, io, racineSecond, documentSecond, 1);
			dumpSource( registerInputSource, racineSecond, documentSecond, 1);
		}
		

	}

	private void logicalConeReadIO() {
		if (io.getDir() == OIDir.OUT) {
			return;
		}
		System.out.println("logicalConeReadIO");
		hdlEntity.searchReadSignalSource(io, ToolManager.getVectorName(io.toString()), 0, nbHierarchie);
		addElement(io, racineFirst, document, 0);
		for (RegisterInputRead registerInputRead : io.getListReadRegisterInput()) {
			dump(registerInputRead, racineFirst,  0);
		}

	}


	private void dump(RegisterInputRead registerInputRead, Element racineFirst, int incr) {
		incr++;
		for (RegisterInputRead registerRead : registerInputRead.getListReadRegisterInput()) {
			addElement(registerInputRead, registerRead, racineFirst, document, incr);
			dump(registerRead, racineFirst, incr);
		}
		
	}

	private void dumpSource(RegisterInputSource registerInputSource, Element racine, Document document, int incr) {
		incr++;
		for (RegisterInputSource registerSource : registerInputSource.getListSourceRegisterInput()) {
			addElementSource(registerInputSource, registerSource, racine, document, incr);
			dumpSource(registerSource, racine, document, incr);
		}
		
	}


}
