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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class HdlFileManager extends ToolManager {

	private static boolean log = true;

	private static boolean logFile = true;

	private static ListUpdateE info;


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
			getHdlFile();
//			dumpXml(listHdlFile, "REQ_FEAT_FN20", "Line Counter");
			logger.info("Rule Checker: tool line counter (REQ_FEAT_FN20) has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message HdlFileManager", e);
		}
		
		close();
	}


	/**
	 * Search hdl selected file in project
	 * @param node
	 * @return 
	 * @return
	 */	
	public static Map<String, HdlFile> getHdlFile() throws EntityException {
		info = updateInfo(info, true);
		
		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}
		listHdlFile = new HashMap <String, HdlFile>();
		if (!getFileConfig(zPrj)) {return listHdlFile;}
		
		info = ListUpdateE.YES;
		
		String fichierName = ResourcesPlugin.getWorkspace().getRoot().findMember("/"+ zPrj.getId()).getLocation().toString();
		File projectDirectory = new File(fichierName);
		List<File> listvhdlFile = getVhdlFile(projectDirectory);
		String projetcPathDirectory = zPrj.getBuildPath().getSourceFile().getFile().getParent();
		for (File vhdlFile : listvhdlFile) {
			String filePathName = vhdlFile.getAbsolutePath().replace(projetcPathDirectory, "");
			List<String> listFilePath = createListFilePath(filePathName.replace(vhdlFile.getName(), ""));
			// file in directory or sub directory or explicit file with path
			if (listFileToWork.contains(filePathName) || !listFilePath.isEmpty()) {
				listHdlFile.put(filePathName, new HdlFile(vhdlFile, filePathName));
			}
		}
		return listHdlFile;

	}

	public static List<File> getVhdlFile(File repertoire){ 
		 List<File> listvhdlFile = new ArrayList<File>();
		File[] listefichiers; 

		int i; 
		listefichiers=repertoire.listFiles(); 
		for(i=0;i<listefichiers.length;i++){ 
			if (listefichiers[i].isDirectory()) {
				listvhdlFile.addAll(getVhdlFile(listefichiers[i]));
			} else {
				if (listefichiers[i].getName().endsWith(".vhd") || listefichiers[i].getName().endsWith(".vhdl")) {
					listvhdlFile.add(listefichiers[i]);
				}
			}
		}
		return listvhdlFile; 
	}
	
	public static void resetInfo() {
		info = ListUpdateE.NO;
	}

}
