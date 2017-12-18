package org.zamia.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.zamia.SourceFile;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.ZamiaProjectBuilder;
import org.zamia.plugin.tool.vhdl.manager.HdlFileManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager;
import org.zamia.plugin.tool.vhdl.manager.SynthesisReport;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.plugin.tool.vhdl.rules.StatusE;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.util.HashSetArray;
import org.zamia.util.Pair;

/**
 * The RuleCheckerCmd class is used exclusively to run build and rule check on a VHDL project 
 * from the command line (see Check class).
 */
public class RuleCheckerCmd  {
	private static final String RULE_CHECKER = "rule_checker";
	private static final String CONFIG_SELECTED_RULES = "rc_config_selected_rules.xml";
	private static final String CONFIG_SELECTED_TOOLS = "rc_config_selected_tools.xml";
	private static final String BUILD_FILE_NAME = "BuildPath.txt";
	
	private static final String RULE_EXEC = "Launch";
	
	private String _vhdlProjectDirectory;
	private ZamiaProject _zamiaProject;
	private String _configDirectory;
	private ZamiaLogger _logger = ZamiaLogger.getInstance();
	
	public boolean checkRules(String vhdlProjectDirectory) {
		boolean ok = false;
		_vhdlProjectDirectory = vhdlProjectDirectory;
		
		if (createZamiaProject()) {
			RuleInfo[] ruleInfos = readSelectedRules(RuleTypeE.RULE);
			boolean hasSelectedRules = ruleInfos != null && ruleInfos.length > 0;
			
			RuleInfo[] toolInfos = readSelectedRules(RuleTypeE.TOOL);
			boolean hasSelectedTools = toolInfos != null && toolInfos.length > 0;
			
			if (hasSelectedRules || hasSelectedTools) {
				_logger.info("RuleChecker: start building project.");
				if (buildProject()) {
					if (hasSelectedRules) {
						_logger.info("RuleChecker: start checking selected rules.");
						checkRulesImpl(ruleInfos);
					} else {
						_logger.info("RuleChecker: no selected rules.");
					}
					
					if (hasSelectedTools) {
						_logger.info("RuleChecker: start checking selected tools.");
						checkToolsImpl(toolInfos);
					} else {
						_logger.info("RuleChecker: no selected tools.");
					}
				}
			} else {
				_logger.info("RuleChecker: nothing to do (no selected rules or tools).");
			}
		
			disposeZamiaProject();
		}
	
		return ok;	
	}
	
	private boolean createZamiaProject() {
		boolean ok = false;
		
		_configDirectory = _vhdlProjectDirectory + "/" + RULE_CHECKER;

		File file = new File(_vhdlProjectDirectory);
		String projectName = file.getName();
		
		SourceFile buildFilePath = new SourceFile(new File(_vhdlProjectDirectory + "/" + BUILD_FILE_NAME), BUILD_FILE_NAME);
		try {
			_zamiaProject = new ZamiaProject(projectName, _vhdlProjectDirectory, buildFilePath, null);
			ToolManager.setFromPlugin(false);
			ToolManager.init(_zamiaProject);
			ok = true;
		} catch (Exception e) {
			_logger.error("RuleChecker: exception thrown while initializing Zamia project.", e);
		}
		
		return ok;
	}
	
	private void disposeZamiaProject() {
		if (_zamiaProject != null) {
			_zamiaProject.shutdown();
		}
	}
	
	private RuleInfo[] readSelectedRules(RuleTypeE ruleType) {
		RuleInfo[] ruleInfos = null;
		String filePath = String.format("%s/%s", _configDirectory, ruleType == RuleTypeE.RULE ? CONFIG_SELECTED_RULES : CONFIG_SELECTED_TOOLS); 
				
		ZamiaLogger logger = ZamiaLogger.getInstance();
		logger.info("Selected Rule file path: %s", filePath);
		
		try {
			File file = new File(filePath);		
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			String tagName = "hb:" + ruleType;
			NodeList nodeList = doc.getElementsByTagName(tagName);			
			int nodeCount = nodeList.getLength();
			ruleInfos = new RuleInfo[nodeCount];
			logger.info("Number of selected rules: %d", nodeCount);
			for (int index = 0; index < nodeCount; ++index) {
				Element nodeElement = (Element) nodeList.item(index);
				String id = nodeElement.getAttribute("UID");
				String parameterSource = nodeElement.getAttribute("ParameterSource");
				ruleInfos[index] = new RuleInfo(id, parameterSource);
			}
			
		} catch (Exception e) {
			logger.error("RuleChecker: exception=", e);
			ruleInfos = null;
		}
		
		return ruleInfos;
	}
	
