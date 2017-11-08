package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EdgeE;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.NumberReportE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;

/*
 * Clock Edge Sensitivity.
 * A unique single sensitive edge is used to clock all Flip-Flop in the same clock domain.
 * No parameters.
 */
public class RuleSTD_04800 extends Rule {

	private Map<String, HdlFile> _listHdlFile;
	private ListClockSource _listClockSource;
	
	public RuleSTD_04800() {
		super(RuleE.STD_04800);
	}

	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		//// Make the clock source list.

		try {
			_listHdlFile = ClockSignalManager.getClockSignal();
			_listClockSource = ClockSignalSourceManager.getClockSourceSignal();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<Integer, RuleResult> (NO_BUILD, null);
		}


		//// Initialize edge in clock sources.
		
		for (ClockSource clockSource : _listClockSource.getListClockSource()) {
			clockSource.setEdgePerProject(EdgeE.NAN);
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

					if (checkClockMixEdges(hdlEntityItem)) {
						if (hdlEntityItem.getListHdlArchitecture() != null) {
							for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
								if (hdlArchitectureItem.getListProcess() != null) {
									for (Process processItem : hdlArchitectureItem.getListProcess()) {
										if (processItem.isSynchronous()) {
											for (ClockSignal clockSignal : processItem.getListClockSignal()) {
												addViolationPerFile(reportFile, hdlFile, hdlEntityItem, hdlArchitectureItem, processItem, clockSignal);
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
						if (checkClockMixEdges()) {
							addViolationPerProject(reportFile, hdlFile, hdlEntityItem);
						}
					}
				}
			}
		}
		
		Pair<Integer, RuleResult> result = reportFile.save(NumberReportE.SECOND);
		return result;
	}
	
	private boolean checkClockMixEdges() {
		for (ClockSource clockSource : _listClockSource.getListClockSource()) {
			if (clockSource.getEdgePerProject() == EdgeE.BOTH) {
				return true;
			}
		}
		return false;
	}

	private boolean checkClockMixEdges(HdlEntity hdlEntityItem) {
		boolean clockMixEdges = false;
		for (ClockSource clockSource : _listClockSource.getListClockSource()) {
			EdgeE edge = EdgeE.NAN;
			if (hdlEntityItem.getListHdlArchitecture() != null) {
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListProcess() != null) {
						for (Process processItem : hdlArchitectureItem.getListProcess()) {
							if (processItem.isSynchronous()) {
								for (ClockSignal clockSignal : processItem.getListClockSignal()) {
									if (clockSource.equals(clockSignal.getClockSource())) {
										edge = update(edge, clockSignal.getEdge());
									}
								}
							}
						}
					}
				}
			}
			clockSource.setEdgePerProject(update(edge, clockSource.getEdgePerProject()));
			if (edge == EdgeE.BOTH) {
				clockMixEdges = true;
			}
		}
		return clockMixEdges;
	}

	private void addViolationPerFile(ReportFile reportFile, HdlFile hdlFile, HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem,	Process processItem, ClockSignal clockSignal) {
		String fileName = hdlFile.getLocalPath();
		int line = clockSignal.getLocation().fLine;
		String entityId = hdlEntityItem.getEntity().getId();
		String architectureId = hdlArchitectureItem.getArchitecture().getId();
		
		for (ClockSource clockSource : _listClockSource.getListClockSource()) {
			EdgeE edge = EdgeE.NAN;
			if (clockSource.equals(clockSignal.getClockSource())) {
				edge = clockSignal.getEdge();
			}

			if (edge != EdgeE.NAN) {
				Element info = reportFile.addViolationPerFile(fileName, line, entityId, architectureId);
				reportFile.addElement(ReportFile.TAG_CLOCK, clockSignal.toString(), info);
				reportFile.addElement(ReportFile.TAG_PROCESS, processItem.getLabel(), info); 

				reportFile.addElement(ReportFile.TAG_SOURCE_TAG, clockSource.getTag(), info); 
				reportFile.addElement(ReportFile.TAG_SIGNAL_EDGE, edge.toString(), info);
				
				reportFile.addElement(ReportFile.TAG_SONAR_ERROR, "Clock signal " + clockSignal.toString() + " use " + edge.toString() + " edge on contrary of other clock signal inside " + entityId, info);
				reportFile.addElement(ReportFile.TAG_SONAR_MSG, "Check that clock domain change mechanism is an authorized one", info);
			}
		}
	}

	private void addViolationPerProject(ReportFile reportFile, HdlFile hdlFile, HdlEntity hdlEntityItem) {
		String fileName = hdlFile.getLocalPath();
		int line = 0;
		String entityId = hdlEntityItem.getEntity().getId();
		String architectureId = null;
		
		for (ClockSource clockSource : _listClockSource.getListClockSource()) {
			EdgeE edge = EdgeE.NAN;
			if (hdlEntityItem.getListHdlArchitecture() != null) {
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListProcess() != null) {
						for (Process processItem : hdlArchitectureItem.getListProcess()) {
							if (processItem.isSynchronous()) {
								for (ClockSignal clockSignal : processItem.getListClockSignal()) {
									if (clockSource.equals(clockSignal.getClockSource())) {
										edge = update(edge, clockSignal.getEdge());
									}
								}
							}
						}
					}
				}
			}

			if (edge != EdgeE.NAN) {
				Element info = reportFile.addViolationPerProject(fileName, line, entityId, architectureId);
				reportFile.addElement(ReportFile.TAG_SOURCE_TAG, clockSource.getTag(), info); 
				reportFile.addElement(ReportFile.TAG_SIGNAL_EDGE, edge.toString(), info);
				
				reportFile.addElement(ReportFile.TAG_SONAR_ERROR, "Clock signal " + clockSource.toString() + " use rising edge on contrary of other clock signal inside the design", info);
				reportFile.addElement(ReportFile.TAG_SONAR_MSG, "Check that only one file in the design implements clock change from rising edge to falling edge", info);
			}
		}
	}
}
