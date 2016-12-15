/* 
 * Copyright 2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jan 18, 2009
 */
package org.zamia.plugin.tool.vhdl.manager;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.SignalSource;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class ClockSignalSourceManager extends ToolManager {

	private boolean log = true;

	private boolean logFile = true;

	private static ListUpdateE info;

	
	
	/**
	 * method is called by vhdl tool pull down menu
	 */
	public void run(IAction action) {
		init(log, logFile);

		zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return;
		}
		
		init(zPrj);
		
		try {
			
			getClockSourceSignal();
			
			 listClockSource.dump();
				
//			dumpXml(listHdlFile, "REQ_FEAT_FN15");
		} catch (EntityException e) {
			logger.error("some exception message ClockSignalSourceManager", e);
		}
		

		close();
	}


	public static ListClockSource getClockSourceSignal() throws EntityException {
		info = updateInfo(info);

		if (info == ListUpdateE.YES && listClockSource != null) {
			return listClockSource;
		}
				
		
		ClockSignalManager.getClockSignal();
		
		info = ListUpdateE.YES;

		listClockSource = new ListClockSource();
		
		
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					HdlEntity hdlEntity = hdlEntityItem;
					if (hdlEntity.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitectureItem : hdlEntity.getListHdlArchitecture()) {
							HdlArchitecture hdlArchitecture = hdlArchitectureItem;
							if (hdlArchitectureItem.getListProcess() != null) {
								for (Process processItem : hdlArchitectureItem.getListProcess()) {
									if (processItem.getListClockSignal() != null) {
										for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
											List<SignalSource> listSearchSignalOrigin = searchSignalOrigin(clockSignalItem.toString(), hdlEntity, 
													hdlArchitecture, true);
											for (SignalSource signalSource : listSearchSignalOrigin) {
												if (hdlFile.getLocalPath().equalsIgnoreCase("\\VHDL_HANDBOOK\\DFlipFlop.vhd") || // TODO BGT
														hdlFile.getLocalPath().equalsIgnoreCase("\\VHDL_HANDBOOK\\STD_04500_bad.vhd")) {
													System.out
															.println("signalSource  "+signalSource.toString());
												}
												// trouver l'origine de la clock
												if (signalSource != null && signalSource.getSignalDeclaration() != null) {
													
													ClockSource clockSource = new ClockSource(signalSource);
													clockSource.setTag(listClockSource.add(clockSource, clockSignalItem));
													clockSignalItem.setClockSource(clockSource);
												} else {
													System.out.println("NO Clock Source Find for clockSignalItem  "+clockSignalItem.toString()+ " location "+clockSignalItem.getLocation());
//													logger.debug("NO Clock Source Find for clockSignalItem  "+clockSignalItem.toString()+ " location "+clockSignalItem.getLocation());
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

		return listClockSource;

	}


	public static void resetInfo() {
		info = ListUpdateE.NO;
	}


}
