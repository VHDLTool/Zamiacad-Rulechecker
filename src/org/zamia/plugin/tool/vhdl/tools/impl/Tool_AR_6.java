package org.zamia.plugin.tool.vhdl.tools.impl;

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
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_AR_6 extends ToolSelectorManager {

	// Clock Mix Edges
	
	ToolE tool = ToolE.REQ_FEAT_AR6;

	public Pair<Integer, String> Launch(ZamiaProject zPrj, String toolId) {
		String fileName = "";
		
		ListClockSource listClockSource;
		Map<String, HdlFile> listHdlFile;
		
		try {
			listHdlFile = ClockSignalManager.getClockSignal();
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
		} catch (EntityException e) {
			logger.error("some exception message Tool_AR_6", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}
		
		Element racineFirst1 = initReportFile(toolId, tool.getType(), tool.getRuleName(), NumberReportE.FIRST);
		Element racineSecond = initReportFile(toolId, tool.getType(), tool.getRuleName(), NumberReportE.SECOND);
		Element racineThird1 = initReportFile(toolId, tool.getType(), tool.getRuleName(), NumberReportE.THIRD);

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					addReport(documentSecond, racineSecond, hdlFile, hdlEntityItem, listClockSource);
					if (hdlEntityItem.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
							if (hdlArchitectureItem.getListProcess() != null) {
								for (Process processItem : hdlArchitectureItem.getListProcess()) {
									if (processItem.isSynchronous()) {
										for (ClockSignal clockSignal : processItem.getListClockSignal()) {
											addReport(document, racineFirst1, hdlFile, hdlEntityItem, hdlArchitectureItem,
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
		
		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			EdgeE edge = EdgeE.NAN;
			for (ClockSignal clockSignal : clockSource.getListClockSignal()) {
				if (clockSource.equals(clockSignal.getClockSource())) {
					edge = update(edge, clockSignal.getEdge());
				}
			}
			addReport(documentThird, racineThird1, clockSource, edge);
		}


		fileName = createReportFile(toolId, tool.getRuleName(), tool.getType(), "tool", NumberReportE.FIRST);
		fileName = createReportFile(toolId, tool.getRuleName(), tool.getType(), "tool", NumberReportE.SECOND);
		fileName = createReportFile(toolId, tool.getRuleName(), tool.getType(), "tool", NumberReportE.THIRD);

		return new Pair<Integer, String> (0, fileName);
	}
	
	
	private void addReport(Document document, Element racineSecond, HdlFile hdlFile,
			HdlEntity hdlEntityItem, ListClockSource listClockSource) {

		Element processElement = document.createElement(NodeType.ENTITY.toString());
		racineSecond.appendChild(processElement);

		Element fileNameElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
		fileNameElement.setTextContent(hdlFile.getLocalPath());
		processElement.appendChild(fileNameElement);

		Element entityNameElement = document.createElement(NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
		entityNameElement.setTextContent(hdlEntityItem.getEntity().getId());
		processElement.appendChild(entityNameElement);


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
			Element clockSourceElement = document.createElement(clockSource.getTag());
			clockSourceElement.setTextContent(clockSource.toString());
			processElement.appendChild(clockSourceElement);

			Element clockSourceEdgeElement = document.createElement(clockSource.getTag()+NodeInfo.EDGE);
			clockSourceEdgeElement.setTextContent(edge.toString());
			processElement.appendChild(clockSourceEdgeElement);

		}
		
	
	}


	private void addReport(Document document, Element racineThird, ClockSource clockSource,
			EdgeE edge) {
		Element processElement = document.createElement(NodeType.CLOCK_SOURCE.toString());
		racineThird.appendChild(processElement);
		
		Element clockSourceElement = document.createElement(clockSource.getTag());
		clockSourceElement.setTextContent(clockSource.toString());
		processElement.appendChild(clockSourceElement);

		Element clockSourceEdgeElement = document.createElement(clockSource.getTag()+NodeInfo.EDGE);
		clockSourceEdgeElement.setTextContent(edge.toString());
		processElement.appendChild(clockSourceEdgeElement);

	}


	private void addReport(Document document, Element racine, HdlFile hdlFile,
			HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem,
			Process processItem, ListClockSource listClockSource, ClockSignal clockSignal) {

		Element processElement = document.createElement(NodeType.CLOCK_SIGNAL.toString());
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

		Element clockNameElement = document.createElement(NodeType.CLOCK_SIGNAL.toString()+NodeInfo.NAME.toString());
		clockNameElement.setTextContent(clockSignal.toString());
		processElement.appendChild(clockNameElement);

		Element clockLocElement = document.createElement(NodeType.CLOCK_SIGNAL.toString()+NodeInfo.LOCATION.toString());
		clockLocElement.setTextContent(String.valueOf(clockSignal.getLocation().fLine));
		processElement.appendChild(clockLocElement);

		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			EdgeE edge = EdgeE.NAN;
			if (clockSource.equals(clockSignal.getClockSource())) {
				edge = clockSignal.getEdge();
			}
			
			Element clockSourceElement = document.createElement(clockSource.getTag());
			clockSourceElement.setTextContent(clockSource.toString());
			processElement.appendChild(clockSourceElement);

			Element clockSourceEdgeElement = document.createElement(clockSource.getTag()+NodeInfo.EDGE);
			clockSourceEdgeElement.setTextContent(edge.toString());
			processElement.appendChild(clockSourceEdgeElement);

		}
		
	}




}
