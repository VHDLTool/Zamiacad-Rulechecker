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
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.SignalSource;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class ResetSignalSourceManager extends ToolManager {

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
			
			getResetSourceSignal();
			
			 listResetSource.dump();
				
//			dumpXml(listHdlFile, "REQ_FEAT_FN15");
		} catch (EntityException e) {
			logger.error("some exception message ResetSignalSourceManager", e);
		}
		

		close();
	}


	public static ListResetSource getResetSourceSignal() throws EntityException {
		info = updateInfo(info);
		
		if (info == ListUpdateE.YES) {
			return listResetSource;
		}
		listResetSource = new ListResetSource();
		
		ResetSignalManager.getResetSignal();
		
		info = ListUpdateE.YES;
		
		
		
		
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
		    // traitements
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
											if (clockSignalItem.getListResetSignal() != null) {
												for (ResetSignal resetSignalItem : clockSignalItem.getListResetSignal()) {
													List<SignalSource> listSearchSignalOrigin = searchSignalOrigin(resetSignalItem.toString(), hdlEntity, 
															hdlArchitecture, true);
													// trouver l'origine du reset
													for (SignalSource signalSource : listSearchSignalOrigin) {
														
														if (signalSource != null) {
															ResetSource resetSource = new ResetSource(signalSource);
															resetSource.setTag(listResetSource.add(resetSource, resetSignalItem));
															resetSignalItem.setResetSource(resetSource);
														} else {
//															logger.debug("NO RESET SOURCE resetSignalItem "+resetSignalItem.toString()+ " LOC "+resetSignalItem.getLocation());
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

		}

		return listResetSource;

	}


	public static void resetInfo() {
		info = ListUpdateE.NO;
	}

}
