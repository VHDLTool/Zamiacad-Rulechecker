package org.zamia.plugin.tool.vhdl.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RuleResult {
	private String _reportFileName;
	private Map<String, Set<Integer>> _vhdlToErrorLines;
	
	public RuleResult() {
	}
	
	public void addError(String vhdlFile, int line) {
		if (_vhdlToErrorLines == null) {
			_vhdlToErrorLines = new HashMap<String, Set<Integer>>();
		}
		
		Set<Integer> lines = _vhdlToErrorLines.get(vhdlFile);
		if (lines == null) {
			lines = new HashSet<Integer>();
			_vhdlToErrorLines.put(vhdlFile, lines);
		}
		
		if (!lines.contains(line)) {
			lines.add(line);
		}
	}
	
	public void setReportFileName(String reportFileName) {
		_reportFileName = reportFileName;
	}
	
	public String getReportFileName() {
		return _reportFileName;
	}

	public Set<String> getVhdlFiles() {
		return _vhdlToErrorLines.keySet();
	}
	
	public Set<Integer> getErrorLines(String vhdlFile) {
		return _vhdlToErrorLines.get(vhdlFile); 
	}
}