	private boolean checkRulesImpl(RuleInfo[] ruleInfos) {
		boolean ok = true;
		ToolManager.deleteDirectory("rule");
		SynthesisReport synthesisReport = new SynthesisReport(SynthesisReport.Purpose.ForRule);
		
		for (RuleInfo ruleInfo : ruleInfos) {
			if (!checkRule(ruleInfo, true, synthesisReport)) {
				ok = false;
			}
		}
	
		synthesisReport.saveToFile();
		
		return ok;	
	}
	
	private boolean checkToolsImpl(RuleInfo[] ruleInfos) {
		boolean ok = true;
		ToolManager.deleteDirectory("tool");
		SynthesisReport synthesisReport = new SynthesisReport(SynthesisReport.Purpose.ForTool);
		
		for (RuleInfo ruleInfo : ruleInfos) {
			if (!checkRule(ruleInfo, false, synthesisReport)) {
				ok = false;
			}
		}
	
		synthesisReport.saveToFile();
	
		return ok;	
	}
	
	private boolean checkRule(RuleInfo ruleInfo, boolean forRule, SynthesisReport synthesisReport) {
		boolean ok = false;
		
		String ruleId = ruleInfo.getRuleId();
		String stdRuleId = ruleId.startsWith("STD_") ? ruleId : "GEN_" + ruleId.substring(4);
		boolean isAlgo = false;
		if (forRule) {
			RuleE ruleE = RuleE.valueOf(stdRuleId);
			if (ruleE.getType() == RuleTypeE.ALGO) {
				isAlgo = true;
			}
		}
		
		ParameterSource parameterSource = ruleInfo.getParameterSource();
		
		Class<?> ruleClass = null;
		try {
			ruleClass = forRule ? getRuleClass(stdRuleId) : getToolClass(ruleId); 
		} catch (Exception e) {
			_logger.error(String.format("RuleChecker: cannot check rule %s (not implemented).", ruleId));
		}

		if (ruleClass != null) {
			Object object = null;
			try {
				if (forRule) {
					_logger.info(String.format("RuleChecker: check rule %s.", ruleId));
				} else {
					_logger.info(String.format("RuleChecker: execute tool %s.", ruleId));
				}
	
				Object rule = ruleClass.newInstance();
				
				Class<?>[] paramTypes = new Class[3];
		        paramTypes[0] = ZamiaProject.class;
		        paramTypes[1] = String.class;
		        paramTypes[2] = ParameterSource.class;
				Method method = ruleClass.getDeclaredMethod(RULE_EXEC, paramTypes);
				
				object = method.invoke(rule, _zamiaProject, ruleId, parameterSource);
				
				ok = true;
			} catch (Exception e) {
				ok = false;
			}

			if (forRule) {
				@SuppressWarnings("unchecked")
				Pair<Integer, RuleResult> returnObject = (Pair<Integer, RuleResult>) object;
				if (returnObject == null) {
					ok = false;
				}
					
				if (!ok) {
					_logger.error(String.format("RuleChecker: checking rule %s failed.", stdRuleId));
					synthesisReport.addRuleReport(stdRuleId, StatusE.NOT_EXECUTED);
				} else {
					int result = returnObject.getFirst();
					RuleResult ruleResult = returnObject.getSecond();

					if (result == ReportManager.WRONG_PARAM) {
						_logger.info(String.format("RuleChecker: wrong parameter for rule %s.", stdRuleId));
						synthesisReport.addRuleReport(stdRuleId, StatusE.NOT_EXECUTED);
					} else if (result == ReportManager.NO_BUILD) {
						_logger.info("RuleChecker: project must be rebuild.");
						synthesisReport.addRuleReport(stdRuleId, StatusE.NOT_EXECUTED);
					} else if (result == 0) {
						_logger.info(String.format("RuleChecker: no violations for rule %s.", stdRuleId));
						synthesisReport.addRuleReport(stdRuleId, isAlgo ? StatusE.PASSED : StatusE.REPORTED);
					} else {
						_logger.info(String.format("RuleChecker: %d violations for rule %s.", result, stdRuleId));
						String reportFileName = ruleResult.getReportFileName();
						synthesisReport.addRuleReport(stdRuleId, StatusE.FAILED, reportFileName, result, ruleResult);
					}
				}
			} else {
				@SuppressWarnings("unchecked")
				Pair<Integer, String> returnObject = (Pair<Integer, String>) object;
				if (returnObject == null) {
					ok = false;
				}
				
				if (!ok) {
					_logger.error(String.format("RuleChecker: tool %s failed.", stdRuleId));
					synthesisReport.addToolReport(stdRuleId, StatusE.NOT_EXECUTED, null);					 
				} else {
					int result = returnObject.getFirst();
					
					if (result == ReportManager.WRONG_PARAM) {
						_logger.info(String.format("RuleChecker: wrong parameter for rule %s.", stdRuleId));
						synthesisReport.addToolReport(stdRuleId, StatusE.NOT_EXECUTED, null);
					} else if (result == ReportManager.NO_BUILD) {
						_logger.info(String.format("RuleChecker: project must be rebuild.", stdRuleId));
						synthesisReport.addToolReport(stdRuleId, StatusE.NOT_EXECUTED, null);
					} else {
						_logger.info(String.format("RuleChecker: tool %s done.", stdRuleId));
						String reportFileName = returnObject.getSecond();
						synthesisReport.addToolReport(stdRuleId, StatusE.REPORTED, reportFileName);
					}
				}
			}
		}
		
		return ok;
	}
	
