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
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_AR_7 extends ToolSelectorManager {

	// Clock Mix Edges
	
	ToolE tool = ToolE.REQ_FEAT_AR7;

	private Element racineFirst;
	private Element racineSecond;
	private Element racineThird;

	ListResetSource listResetSource;
	Map<String, HdlFile> listHdlFile;

	
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String toolId, ParameterSource parameterSource) {
		String fileName = "";
		
		racineFirst = null;
		racineSecond = null;
		racineThird = null;
		
		try {
			// Retrieve data
			listHdlFile = ResetSignalManager.getResetSignal();
			listResetSource = ResetSignalSourceManager.getResetSourceSignal();
		} catch (EntityException e) {
			logger.error("some exception message Tool_AR_7", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}
		
		try 
		{
			// Dump report file 1
			fileName = dumpXml(tool, parameterSource, NumberReportE.FIRST);
		} catch (Exception e) {
			logger.error("some exception message Tool_AR_7", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		try 
		{
			// Dump report file 2
			fileName = dumpXml(tool, parameterSource, NumberReportE.SECOND);
		} catch (Exception e) {
			logger.error("some exception message Tool_AR_7", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		try 
		{
			// Dump report file 3
			fileName = dumpXml(tool, parameterSource, NumberReportE.THIRD);
		} catch (Exception e) {
			logger.error("some exception message Tool_AR_7", e);
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
					for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
						if (hdlEntityItem.getListHdlArchitecture() != null) {
							for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
								if (hdlArchitectureItem.getListProcess() != null) {
									for (Process processItem : hdlArchitectureItem.getListProcess()) {
										if (processItem.hasSynchronousReset()) {
											for (ClockSignal clockSignal : processItem.getListClockSignal()) {
												for (ResetSignal resetSignal : clockSignal.getListResetSignal()) {
													addReport(hdlFile, hdlEntityItem, hdlArchitectureItem, processItem, listResetSource, resetSignal);
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
		else if (racineSecond == null)
		{
			racineSecond = racine;
			
			for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				if (hdlFile.getListHdlEntity() != null) {
					for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
						addReport(hdlFile, hdlEntityItem, listResetSource);
					}
				}
			}
		}
		else
		{
			racineThird = racine;

			for (ResetSource resetSource : listResetSource.getListResetSource()) {
				LevelE level = LevelE.NAN;
				for (ResetSignal resetSignal : resetSource.getListResetSignal()) {
					if (resetSource.equals(resetSignal.getResetSource())) {
						level = update(level, resetSignal.getLevel());
					}
				}

				Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T3");

				Element resetSourceLevelElement = createResetSourceLevelInfoTypeElement(resetSource, level);
				logEntry.appendChild(resetSourceLevelElement);
				
				racineThird.appendChild(logEntry);
			}
		}
	}
	
	private void addReport(HdlFile hdlFile,
			HdlEntity hdlEntityItem, ListResetSource listResetSource) {

		for (ResetSource resetSource : listResetSource.getListResetSource()) 
		{
			Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T2");

			Element fileNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.FILE.toString()+NodeInfo.NAME.toString());
			fileNameElement.setTextContent(hdlFile.getLocalPath());
			logEntry.appendChild(fileNameElement);

			Element entityNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
			entityNameElement.setTextContent(hdlEntityItem.getEntity().getId());
			logEntry.appendChild(entityNameElement);
			
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

			Element resetSourceLevelElement = createResetSourceLevelInfoTypeElement(resetSource, level);
			logEntry.appendChild(resetSourceLevelElement);

			racineSecond.appendChild(logEntry);
		}
	
	}

	private void addReport(HdlFile hdlFile,
			HdlEntity hdlEntityItem, HdlArchitecture hdlArchitectureItem,
			Process processItem, ListResetSource listResetSource, ResetSignal resetSignal) {

		for (ResetSource resetSource : listResetSource.getListResetSource()) 
		{
			Element logEntry = document.createElement(NAMESPACE_PREFIX + tool.getIdReq() + "_T1");

			Element entityArchitecture = createEntityArchitectureTypeElement(hdlFile.getLocalPath(), hdlEntityItem.getEntity().getId(), hdlArchitectureItem.getArchitecture().getId());
			logEntry.appendChild(entityArchitecture);

			Element processSignal = createProcessSignalTypeElement(processItem);
			logEntry.appendChild(processSignal);
			
			Element resetSignalNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SIGNAL.toString()+NodeInfo.NAME.toString());
			resetSignalNameElement.setTextContent(resetSignal.toString());
			logEntry.appendChild(resetSignalNameElement);

			Element resetSignalLocElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SIGNAL.toString()+NodeInfo.LOCATION.toString());
			resetSignalLocElement.setTextContent(String.valueOf(resetSignal.getLocation().fLine));
			logEntry.appendChild(resetSignalLocElement);
			
			LevelE level = LevelE.NAN;
			if (resetSource.equals(resetSignal.getResetSource())) 
			{
				level = update(level, resetSignal.getLevel());
			}
			
			Element resetSourceLevelElement = createResetSourceLevelInfoTypeElement(resetSource, level);
			logEntry.appendChild(resetSourceLevelElement);
			
			racineFirst.appendChild(logEntry);

		}
	}

}
