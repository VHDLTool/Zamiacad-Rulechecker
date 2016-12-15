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
	
	public SensitivityRuleViolation(String fileName, int line, Entity entity, Architecture architecture, Process process, String sensitivityName) {
		_fileName = fileName;
		_line = line;
		_entity = entity;
		_architecture = architecture;
		_process = process;
		_sensitivityName = sensitivityName;
	}
	
	public void generate(ReportFile reportFile) {
		String entityId = _entity.getId();
		String architectureId = _architecture.getId();
		Element info = reportFile.addViolation(_fileName, _line, entityId, architectureId);
		
		String processId = _process.getLabel(); 
		reportFile.addElement(ReportFile.TAG_PROCESS, processId, info); 
		reportFile.addElement(ReportFile.TAG_SENSITIVITY, _sensitivityName, info); 
	}
}
