package org.zamia.plugin.tool.vhdl.tools.impl;

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
	
	private Element racineFirst;
	private Element racineSecond;
	private Element racineThird;

	private ListClockSource listClockSource;
	private Map<String, HdlFile> listHdlFile;
	
	ToolE tool = ToolE.REQ_FEAT_AR6;

	public Pair<Integer, String> Launch(ZamiaProject zPrj, String toolId, ParameterSource parameterSource) {
		String fileName = "";
		
		racineFirst = null;
		racineSecond = null;
		racineThird = null;
		
		try 
		{
			// Retrieve data
			listHdlFile = ClockSignalManager.getClockSignal();
			listClockSource = ClockSignalSourceManager.getClockSourceSignal();
		} 
		catch (EntityException e) 
		{
			logger.error("some exception message Tool_AR_6", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		try 
		{
			// Dump report file 1
			fileName = dumpXml(tool, parameterSource, NumberReportE.FIRST);
		} catch (Exception e) {
			logger.error("some exception message Tool_AR_6", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		try 
		{
			// Dump report file 2
			fileName = dumpXml(tool, parameterSource, NumberReportE.SECOND);
		} catch (Exception e) {
			logger.error("some exception message Tool_AR_6", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		try 
		{
			// Dump report file 3
			fileName = dumpXml(tool, parameterSource, NumberReportE.THIRD);
		} catch (Exception e) {
			logger.error("some exception message Tool_AR_6", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}
	
	protected void addLogContent(Element racine, ParameterSource parameterSource) throws Exception
	{
		if (racineFirst == null)
		{
			racineFirst = racine;
			
			for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				if (hdlFile.getListHdlEntity() != null) {
					for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) 
					{
						if (hdlEntityItem.getListHdlArchitecture() != null) {
							for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
								if (hdlArchitectureItem.getListProcess() != null) {
									for (Process processItem : hdlArchitectureItem.getListProcess()) {
										if (processItem.isSynchronous()) {
											for (ClockSignal clockSignal : processItem.getListClockSignal()) 
											{
												addReport(hdlFile, hdlEntityItem, hdlArchitectureItem,
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
		else if (racineSecond == null)
		{
			racineSecond = racine;
			
			for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				if (hdlFile.getListHdlEntity() != null) {
					for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
						addReport(hdlFile, hdlEntityItem, listClockSource);
					}
				}
			}
		}
		else
		{
			racineThird = racine;

			for (ClockSource clockSource : listClockSource.getListClockSource()) {
				EdgeE edge = EdgeE.NAN;
				for (ClockSignal clockSignal : clockSource.getListClockSignal()) {
					if (clockSource.equals(clockSignal.getClockSource())) {
						edge = update(edge, clockSignal.getEdge());
					}
				}
				
				Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T3");

				Element clockSourceEdgeElement = createClockSourceEdgeInfoTypeElement(clockSource, edge);
				logEntry.appendChild(clockSourceEdgeElement);
				
				racineThird.appendChild(logEntry);
			}

		}
	}
	
	
	private void addReport(HdlFile hdlFile,
			HdlEntity hdlEntityItem, ListClockSource listClockSource) {

		for (ClockSource clockSource : listClockSource.getListClockSource()) 
		{
			Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T2");

			Element fileNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.FILE.toString()+NodeInfo.NAME.toString());
			fileNameElement.setTextContent("." + hdlFile.getLocalPath());
			logEntry.appendChild(fileNameElement);

			Element entityNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
			entityNameElement.setTextContent(hdlEntityItem.getEntity().getId());
			logEntry.appendChild(entityNameElement);
			
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
			
			Element clockSourceEdgeElement = createClockSourceEdgeInfoTypeElement(clockSource, edge);
			logEntry.appendChild(clockSourceEdgeElement);
			
			racineSecond.appendChild(logEntry);
		}
		
	
	}

	private void addReport(HdlFile hdlFile,
			HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem,
			Process processItem, ListClockSource listClockSource, ClockSignal clockSignal) {

		for (ClockSource clockSource : listClockSource.getListClockSource()) 
		{
			Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T1");

			Element entityArchitecture = createEntityArchitectureTypeElement(hdlFile.getLocalPath(), hdlEntityItem.getEntity().getId(), hdlArchitectureItem.getArchitecture().getId());
			logEntry.appendChild(entityArchitecture);

			Element processSignal = createProcessSignalTypeElement(processItem);
			logEntry.appendChild(processSignal);

			Element clockNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SIGNAL.toString()+NodeInfo.NAME.toString());
			clockNameElement.setTextContent(clockSignal.toString());
			logEntry.appendChild(clockNameElement);

			Element clockLocElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SIGNAL.toString()+NodeInfo.LOCATION.toString());
			clockLocElement.setTextContent(String.valueOf(clockSignal.getLocation().fLine));
			logEntry.appendChild(clockLocElement);
			
			EdgeE edge = EdgeE.NAN;
			if (clockSource.equals(clockSignal.getClockSource())) 
			{
				edge = clockSignal.getEdge();
			}

			Element clockSourceEdgeElement = createClockSourceEdgeInfoTypeElement(clockSource, edge);
			logEntry.appendChild(clockSourceEdgeElement);
			
			racineFirst.appendChild(logEntry);
		}
		
	}
}
