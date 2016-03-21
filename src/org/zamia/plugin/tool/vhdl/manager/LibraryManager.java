/* 
 * Copyright 2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jan 18, 2009
 */
package org.zamia.plugin.tool.vhdl.manager;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.vhdl.ast.VHDLPackage;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class LibraryManager extends ToolManager {

	private static boolean log = true;

	private static boolean logFile = true;


	/**
	 * method is called by vhdl tool pull down menu
	 */
	public void run(IAction action) {
		
		init(log, logFile);

		// get zamia project
		zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return;
		}
		
		init(zPrj);
		

		try {
			EntityManager.getEntity();
			getLibrary();
			
//			dumpXml(listHdlFile, "REQ_FEAT_FN19", "Package Library Identification");
			logger.info("Rule Checker: tool package/library identification (REQ_FEAT_FN19) has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message LibraryManager", e);
		}
		
		close();
	}

	/**
	 * search all libraries used in vhdl file
	 * @param hdlFiles
	 * @return
	 */
	public static Map<String, HdlFile> getLibrary() {
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			
			if (hdlFile.getListHdlEntity() != null && !hdlFile.getListHdlEntity().isEmpty()) { 
				hdlFile.clearLibrary();
				for (HdlEntity entity : hdlFile.getListHdlEntity()) {
					for (int i = 0; i < entity.getEntity().getContext().getNumUses(); i++) {
						hdlFile.addLibrary(entity.getEntity().getContext().getUse(i));
					}
				}
			}
			if (hdlFile.getListHdlPackage() == null) { continue;}
			if (hdlFile.getListHdlPackage().isEmpty()) { continue;}

			for (VHDLPackage vhdlPackage : hdlFile.getListHdlPackage()) {
				for (int i = 0; i < vhdlPackage.getContext().getNumUses(); i++) {
					hdlFile.addLibrary(vhdlPackage.getContext().getUse(i));
				}
			}
		}
		
		return listHdlFile;
		
	}


}
