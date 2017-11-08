package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.LevelE;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.NumberReportE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;

/*
 * Reset Sensitive Level.
 * Every synchronous process uses the same reset activation level.
 * No parameters.
 */
public class RuleSTD_03600 extends Rule {
	
	private Map<String, HdlFile> _listHdlFile;
	private ListResetSource _listResetSource;
	
	public RuleSTD_03600() {
		super(RuleE.STD_03600);
	}

	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {

		initializeRule(parameterSource, ruleId);
		
		//// Make the reset source list.

		try {
			_listHdlFile = ResetSignalManager.getResetSignal();
			_listResetSource = ResetSignalSourceManager.getResetSourceSignal();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<Integer, RuleResult>(NO_BUILD, null);
		}

		//// Initialize level in reset sources.
		
		for (ResetSource resetSource : _listResetSource.getListResetSource()) {
			resetSource.setLevelPerProject(LevelE.NAN);
		}

		//// Retrieve and save all violations per file.
		
		Pair<Integer, RuleResult> result1 = checkRulePerFile();
		
		//// Retrieve and save all violations per project.

		Pair<Integer, RuleResult> result2 = checkRulePerProject();
		
		//// Return check result.

		int totalViolationCount = result1.getFirst() + result2.getFirst();
		RuleResult ruleResult = null;
		if (result1.getFirst() > 0) {
			ruleResult = result1.getSecond(); 
		} else if (result2.getFirst() > 0) {
			ruleResult = result2.getSecond(); 
		}
				
		return new Pair<Integer, RuleResult>(totalViolationCount, ruleResult);
	}
	
	private Pair<Integer, RuleResult> checkRulePerFile() {
		ReportFile reportFile = new ReportFile(this);
		if (!reportFile.initialize()) 
			return null;

		for (Entry<String, HdlFile> entry : _listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {

					if (checkResetMixLevels(hdlEntityItem)) {
						if (hdlEntityItem.getListHdlArchitecture() != null) {
							for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
								if (hdlArchitectureItem.getListProcess() != null) {
									for (Process processItem : hdlArchitectureItem.getListProcess()) {
										if (processItem.isSynchronous()) {
											for (ClockSignal clockSignal : processItem.getListClockSignal()) {
												if (clockSignal.hasSynchronousReset()) {
													for (ResetSignal resetSignal : clockSignal.getListResetSignal()) {
														addViolationPerFile(reportFile, hdlFile, hdlEntityItem, hdlArchitectureItem, processItem, resetSignal);
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
		
		Pair<Integer, RuleResult> result = reportFile.save(NumberReportE.FIRST);
		return result;
	}
	
	private Pair<Integer, RuleResult> checkRulePerProject() {
		ReportFile reportFile = new ReportFile(this);
		if (!reportFile.initialize()) 
			return null;
		
		for(Entry<String, HdlFile> entry : _listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					if (hdlEntityItem.useClock()) {
						if (checkResetMixLevels()) {
							addViolationPerProject(reportFile, hdlFile, hdlEntityItem);
						}
					}
				}
			}
		}

		Pair<Integer, RuleResult> result = reportFile.save(NumberReportE.SECOND);
		return result;
	}
	
	private boolean checkResetMixLevels() {
		for (ResetSource resetSource : _listResetSource.getListResetSource()) {
			if (resetSource.getLevelPerProject() == LevelE.BOTH) {
				return true;
			}
		}
		return false;
	}

	private boolean checkResetMixLevels(HdlEntity hdlEntityItem) {
		boolean resetMixLevels = false;
		
		for (ResetSource resetSource : _listResetSource.getListResetSource()) {
			LevelE level = LevelE.NAN;
			if (hdlEntityItem.getListHdlArchitecture() != null) {
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListProcess() != null) {
						for (Process processItem : hdlArchitectureItem.getListProcess()) {
							if (processItem.isSynchronous()) {
								for (ClockSignal clockSignal : processItem.getListClockSignal()) {
									if (clockSignal.hasSynchronousReset()) {
										for (ResetSignal resetSignal : clockSignal.getListResetSignal()) {
											try {
												level = update(level, resetSignal.getLevel());
											} catch (Exception e) {
												logger.error("some exception message RuleSTD_03600 checkResetMixLevels", e);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			resetSource.setLevelPerProject(update(level, resetSource.getLevelPerProject()));
			if (level == LevelE.BOTH) {
				resetMixLevels = true;
			}
		}
		return resetMixLevels;
	}

	private void addViolationPerFile(ReportFile reportFile, HdlFile hdlFile, HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem,	Process processItem, ResetSignal resetSignal) {

		String fileName = hdlFile.getLocalPath();
		int line = resetSignal.getLocation().fLine;
		String entityId = hdlEntityItem.getEntity().getId();
		String architectureId = hdlArchitectureItem.getArchitecture().getId();
		
		for (ResetSource resetSource : _listResetSource.getListResetSource()) {
			LevelE level = LevelE.NAN;
			if (resetSource.equals(resetSignal.getResetSource())) {
				level = resetSignal.getLevel();
			}

			if (level != LevelE.NAN) {
				Element info = reportFile.addViolationPerFile(fileName, line, entityId, architectureId);
				reportFile.addElement(ReportFile.TAG_RESET, resetSignal.toString(), info);
				reportFile.addElement(ReportFile.TAG_PROCESS, processItem.getLabel(), info); 
				
				reportFile.addElement(ReportFile.TAG_SOURCE_TAG, resetSource.getTag(), info); 
				reportFile.addElement(ReportFile.TAG_SOURCE_LEVEL, level.toString(), info);
				
				reportFile.addElement(ReportFile.TAG_SONAR_ERROR, "Reset signal " + resetSignal.toString() + " is active " + level.toString() + " on contrary of other reset signal inside " + entityId, info);
				reportFile.addElement(ReportFile.TAG_SONAR_MSG, "Choose a unique reset polarity for every reset signal in entity " + entityId, info);

			}
		}
	}

	private void addViolationPerProject(ReportFile reportFile, HdlFile hdlFile, HdlEntity hdlEntityItem) {

		String fileName = hdlFile.getLocalPath();
		int line = 0;
		String entityId = hdlEntityItem.getEntity().getId();
		String architectureId = null;
		
		for (ResetSource resetSource : _listResetSource.getListResetSource()) {
			LevelE level = LevelE.NAN;
			if (hdlEntityItem.getListHdlArchitecture() != null) {
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListProcess() != null) {
						for (Process processItem : hdlArchitectureItem.getListProcess()) {
							if (processItem.isSynchronous()) {
								for (ClockSignal clockSignal : processItem.getListClockSignal()) {
									if (clockSignal.hasSynchronousReset()) {
										for (ResetSignal resetSignal : clockSignal.getListResetSignal()) {
											if (resetSource.equals(resetSignal.getResetSource())) {
												level = update(level, resetSignal.getLevel());
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			if (level != LevelE.NAN) {
				Element info = reportFile.addViolationPerProject(fileName, line, entityId, architectureId);
				reportFile.addElement(ReportFile.TAG_SOURCE_TAG, resetSource.getTag(), info); 
				reportFile.addElement(ReportFile.TAG_SOURCE_LEVEL, level.toString(), info); 
				
				reportFile.addElement(ReportFile.TAG_SONAR_ERROR, "Reset signal " + resetSource.toString() + " is active " + level.toString() + " on contrary of other reset signal inside the design", info);
				reportFile.addElement(ReportFile.TAG_SONAR_MSG, "Choose a unique reset polarity for every reset signal in the design", info);
			}
		}
	}
}
