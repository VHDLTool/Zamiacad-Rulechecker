/* 
 * Copyright 2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jan 18, 2009
 */
package org.zamia.plugin.tool.vhdl.manager;

import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.zamia.BuildPath;
import org.zamia.DMManager;
import org.zamia.DesignModuleStub;
import org.zamia.ERManager;
import org.zamia.ExceptionLogger;
import org.zamia.Toplevel;
import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.build.ZamiaErrorObserver;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.DMUID.LUType;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.VHDLNode;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class UnnamedProcess extends ToolManager {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	private IWorkbenchWindow fWindow;


	private int incr =0;

	private boolean log = true;

	private boolean logFile = true;

	private ERManager erm;

	/**
	 * method is called by vhdl tool pull down menu for debug
	 */
	public void run(IAction action) {

		init(log, logFile);

			// get zamia project
			ZamiaProject zPrj = ZamiaPlugin.findCurrentProject();
			if (zPrj == null) {
				ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
				return;
			}
			
			init(zPrj);
			

			erm = zPrj.getERM();
			BuildPath bp = zPrj.getBuildPath();

			// get infos on toplevel
			write("************************************************");
			write("************************************************");
			write("project : "+zPrj.getId());
			write("buildPath file name: "+ bp.getSourceFile().getFileName());
			if (bp.getNumToplevels()==0){
				logger.error("top level not found. Build the project first");
				return;
			}
			Toplevel tl = bp.getToplevel(0);
			write("source file : " + zPrj.getDataPath());
			write("nb synth TLS : " + bp.getNumSynthTLs());
			write("toplevel : " + tl.getDUUID());
			write("************************************************");


			DMManager dum = zPrj.getDUM();

			int m = dum.getNumStubs();
			Architecture architecture;
			for (int j = 0; j < m; j++) {
				DesignModuleStub stub = dum.getStub(j);

				if (stub.getDUUID().getLibId().equalsIgnoreCase("WORK")) {
					if (stub.getDUUID().getType() == LUType.Architecture) {
						try {
							architecture = ((Architecture)zPrj.getDUM().getDM(stub.getDUUID()));

							if (architecture==null){
								write("NODE NULL. Check the Top Level in build path");
							}else{
								write("top : "+ architecture.toString() +  "class: " + architecture.getClass().getName());
								write("sourcefile "+architecture.getSourceFile().getFileName()+ "nb lines:"+architecture.getSourceFile().getNumLines());
								nodeToLogger(architecture);
							}
						} catch (ZamiaException e) {
							logger.error("some exception message UnnamedProcess", e);
						}
					}
				}
			}

			// update markers (for marker creation test )
			ZamiaErrorObserver.updateAllMarkers(zPrj);
			
			close();

	}

	private String getIncrPrefix()
	{
		String pref="";
		for (int i=0; i<incr ; i++){
			pref=pref+" ";
		}
		return pref;
	}

	/**
	 * prints the VHDLNode and its children recursively in the log
	 * @param node
	 * @throws IOException 
	 */
	private void nodeToLogger(VHDLNode node) {
		if (node==null){
			//			write(getIncrPrefix()+"CHILD NULL");
		}else{
			boolean isInc=false;
			VHDLNode child;
			int numChildren = node.getNumChildren();
			if (node instanceof SequentialProcess) {
				if (((SequentialProcess) node).getLabel() == null) {
					erm.addError(new ZamiaException("unnamed process" , node.getLocation()));
				}
				write(getIncrPrefix()+"=================");

				write(
						getIncrPrefix()
						+"node : "
						+ node.toString() 
						+ " ["
						+ node.getClass().getSimpleName()
						+ " ] "
						+ " >> numChildren: "+ numChildren);
				
				for (int j=0; j<node.getNumChildren(); j++){
					child = node.getChild(j);
					write(
							getIncrPrefix()
							+"child : "
							+ child.toString() 
							+ " ["
							+ child.getClass().getSimpleName()
							+ " ] "
							+ " >> numChildren: "+ numChildren);
				}
			}
			for (int i=0; i<node.getNumChildren(); i++){
				if (!isInc){
					incr=incr+3;
					isInc=true;
				}
				child = node.getChild(i);
				nodeToLogger(child);
			}
			if (isInc){
				incr=incr-3;
			}
		}

	}

	

}
