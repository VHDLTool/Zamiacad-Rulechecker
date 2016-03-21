package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.LevelE;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.NumberReportE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalManager;
import org.zamia.plugin.tool.vhdl.manager.ResetSignalSourceManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class RuleSTD_03600 extends RuleManager {

	// Reset Sensitive Level
	
	RuleE rule = RuleE.STD_03600;
	ZamiaProject zPrj;
	
	ResetSignal currentResetSignal;
	ListResetSource listResetSource;
	boolean error = false;
	
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		this.zPrj = zPrj;
		String fileName = "";

		Map<String, HdlFile> listHdlFile;

		try {
			listHdlFile = ResetSignalManager.getResetSignal();
			listResetSource = ResetSignalSourceManager.getResetSourceSignal();
		} catch (EntityException e) {
			logger.error("some exception message RuleSTD_03600", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		Element racineFirst = initReportFile(ruleId, rule.getType(), rule.getRuleName(), NumberReportE.FIRST);
		Element racineSecond = initReportFile(ruleId, rule.getType(), rule.getRuleName(), NumberReportE.SECOND);

		Integer cmptViolationFirst = 0;
		Integer cmptViolationSecond = 0;

		// init edge per project in clockSource
		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			resetSource.setLevelPerProject(LevelE.NAN);
		}

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {

					if (checkResetMixLevels(hdlEntityItem)) {
						cmptViolationSecond++;
						if (hdlEntityItem.getListHdlArchitecture() != null) {
							for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
								if (hdlArchitectureItem.getListProcess() != null) {
									for (Process processItem : hdlArchitectureItem.getListProcess()) {
										if (processItem.isSynchronous()) {
											for (ClockSignal clockSignal : processItem.getListClockSignal()) {
												if (clockSignal.hasSynchronousReset()) {
													for (ResetSignal resetSignal : clockSignal.getListResetSignal()) {
														addViolationPerFile(document, racineFirst, hdlFile, hdlEntityItem, hdlArchitectureItem,
																processItem, listResetSource, resetSignal);
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


		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					if (hdlEntityItem.useClock()) {
						if (checkResetMixLevels(listResetSource)) {
							addViolationPerProject(documentSecond, racineSecond, hdlFile, hdlEntityItem, listResetSource);
						}
					}
				}
			}
		}

		cmptViolationFirst = countViolationPerProject();
System.out.println("cmptViolationFirst "+cmptViolationFirst);
		if (cmptViolationFirst != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType(), "rule", NumberReportE.FIRST);
		}
		System.out.println("cmptViolationSecond "+cmptViolationSecond);
		if (cmptViolationSecond != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType(), "rule", NumberReportE.SECOND);
		}

		return new Pair<Integer, String> (cmptViolationFirst+cmptViolationSecond, fileName);

	}
	
	private int countViolationPerProject() {
		Integer cmptViolationFirst = 0;
		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			if (resetSource.getLevelPerProject() == LevelE.BOTH) {
				cmptViolationFirst++;
			}
		}
		return cmptViolationFirst;
	}


	private boolean checkResetMixLevels(ListResetSource listResetSource) {
		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			if (resetSource.getLevelPerProject() == LevelE.BOTH) {
				return true;
			}
		}
		return false;
	}


	private boolean checkResetMixLevels(HdlEntity hdlEntityItem) {
		boolean resetMixLevels = false;
		
		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			LevelE level = LevelE.NAN;
			if (hdlEntityItem.getListHdlArchitecture() != null) {
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListProcess() != null) {
						for (Process processItem : hdlArchitectureItem.getListProcess()) {
							if (processItem.isSynchronous()) {
								for (ClockSignal clockSignal : processItem.getListClockSignal()) {
									if (clockSignal.hasSynchronousReset()) {
										for (ResetSignal resetSignal : clockSignal.getListResetSignal()) {
											System.out.println("resetSignal "+resetSignal);
											System.out.println("resetSource  "+resetSource);
											System.out.println("resetSignal.getResetSource() "+resetSignal.getResetSource());
											try {
												if (resetSource.equals(resetSignal.getResetSource())) {
													level = update(level, resetSignal.getLevel());
												}
											} catch (Exception e) {
												logger.error("some exception message RuleSTD_03600 checkResetMixLevels", e);
												if (! error) {
													JOptionPane.showMessageDialog(null, "<html>no source reset find for signal reset "+resetSignal+" </html>", "Error",
															JOptionPane.ERROR_MESSAGE);
												}
												error = true;
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


	private void addViolationPerProject(Document document, Element racineSecond, HdlFile hdlFile,
			HdlEntity hdlEntityItem, ListResetSource listResetSource) {

		Element processElement = document.createElement(NodeType.ENTITY.toString());
		racineSecond.appendChild(processElement);

		processElement.appendChild(NewElement(document, "violationType"
				, "mixLevelPerProject"));

		processElement.appendChild(NewElement(document, NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, hdlFile.getLocalPath()));

		processElement.appendChild(NewElement(document, NodeType.ENTITY.toString()+NodeInfo.NAME.toString()
				, hdlEntityItem.getEntity().getId()));


		for (ResetSource resetSource : listResetSource.getListResetSource()) {
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
			processElement.appendChild(NewElement(document, resetSource.getTag()+NodeInfo.TAG
					, resetSource.getTag()));

			processElement.appendChild(NewElement(document, resetSource.getTag()+NodeInfo.LEVEL
					, level.toString()));

		}


	}


	private void addViolationPerFile(Document document, Element racine, HdlFile hdlFile,
			HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem,
			Process processItem, ListResetSource listResetSource, ResetSignal resetSignal) {

		Element processElement = document.createElement(NodeType.RESET_SIGNAL.toString());
		racine.appendChild(processElement);

		processElement.appendChild(NewElement(document, "violationType"
				, "mixLevelPerFile"));

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

		processElement.appendChild(NewElement(document, NodeType.RESET_SIGNAL.toString()+NodeInfo.NAME.toString()
				, resetSignal.toString()));

		processElement.appendChild(NewElement(document, NodeType.RESET_SIGNAL.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(resetSignal.getLocation().fLine)));

		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			LevelE level = LevelE.NAN;
			if (resetSource.equals(resetSignal.getResetSource())) {
				level = resetSignal.getLevel();
			}

			processElement.appendChild(NewElement(document, resetSource.getTag()+NodeInfo.TAG
					, resetSource.getTag()));

			processElement.appendChild(NewElement(document, resetSource.getTag()+NodeInfo.LEVEL
					, level.toString()));

		}
	}

}
