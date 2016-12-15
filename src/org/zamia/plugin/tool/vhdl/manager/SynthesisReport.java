package org.zamia.plugin.tool.vhdl.manager;

import org.w3c.dom.Element;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.plugin.tool.vhdl.rules.StatusE;

public class SynthesisReport {
	
	private Element _root;
	private boolean _forRule;
	private SonarQubeReport _sonarQubeReport;
	
	public enum Purpose { ForRule, ForTool };
	
	public SynthesisReport(Purpose purpose) {
		_forRule = purpose.equals(Purpose.ForRule);
		if (_forRule) {
			_sonarQubeReport = new SonarQubeReport();
		}
		
		String rootTag = _forRule ? "rc:ruleReporting" : "rc:toolReporting";
		_root = ToolManager.initReportXml(rootTag, RuleTypeE.NA);
	}

	public void addToolReport(String id, StatusE statusE, String reportFileName) {
		ToolManager.addReportToolStatusXml(_root, id, statusE, reportFileName);
	}
	
	public void addRuleReport(String id, StatusE statusE) {
		addRuleReport(id, statusE, null, 0, null);
	}
	
	public void addRuleReport(String id, StatusE statusE, String reportFileName, int nbFailed, RuleResult ruleResult) {
		ToolManager.addReportRuleStatusXml(_root, id, statusE, nbFailed, reportFileName);
		if (nbFailed > 0) {
			_sonarQubeReport.addLogs(id, ruleResult);
		}
	}
	
	public String saveToFile() {
		if (_forRule) {
			_sonarQubeReport.saveToFile();
		}
		
		String reportFileName = ToolManager.finishReportXml(_forRule ? RuleTypeE.RULE : RuleTypeE.TOOL);
		return reportFileName;
	}
}
