package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
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
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.NumberReportE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ClockSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class RuleSTD_04800 extends RuleManager {

	// Clock Edge Sensitivity

	RuleE rule = RuleE.STD_04800;
	ZamiaProject zPrj;
	ClockSignal currentClockSignal;
	ListClockSource listClockSource;

	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		this.zPrj = zPrj;
		String fileName = "";

		Map<String, HdlFile> listHdlFile;

		try {
			listHdlFile = ClockSignalManager.getClockSignal();
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
		} catch (EntityException e) {
			logger.error("some exception message RuleSTD_04800", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		Element racineFirst = initReportFile(ruleId, rule.getType(), rule.getRuleName(), NumberReportE.FIRST);
		Element racineSecond = initReportFile(ruleId, rule.getType(), rule.getRuleName(), NumberReportE.SECOND);

		Integer cmptViolationFirst = 0;
		Integer cmptViolationSecond = 0;

		// init edge per project in clockSource
		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			clockSource.setEdgePerProject(EdgeE.NAN);
		}

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {

					if (checkClockMixEdges(hdlEntityItem)) {
						cmptViolationSecond++;
						if (hdlEntityItem.getListHdlArchitecture() != null) {
							for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
								if (hdlArchitectureItem.getListProcess() != null) {
									for (Process processItem : hdlArchitectureItem.getListProcess()) {
										if (processItem.isSynchronous()) {
											for (ClockSignal clockSignal : processItem.getListClockSignal()) {
												addViolationPerFile(document, racineFirst, hdlFile, hdlEntityItem, hdlArchitectureItem,
														processItem, listClockSource, clockSignal);
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


		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					if (hdlEntityItem.useClock()) {
						if (checkClockMixEdges(listClockSource)) {
							addViolationPerProject(documentSecond, racineSecond, hdlFile, hdlEntityItem, listClockSource);
						}
					}
				}
			}
		}

		cmptViolationFirst = countViolationPerProject();

		if (cmptViolationFirst != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType(), "rule", NumberReportE.FIRST);
		}
		if (cmptViolationSecond != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType(), "rule", NumberReportE.SECOND);
		}

		return new Pair<Integer, String> (cmptViolationFirst+cmptViolationSecond, fileName);

	}


	private int countViolationPerProject() {
		Integer cmptViolationFirst = 0;
		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			if (clockSource.getEdgePerProject() == EdgeE.BOTH) {
				cmptViolationFirst++;
			}
		}
		return cmptViolationFirst;
	}


	private boolean checkClockMixEdges(ListClockSource listClockSource2) {
		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			if (clockSource.getEdgePerProject() == EdgeE.BOTH) {
				return true;
			}
		}
		return false;
	}


	private boolean checkClockMixEdges(HdlEntity hdlEntityItem) {
		boolean clockMixEdges = false;
		for (ClockSource clockSource : listClockSource.getListClockSource()) {
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


	private void addViolationPerProject(Document document, Element racineSecond, HdlFile hdlFile,
			HdlEntity hdlEntityItem, ListClockSource listClockSource) {

		Element processElement = document.createElement(NodeType.ENTITY.toString());
		racineSecond.appendChild(processElement);

		processElement.appendChild(NewElement(document, "violationType"
				, "mixEdgesPerProject"));

		processElement.appendChild(NewElement(document, NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, hdlFile.getLocalPath()));

		processElement.appendChild(NewElement(document, NodeType.ENTITY.toString()+NodeInfo.NAME.toString()
				, hdlEntityItem.getEntity().getId()));


		for (ClockSource clockSource : listClockSource.getListClockSource()) {
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
			processElement.appendChild(NewElement(document, clockSource.getTag()+NodeInfo.TAG
					, clockSource.getTag()));

			processElement.appendChild(NewElement(document, clockSource.getTag()+NodeInfo.EDGE
					, edge.toString()));

		}


	}


	private void addViolationPerFile(Document document, Element racine, HdlFile hdlFile,
			HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem,
			Process processItem, ListClockSource listClockSource, ClockSignal clockSignal) {

		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			Element processElement = document.createElement(NodeType.CLOCK_SIGNAL.toString());
			racine.appendChild(processElement);
	
			processElement.appendChild(NewElement(document, "violationType"
					, "mixEdgesPerFile"));
	
			processElement.appendChild(NewElement(document, NodeType.FILE.toString()+NodeInfo.NAME.toString()
					, hdlFile.getLocalPath()));
	
			processElement.appendChild(NewElement(document, NodeType.ENTITY.toString()+NodeInfo.NAME.toString() 
					, hdlEntityItem.getEntity().getId()));
	
			processElement.appendChild(NewElement(document, NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString()
					, hdlArchitectureItem.getArchitecture().getId()));
	
			processElement.appendChild(NewElement(document, NodeType.PROCESS.toString()+NodeInfo.NAME.toString()
					, processItem.getLabel()));
	
			processElement.appendChild(NewElement(document, NodeType.PROCESS.toString()+NodeInfo.LOCATION.toString()
					, String.valueOf(processItem.getLocation().fLine)));
	
			processElement.appendChild(NewElement(document, NodeType.CLOCK_SIGNAL.toString()+NodeInfo.NAME.toString()
					, clockSignal.toString()));
	
			processElement.appendChild(NewElement(document, NodeType.CLOCK_SIGNAL.toString()+NodeInfo.LOCATION.toString()
					, String.valueOf(clockSignal.getLocation().fLine)));

			EdgeE edge = EdgeE.NAN;
			if (clockSource.equals(clockSignal.getClockSource())) {
				edge = clockSignal.getEdge();
			}

			processElement.appendChild(NewElement(document, clockSource.getTag()+NodeInfo.TAG
					, clockSource.getTag()));

			processElement.appendChild(NewElement(document, clockSource.getTag()+NodeInfo.EDGE
					, edge.toString()));

		}

	}


}
