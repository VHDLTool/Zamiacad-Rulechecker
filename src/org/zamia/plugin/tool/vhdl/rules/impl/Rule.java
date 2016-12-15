package org.zamia.plugin.tool.vhdl.rules.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalReadManager;
import org.zamia.plugin.tool.vhdl.manager.ProcessManager;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalReadManager;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

public abstract class Rule extends RuleManager {
	
	protected class ProcessInfo {
		private Entity _entity;
		private Architecture _architecture;
		
		public ProcessInfo(Entity entity, Architecture architecture) {
			_entity = entity;
			_architecture = architecture;
		}

		public Entity getEntity() { return _entity; }
		public Architecture getArchitecture() { return _architecture; }
	}
	
	private RuleE _ruleE;
	private String _ruleId;
	ParameterSource _parameterSource;
	
	protected Rule(RuleE ruleE) {
		_ruleE = ruleE;
	}
	
	/**
	 * This method is run to execute a rule verification.
	 * 
	 * @param zPrj The project
	 * @param ruleId The rule identifier
	 * @param parameterSource The source of the parameters
	 * @return number of violation and rule result
	 */
	public abstract Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) ;


	public RuleE getRuleE() { return _ruleE; }
	
	public String getRuleId() { return _ruleId; }
	
	protected void initializeRule(ParameterSource parameterSource, String ruleId) {
		_parameterSource = parameterSource;
		_ruleId = ruleId;
	}
	
	protected List<IHandbookParam> getParameterList(ZamiaProject zPrj) {
		List<IHandbookParam> parameterList = null;
		if (_parameterSource != null) {
			String id;
			if (_parameterSource == ParameterSource.RULE_CHECKER) {
				id = _ruleE.getIdReq();
			} else {
				id = _ruleId;
			}

			parameterList = getXmlParameterFileConfig(zPrj, /*id*/ _ruleId, _parameterSource);
			if (parameterList == null) {
				// wrong param
				logger.info("Rule Checker: wrong parameter for rules " + _ruleId +  ".");
			}
		}

		return parameterList;
	}

	protected Dictionary<Process, ProcessInfo> getAllProcesses() {
		Dictionary<Process, ProcessInfo> processInfos = new Hashtable<Process, ProcessInfo>();
		
		try {
			Map<String, HdlFile> hdlFiles = ProcessManager.getProcess();
			for (Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				if (hdlFile.getListHdlEntity() == null) { continue;}
				
				for (HdlEntity hdlEntity : hdlFile.getListHdlEntity()) {
					if (hdlEntity.getListHdlArchitecture() == null) { continue;}
					
					for (HdlArchitecture hdlArchitecture : hdlEntity.getListHdlArchitecture()) {
						if (hdlArchitecture.getListProcess() == null) { continue;}
					
						for (Process process : hdlArchitecture.getListProcess()) {
							if (processInfos.get(process) == null) {
								ProcessInfo processInfo = new ProcessInfo(hdlEntity.getEntity(), hdlArchitecture.getArchitecture());
								processInfos.put(process, processInfo);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			processInfos = null;
			logger.error("Exception thrown while retrieving all processes.", e);
		}
		
		return processInfos;
	}
	
	protected List<ClockSignal> getAllClockSignals() {
		List<ClockSignal> clockSignals = new ArrayList<ClockSignal>();
		
		try {
			Map<String, HdlFile> hdlFiles = ClockSignalManager.getClockSignal();
			for (Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				if (hdlFile.getListHdlEntity() == null) { continue;}
				
				for (HdlEntity hdlEntity : hdlFile.getListHdlEntity()) {
					if (hdlEntity.getListHdlArchitecture() == null) { continue;}
					
					for (HdlArchitecture hdlArchitecture : hdlEntity.getListHdlArchitecture()) {
						if (hdlArchitecture.getListProcess() == null) { continue;}
					
						for (Process process : hdlArchitecture.getListProcess()) {
							if (process.getListClockSignal() == null) { continue;}
						
							for (ClockSignal clockSignal : process.getListClockSignal()) {
								if (!clockSignals.contains(clockSignal)) {
									clockSignals.add(clockSignal);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			clockSignals = null;
			logger.error("Exception thrown while retrieving all clock signals.", e);
		}
		
		return clockSignals;
	}
	
	protected List<ClockSource> getAllClockSources() {
		List<ClockSource> clockSources = new ArrayList<ClockSource>();
		
		try {
			ListClockSource listClockSource = ClockSignalReadManager.getClockReadSignal();
			clockSources = listClockSource.getListClockSource();
		} catch (Exception e) {
			clockSources = null;
			logger.error("Exception thrown while retrieving all clock sources.", e);
		}
		
		return clockSources;
	}
	
	protected List<ResetSignal> getAllResetSignals() {
		List<ResetSignal> resetSignals = new ArrayList<ResetSignal>();
	
		try {
			Map<String, HdlFile> hdlFiles = ResetSignalManager.getResetSignal();
			for (Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				if (hdlFile.getListHdlEntity() == null) { continue;}
				
				for (HdlEntity hdlEntity : hdlFile.getListHdlEntity()) {
					if (hdlEntity.getListHdlArchitecture() == null) { continue;}
					
					for (HdlArchitecture hdlArchitecture : hdlEntity.getListHdlArchitecture()) {
						if (hdlArchitecture.getListProcess() == null) { continue;}
					
						for (Process process : hdlArchitecture.getListProcess()) {
							if (process.getListClockSignal() == null) { continue;}
						
							for (ClockSignal clockSignal : process.getListClockSignal()) {
								if (clockSignal.getListResetSignal() == null) { continue;}
								
								for (ResetSignal resetSignal : clockSignal.getListResetSignal()) {
									if (!resetSignals.contains(resetSignal)) {
										resetSignals.add(resetSignal);
									}
								}
							}							
						}
					}
				}
			}
		} catch (Exception e) {
			resetSignals = null;
			logger.error("Exception thrown while retrieving all reset signals.", e);
		}
		
		return resetSignals;
	}
	
	protected List<ResetSource> getAllResetReadSources() {
		List<ResetSource> resetSources = new ArrayList<ResetSource>();
		
		try {
			ListResetSource listResetSource = ResetSignalReadManager.getResetReadSignal();
			resetSources = listResetSource.getListResetSource();
		} catch (Exception e) {
			resetSources = null;
			logger.error("Exception thrown while retrieving all reset sources.", e);
		}
		
		return resetSources;
	}
	
	protected List<ResetSource> getAllResetSources() {
		List<ResetSource> resetSources = new ArrayList<ResetSource>();
		
		try {
			ListResetSource listResetSource = ResetSignalSourceManager.getResetSourceSignal();
			resetSources = listResetSource.getListResetSource();
		} catch (Exception e) {
			resetSources = null;
			logger.error("Exception thrown while retrieving all reset sources.", e);
		}
		
		return resetSources;
	}
	
	protected void LogNeedBuild() {
		logger.error("Current project needs a build.");
	}
}
