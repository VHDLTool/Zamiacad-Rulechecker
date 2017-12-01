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
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.ResetSourceRead;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class ResetSignalReadManager extends ToolManager {

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
			
			getResetReadSignal();
			
			for (ResetSource resetSource : listResetSource.getListResetSource()) {
				logger.info("########################RESET SOURCE #########################");
				logger.info("RESET SOURCE "+resetSource.toString());

				logger.info("_______________________RESULT ________________________");
				for (ResetSourceRead node : resetSource.getListReadResetSource()) {
					logger.info(node.getRead() + " TYPE "+node.getRead().getClass().getSimpleName()+ " LOCATION "+node.getRead().getLocation());
				}
				logger.info("_______________________VERIF ________________________");
				for (ResetSignal node : resetSource.getListResetSignal()) {
					logger.info(node + " TYPE "+node.getClass().getSimpleName()+ " LOCATION "+node.getLocation());
				}

			}
//			 listResetSource.dump();
				
//			dumpXml(listHdlFile, "REQ_FEAT_FN15");
		} catch (EntityException e) {
			logger.error("some exception message ResetSignalReadManager", e);
		}
		

		close();
	}




	public static ListResetSource getResetReadSignal() throws EntityException{
		info = updateInfo(info);
		
		if (info == ListUpdateE.YES) {
			return listResetSource;
		}
		ResetSignalSourceManager.getResetSourceSignal();

		info = ListUpdateE.YES;
		
		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			resetSource.clearListReadReadSource();
		}
		
		for (ResetSource resetSource : listResetSource.getListResetSource()) {
			String signalName = resetSource.toString();
			HdlFile hdlFile = listHdlFile.get("/"+resetSource.getSignalDeclaration().getLocation().fSF.getLocalPath());
			
			for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
				hdlEntityItem.searchReadSignalSource(resetSource, signalName, 0, 10);
			}
			
		}
		
		return listResetSource;
	}




	public static void resetInfo() {
		info = ListUpdateE.NO;
	}


}
