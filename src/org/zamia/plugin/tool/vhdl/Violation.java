package org.zamia.plugin.tool.vhdl;

import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

public class Violation {

	private String error;
	private String name;
	private int fLine;
	private String localPath;
	private Entity entity;
	private Architecture architecture;
	private Process processItem;
	private ClockSignal clockSignalItem;
	
	public Violation(String _error, String _name, int _fLine, String _localPath,
			Entity _entity, Architecture _architecture, Process _processItem,
			ClockSignal _clockSignalItem) {
		error = _error;
		name = _name;
		fLine = _fLine;
		localPath = _localPath;
		entity = _entity;
		architecture = _architecture;
		processItem = _processItem;
		clockSignalItem = _clockSignalItem;
	}

	public String getError() {
		return error;
	}
	
	public String getName() {
		return name;
	}
	
	public int getfLine() {
		return fLine;
	}
	
	public String getLocalPath() {
		return localPath;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public Architecture getArchitecture() {
		return architecture;
	}
	
	public Process getProcessItem() {
		return processItem;
	}
	
	public ClockSignal getClockSignalItem() {
		return clockSignalItem;
	}
	
}
