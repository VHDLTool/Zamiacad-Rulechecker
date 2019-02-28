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

import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.eclipse.jface.action.IAction;
import org.zamia.DMManager;
import org.zamia.DesignModuleStub;
import org.zamia.ZamiaException;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.vhdl.ast.PackageBody;
import org.zamia.vhdl.ast.DMUID.LUType;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class PackageBodyManager extends ToolManager {

	private static boolean log = true;

	private static boolean logFile = true;

	private static ListUpdateE info;


	public static Map<String, HdlFile> getPackageBody() throws EntityException {
		info = updateInfo(info);

		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}
		HdlFileManager.getHdlFile();

		info = ListUpdateE.YES;

		// clear old package body
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			hdlFile.clearPackageBody();
		}
		// end clear old package body
		
		DMManager dum = zPrj.getDUM();
		int m = dum.getNumStubs();

		PackageBody packageBody;
		int cmpt = 0;
		
		for (int j = 0; j < m; j++) {
			DesignModuleStub stub = dum.getStub(j);
			if (stub.getDUUID().getLibId().equalsIgnoreCase("WORK")) {
				if (stub.getDUUID().getType() == LUType.PackageBody ) {
					try {
						packageBody = ((PackageBody)zPrj.getDUM().getDM(stub.getDUUID()));
						if (packageBody != null) {
							String localPath = "/" + packageBody.getSource().getLocalPath();
							HdlFile hdlFile = listHdlFile.get(localPath);
							if (hdlFile != null) {
								cmpt++;
								hdlFile.addPackageBody(packageBody);
								listHdlFile.replace(localPath, hdlFile);
							}
						}
					} catch (ZamiaException e) {
						logger.error("some exception message PackageBodyManager getpackgeBody LUType.PackageBody", e);
					}
				}
			}
		}
		return listHdlFile;
	}

	public static void resetInfo() {
		info = ListUpdateE.NO;
	}

	@Override
	public void run(IAction arg0) {
		init(log, logFile);

		// get zamia project
		zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return;
		}
		init(zPrj);
		try {
			getPackageBody();
//			dumpXml(listHdlFile, "REQ_ENTITY", "entity");
		} catch (EntityException e) {
			logger.error("some exception message EntityManager getEntity", e);
		}
		close();
	}


}
