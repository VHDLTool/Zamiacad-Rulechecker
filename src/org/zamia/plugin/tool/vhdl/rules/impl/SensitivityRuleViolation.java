package org.zamia.plugin.tool.vhdl.rules.impl;

import org.w3c.dom.Element;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

public class SensitivityRuleViolation {
	private String _fileName;
	private int _line;
	private Entity _entity;
	private Architecture _architecture; 
	private Process _process;
	private String _sensitivityName;
	private boolean _sensitivityMissing;
	private boolean _synchronousProcess;
	
	public SensitivityRuleViolation(String fileName, int line, Entity entity, Architecture architecture, Process process, String sensitivityName, boolean synchronousProcess, boolean sensitivityMissing) {
		_fileName = fileName;
		_line = line;
		_entity = entity;
		_architecture = architecture;
		_process = process;
		_sensitivityName = sensitivityName;
		_sensitivityMissing = sensitivityMissing;
		_synchronousProcess = synchronousProcess;
	}
	
	public void generate(ReportFile reportFile) {
		String entityId = _entity.getId();
		String architectureId = _architecture.getId();
		Element info = reportFile.addViolation(_fileName, _line, entityId, architectureId);
		
		String processId = _process.getLabel(); 
		reportFile.addElement(ReportFile.TAG_PROCESS, processId, info); 
		reportFile.addElement(ReportFile.TAG_SENSITIVITY, _sensitivityName, info); 

		if (_synchronousProcess)
		{
			// Rule STD_05000
			if (_sensitivityMissing)
			{
				reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_05000_MISSING, new Object[] {_sensitivityName}, SonarQubeRule.SONAR_MSG_STD_05000_MISSING, new Object[] {_sensitivityName, processId});
			}
			else
			{
				reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_05000_MORE, new Object[] {_sensitivityName}, SonarQubeRule.SONAR_MSG_STD_05000_MORE, null);
			}
		}
		else
		{
			// Rule STD_05300
			if (_sensitivityMissing)
			{
				reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_05300_MISSING, new Object[] {_sensitivityName}, SonarQubeRule.SONAR_MSG_STD_05300_MISSING, new Object[] {_sensitivityName, processId});
			}
			else
			{
				reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_05300_MORE, new Object[] {_sensitivityName}, SonarQubeRule.SONAR_MSG_STD_05300_MORE, new Object[] {_sensitivityName, processId});
			}
		}
	}
}
