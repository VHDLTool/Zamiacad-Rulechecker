/* 
 * Copyright 2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jan 18, 2009
 */
package org.zamia.plugin.tool.vhdl.manager;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.ClockSourceRead;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.ListUpdateE;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class ClockSignalReadManager extends ToolManager {

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
			
			getClockReadSignal();
	
			for (ClockSource clockSource : listClockSource.getListClockSource()) {
				logger.info("########################CLOCK SOURCE #########################");
				logger.info("CLOCK SOURCE "+clockSource.toString());
				logger.info("_______________________RESULT ________________________");
				for (ClockSourceRead node : clockSource.getListReadClockSource()) {
					logger.info(node.getRead() + " TYPE "+node.getRead().getClass().getSimpleName()+ " LOCATION "+node.getRead().getLocation());
				}
				logger.info("_______________________VERIF ________________________");
				for (ClockSignal node : clockSource.getListClockSignal()) {
					logger.info(node + " TYPE "+node.getClass().getSimpleName()+ " LOCATION "+node.getLocation());
				}
			}
//			 listClockSource.dump();
				
//			dumpXml(listHdlFile, "REQ_FEAT_FN15");
		} catch (EntityException e) {
			logger.error("some exception message ClockSignalReadManager", e);
		}
		

		close();
	}


	public static void resetClockReadSignal() {
		System.out.println("resetClockReadSignal");
		listClockSource = null;
	}
		public static ListClockSource getClockReadSignal() throws EntityException {
			info = updateInfo(info);
		
		if (info == ListUpdateE.YES && listClockSource != null) {
			return listClockSource;
		}
		
		ClockSignalSourceManager.getClockSourceSignal();
		
		info = ListUpdateE.YES;

		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			clockSource.clearListReadClockSource();
		}

		for (ClockSource clockSource : listClockSource.getListClockSource()) {
			String signalName = clockSource.toString();
			HdlFile hdlFile = listHdlFile.get("/"+clockSource.getSignalDeclaration().getLocation().fSF.getLocalPath());
			for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
				hdlEntityItem.searchReadSignalSource(clockSource, signalName, 0, 10);
			}
			
		}
		
		return listClockSource;

	}


		public static void resetInfo() {
			info = ListUpdateE.NO;
		}


}
