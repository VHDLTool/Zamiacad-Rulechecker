package org.zamia.plugin.tool.vhdl.tools.impl;

import java.util.Map;
import java.util.Map.Entry;

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
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_AR_7 extends ToolSelectorManager {

	// Clock Mix Edges
	
	ToolE tool = ToolE.REQ_FEAT_AR7;

	public Pair<Integer, String> Launch(ZamiaProject zPrj, String toolId) {
		String fileName = "";
		
		ListResetSource listResetSource;
		Map<String, HdlFile> listHdlFile;
		
		try {
			listHdlFile = ResetSignalManager.getResetSignal();
			listResetSource = ResetSignalSourceManager.getResetSourceSignal();
		} catch (EntityException e) {
			logger.error("some exception message Tool_AR_7", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}
		
		Element racineThird1 = initReportFile(toolId, tool.getType(), tool.getRuleName(), NumberReportE.THIRD);
		Element racineSecond = initReportFile(toolId, tool.getType(), tool.getRuleName(), NumberReportE.SECOND);
		Element racineFirst1 = initReportFile(toolId, tool.getType(), tool.getRuleName(), NumberReportE.FIRST);

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					addReport(documentSecond, racineSecond, hdlFile, hdlEntityItem, listResetSource);
					if (hdlEntityItem.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
							if (hdlArchitectureItem.getListProcess() != null) {
								for (Process processItem : hdlArchitectureItem.getListProcess()) {
									if (processItem.hasSynchronousReset()) {
										for (ClockSignal clockSignal : processItem.getListClockSignal()) {
											for (ResetSignal resetSignal : clockSignal.getListResetSignal()) {
												addReport(document, racineFirst1, hdlFile, hdlEntityItem, hdlArchitectureItem, processItem, listResetSource, resetSignal);
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
		
		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			LevelE level = LevelE.NAN;
			for (ResetSignal resetSignal : resetSource.getListResetSignal()) {
				if (resetSource.equals(resetSignal.getResetSource())) {
					level = update(level, resetSignal.getLevel());
				}
			}
			addReport(documentThird, racineThird1, resetSource, level);
		}


		fileName = createReportFile(toolId, tool.getRuleName(), tool.getType(), "tool", NumberReportE.FIRST);
		fileName = createReportFile(toolId, tool.getRuleName(), tool.getType(), "tool", NumberReportE.SECOND);
		fileName = createReportFile(toolId, tool.getRuleName(), tool.getType(), "tool", NumberReportE.THIRD);

		return new Pair<Integer, String> (0, fileName);
	}

	private void addReport(Document document, Element racineSecond, HdlFile hdlFile,
			HdlEntity hdlEntityItem, ListResetSource listResetSource) {

		Element processElement = document.createElement(NodeType.ENTITY.toString());
		racineSecond.appendChild(processElement);

		Element fileNameElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
		fileNameElement.setTextContent(hdlFile.getLocalPath());
		processElement.appendChild(fileNameElement);

		Element entityNameElement = document.createElement(NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
		entityNameElement.setTextContent(hdlEntityItem.getEntity().getId());
		processElement.appendChild(entityNameElement);


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
			Element clockSourceElement = document.createElement(resetSource.getTag());
			clockSourceElement.setTextContent(resetSource.toString());
			processElement.appendChild(clockSourceElement);

			Element clockSourceEdgeElement = document.createElement(resetSource.getTag()+NodeInfo.EDGE);
			clockSourceEdgeElement.setTextContent(level.toString());
			processElement.appendChild(clockSourceEdgeElement);

		}
		
	
	}


	private void addReport(Document document, Element racineThird, ResetSource resetSource,
			LevelE level) {
		Element processElement = document.createElement(NodeType.RESET_SOURCE.toString());
		racineThird.appendChild(processElement);
		
		Element clockSourceElement = document.createElement(resetSource.getTag());
		clockSourceElement.setTextContent(resetSource.toString());
		processElement.appendChild(clockSourceElement);

		Element clockSourceEdgeElement = document.createElement(resetSource.getTag()+NodeInfo.LEVEL);
		clockSourceEdgeElement.setTextContent(level.toString());
		processElement.appendChild(clockSourceEdgeElement);

	}


	private void addReport(Document document, Element racine, HdlFile hdlFile,
			HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem,
			Process processItem, ListResetSource listResetSource, ResetSignal resetSignal) {

		Element processElement = document.createElement(NodeType.PROCESS.toString());
		racine.appendChild(processElement);

		Element fileNameElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
		fileNameElement.setTextContent(hdlFile.getLocalPath());
		processElement.appendChild(fileNameElement);

		Element entityNameElement = document.createElement(NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
		entityNameElement.setTextContent(hdlEntityItem.getEntity().getId());
		processElement.appendChild(entityNameElement);

		Element archiNameElement = document.createElement(NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString());
		archiNameElement.setTextContent(hdlArchitectureItem.getArchitecture().getId());
		processElement.appendChild(archiNameElement);

		Element processNameElement = document.createElement(NodeType.PROCESS.toString()+NodeInfo.NAME.toString());
		processNameElement.setTextContent(processItem.getLabel());
		processElement.appendChild(processNameElement);

		Element processLocElement = document.createElement(NodeType.PROCESS.toString()+NodeInfo.LOCATION.toString());
		processLocElement.setTextContent(String.valueOf(processItem.getLocation().fLine));
		processElement.appendChild(processLocElement);

		Element resetSignalNameElement = document.createElement(NodeType.RESET_SIGNAL.toString()+NodeInfo.NAME.toString());
		resetSignalNameElement.setTextContent(resetSignal.toString());
		processElement.appendChild(resetSignalNameElement);

		Element resetSignalLocElement = document.createElement(NodeType.RESET_SIGNAL.toString()+NodeInfo.LOCATION.toString());
		resetSignalLocElement.setTextContent(String.valueOf(resetSignal.getLocation().fLine));
		processElement.appendChild(resetSignalLocElement);

		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			LevelE level = LevelE.NAN;
			if (resetSource.equals(resetSignal.getResetSource())) {
				level = update(level, resetSignal.getLevel());
			}
			Element clockSourceElement = document.createElement(resetSource.getTag());
			clockSourceElement.setTextContent(resetSource.toString());
			processElement.appendChild(clockSourceElement);

			Element clockSourceEdgeElement = document.createElement(resetSource.getTag()+NodeInfo.LEVEL);
			clockSourceEdgeElement.setTextContent(level.toString());
			processElement.appendChild(clockSourceEdgeElement);

		}
		
	}




}