	private static RuleTypeE[] getRuleTypes() {
		RuleTypeE[] types = { RuleTypeE.RULE, RuleTypeE.TOOL };
		return types;
	}
	
	private static Class<?> getRuleClass(String ruleId) {
		RuleE ruleEnum = RuleE.valueOf(ruleId);
		Class<?> ruleClass = ruleEnum.getClassRule(); 
		return ruleClass;
	}
	
	private static Class<?> getToolClass(String toolId) {
		ToolE toolEnum = ToolE.valueOf(toolId);
		Class<?> toolClass = toolEnum.getClassRule(); 
		return toolClass;
	}
	
	private boolean buildProject() {
		boolean ok = false;
		
		File projectDirectoryFile = new File(_vhdlProjectDirectory);
		List<File> vhdlFiles = HdlFileManager.getVhdlFile(projectDirectoryFile);

		HashSetArray<SourceFile> sourceFiles = new HashSetArray<SourceFile>();
		for(File file : vhdlFiles) {
			SourceFile sourceFile = new SourceFile(file,file.getPath());
			sourceFiles.add(sourceFile);
		}
		
		try {
			ZamiaProjectBuilder builder = _zamiaProject.getBuilder();
			builder.build(true, true, null);
			ok = true;
		} catch (Exception e) {
			_logger.error("RuleChecker: building project failed.", e);
		}
		
		return ok;
	} 
	
	private class RuleInfo {
		private String _ruleId;
		private ParameterSource _parameterSource;
		
		public RuleInfo(String ruleId, String parameterSource) {
			_ruleId = ruleId;
			if (parameterSource != null && parameterSource.length() > 0) {
				_parameterSource = ParameterSource.valueOf(parameterSource);
			} else {
				_parameterSource = ParameterSource.RULE_CHECKER;
			}
		}
		
		public String getRuleId() { return _ruleId; }
		public ParameterSource getParameterSource() { return _parameterSource; }
	}
}
