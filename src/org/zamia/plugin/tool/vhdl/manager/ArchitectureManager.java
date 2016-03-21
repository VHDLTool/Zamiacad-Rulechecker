/* 
 * Copyright 2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Niesner Christophe on June, 2015
 * 
 * 
 */
package org.zamia.plugin.tool.vhdl.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.zamia.DMManager;
import org.zamia.DesignModuleStub;
import org.zamia.ZamiaException;
import org.zamia.ZamiaProject;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.DMUID.LUType;
import org.zamia.vhdl.ast.VHDLPackage;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class ArchitectureManager extends ToolManager {

	private static boolean log = true;

	private static boolean logFile = true;

	private static ListUpdateE info;

	/**
	 * method is called by vhdl tool pull down menu for debug for debug
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
			
			getArchitecture();
//			dumpXml(listHdlFile, "REQ_ARCHI", "archi");
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			logger.error("some exception message ArchitectureManager run", e);
		}
		
	
		close();
	}


/**
 * Search process in architecture
 * @param node
 * @return
 */
	public static ArrayList<Architecture> getArrayListArchitecture() {
		ArrayList<Architecture> listArchi = new ArrayList<Architecture>();
		// get zamia project
		ZamiaProject zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return listArchi;
		}
		
		
		init(zPrj);
		

		if (!getFileConfig(zPrj)) {return listArchi;}

		
		DMManager dum = zPrj.getDUM();

		int m = dum.getNumStubs();
		Architecture architecture;

		for (int j = 0; j < m; j++) {
			DesignModuleStub stub = dum.getStub(j);

			if (stub.getDUUID().getLibId().equalsIgnoreCase("WORK")) { // pour prendre les fichiers de travail
				System.out.println("stub.getDUUID().getType() "+stub.getDUUID().getType());
				if (stub.getDUUID().getType() == LUType.Architecture) {
					try {
						architecture = ((Architecture)zPrj.getDUM().getDM(stub.getDUUID()));
						
						if (architecture==null){
							write("ARCHITECTURE NULL.");
						}else{
							
							String fileName = architecture.getSourceFile().getFile().getName();
							String filePath = "\\"+architecture.getSourceFile().getLocalPath().replace(fileName, "");
							String filePathName = "\\"+architecture.getSourceFile().getLocalPath();
							List<String> listFilePath = createListFilePath(filePath);

							if (listFileToWork.contains(filePathName) || !listFilePath.isEmpty()) {
								listArchi.add(architecture);
							}
						}
					} catch (ZamiaException e) {
						logger.error("some exception message getArrayListArchitecture", e);
					}
				} else if (stub.getDUUID().getType() == LUType.Package) {
					try {
						VHDLPackage packageBody = ((VHDLPackage)zPrj.getDUM().getDM(stub.getDUUID()));
						
						if (packageBody==null){
							write("PACKAGE NULL.");
						}else{
							
							String fileName = packageBody.getSourceFile().getFile().getName();
							String filePath = "\\"+packageBody.getSourceFile().getLocalPath().replace(fileName, "");
							String filePathName = "\\"+packageBody.getSourceFile().getLocalPath();
							List<String> listFilePath = createListFilePath(filePath);

							if (listFileToWork.contains(filePathName) || !listFilePath.isEmpty()) {
								System.out.println(fileName);
								System.out.println("NB lib "+packageBody.getContext().getNumLibraries());
								if (packageBody.getContext().getNumLibraries() != 0) {System.out.println(packageBody.getContext().getLibrary(0));}
								System.out.println("NB use "+packageBody.getContext().getNumUses());
								if (packageBody.getContext().getNumUses() != 0) {System.out.println(packageBody.getContext().getUse(0));}
//								listArchi.add(packageBody);
							}
						}
					} catch (ZamiaException e) {
						logger.error("some exception message getArrayListArchitecture", e);
					}
				}
			}
		}
		
		return listArchi;

	}


	public static Map<String, HdlFile> getArchitecture() throws EntityException {
		info = updateInfo(info);
		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}
		EntityManager.getEntity();
		
		info = ListUpdateE.YES;
		// clear old architecture
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					hdlEntityItem.setArchitecture(zPrj);
				}
			}
		}		

		return listHdlFile;
	}


	public static void resetInfo() {
		info = ListUpdateE.NO;
	}


}
