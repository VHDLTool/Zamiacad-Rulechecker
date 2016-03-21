/* 
 * Copyright 2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jan 18, 2009
 */
package org.zamia.plugin.tool.vhdl.manager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.VHDLNode;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class ProcessManager extends ToolManager {

	private static ListUpdateE info;

	private boolean log = false;

	private boolean logFile = true;

	/**
	 * method is called by vhdl tool pull down menu for debug
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
			getProcess();
//			dumpXml(listHdlFile, "REQ_PROCESS", "process");
		} catch (EntityException e) {
			logger.error("some exception message ProcessManager", e);
		}
		

		close();
	}


	/**
	 * Search process in an architecture
	 * @param archi
	 * @return
	 */
		public static ArrayList<Process> getProcess(HdlEntity hdlEntity, HdlArchitecture hdlArchi) {
			ArrayList<Process> listProcess = new ArrayList<>();
			if (hdlArchi==null){
				logger.error("CHILD NULL");
			}else{
				
				VHDLNode child;
				for (int i=0; i<hdlArchi.getArchitecture().getNumChildren(); i++){
					child = hdlArchi.getArchitecture().getChild(i);
					if (child instanceof SequentialProcess) {
						Process process = new Process((SequentialProcess) child, hdlEntity, hdlArchi);
						listProcess.add(process);
//						System.out.println(process);
					}
				}
			}
			return listProcess;

		}

		
	/**
	 * Search all process in vhdl project
	 * @param archi
	 * @return
	 * @throws CloneNotSupportedException 
	 */
		public static Map<String, HdlFile> getProcess() throws EntityException {
			info = updateInfo(info);

			if (info == ListUpdateE.YES && listHdlFile != null) {
				return listHdlFile;
			}
			// search all selected file
				ArchitectureManager.getArchitecture();
			
			info = ListUpdateE.YES;
			
			for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				if (hdlFile.getListHdlEntity() != null) {
					for (HdlEntity hdlEntity : hdlFile.getListHdlEntity()) {
						if (hdlEntity.getListHdlArchitecture() != null) {
							for (HdlArchitecture hdlArchitecture : hdlEntity.getListHdlArchitecture()) {
								// in each architecture, search the process
								hdlArchitecture.setListProcess(getProcess(hdlEntity, hdlArchitecture));
							}
						}
					}
				}
			}
				
			return listHdlFile;
		}


		public static void resetInfo() {
			info = ListUpdateE.NO;
		}

}
