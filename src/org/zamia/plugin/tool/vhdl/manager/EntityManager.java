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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.eclipse.jface.action.IAction;
import org.zamia.DMManager;
import org.zamia.DesignModuleStub;
import org.zamia.ZamiaException;
import org.zamia.ZamiaProject;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.vhdl.ast.DMUID.LUType;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.VHDLPackage;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class EntityManager extends ToolManager {

	private static boolean log = true;

	private static boolean logFile = true;

	private static ListUpdateE info;

	/**
	 * method is called by vhdl tool pull down menu foir debug
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
			getEntity();
			
//			dumpXml(listHdlFile, "REQ_ENTITY", "entity");
		} catch (EntityException e) {
			logger.error("some exception message EntityManager getEntity", e);
		}
		
		
		close();
	}


/**
 * Search process in architecture
 * @param node
 * @return
 */
	public static ArrayList<Entity> getArrayListEntity() {
		ArrayList<Entity> listEntity = new ArrayList<Entity>();
		// get zamia project
		ZamiaProject zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return listEntity;
		}
		
		
		init(zPrj);
		

		if (!getFileConfig(zPrj)) {return listEntity;}

		DMManager dum = zPrj.getDUM();

		int m = dum.getNumStubs();
		Entity entity;

		for (int j = 0; j < m; j++) {
			DesignModuleStub stub = dum.getStub(j);

			if (stub.getDUUID().getLibId().equalsIgnoreCase("WORK")) { // pour prendre les fichiers de travail
				if (stub.getDUUID().getType() == LUType.Entity ) {
					try {
						entity = ((Entity)zPrj.getDUM().getDM(stub.getDUUID()));
						
						if (entity==null){
							write("ENTITY NULL.");
						}else{
							
							String fileName = entity.getSourceFile().getFile().getName();
							String filePath = "\\"+entity.getSourceFile().getLocalPath().replace(fileName, "");
							String filePathName = "\\"+entity.getSourceFile().getLocalPath();
							List<String> listFilePath = createListFilePath(filePath);
														
							if (listFileToWork.contains(filePathName) || !listFilePath.isEmpty()) {
								listEntity.add(entity);
							}
						}
					} catch (ZamiaException e) {
						logger.error("some exception message EntityManager getArrayListEntity", e);
					}
				}
			}
		}
		
		return listEntity;

	}

	
	public static Map<String, HdlFile> getEntity() throws EntityException {
		info = updateInfo(info);

		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}
		HdlFileManager.getHdlFile();

		info = ListUpdateE.YES;

		// clear old entity
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			hdlFile.clearEntity();
		}
		
		// end clear old entity
		
		DMManager dum = zPrj.getDUM();

		int m = dum.getNumStubs();

		Entity entity;
		int cmpt = 0;
		
		for (int j = 0; j < m; j++) {
			DesignModuleStub stub = dum.getStub(j);

			if (stub.getDUUID().getLibId().equalsIgnoreCase("WORK")) { // pour prendre les fichiers de travail
				if (stub.getDUUID().getType() == LUType.Entity ) {
					try {
						entity = ((Entity)zPrj.getDUM().getDM(stub.getDUUID()));
						if (entity != null) {
							String localPath = "\\"+entity.getSource().getLocalPath();
							HdlFile hdlFile = listHdlFile.get(localPath);
							if (hdlFile != null) {
								cmpt++;
								hdlFile.addHdlEntityElement(new HdlEntity(entity, localPath));
								listHdlFile.replace(localPath, hdlFile);
							}
						}
					} catch (ZamiaException e) {
						logger.error("some exception message EntityManager getEntity LUType.Entity", e);
					}
				} else {
					if (stub.getDUUID().getType() == LUType.Package ) {
						try {
							VHDLPackage vhdlPackage = ((VHDLPackage)zPrj.getDUM().getDM(stub.getDUUID()));
							if (vhdlPackage != null) {
								String localPath = "\\"+vhdlPackage.getSource().getLocalPath();
								HdlFile hdlFile = listHdlFile.get(localPath);
								if (hdlFile != null) {
									System.out.println("package "+localPath);
									cmpt++;
									hdlFile.addHdlPackageElement(vhdlPackage);
									listHdlFile.replace(localPath, hdlFile);
								}
							}
						} catch (ZamiaException e) {
							logger.error("some exception message EntityManager getEntity LUType.Package", e);
						}
					}
				}
			}
		}
		
		if (cmpt == 0) {
			JOptionPane.showMessageDialog(null, "<html>A full project build is requested to use Rule Checker</html>", "Error",
                    JOptionPane.ERROR_MESSAGE);

		}
		return listHdlFile;
		
	}


	public static void resetInfo() {
		info = ListUpdateE.NO;
	}

}
