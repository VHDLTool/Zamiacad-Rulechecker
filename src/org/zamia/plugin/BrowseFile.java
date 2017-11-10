/* 
 * Copyright 2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jan 18, 2009
 */
package org.zamia.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.zamia.BuildPath;
import org.zamia.DMManager;
import org.zamia.DesignModuleStub;
import org.zamia.ERManager;
import org.zamia.ExceptionLogger;
import org.zamia.SourceLocation;
import org.zamia.Toplevel;
import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
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

public class BrowseFile implements IWorkbenchWindowActionDelegate {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	private IWorkbenchWindow fWindow;


	private int incr =0;

	private FileWriter  fichier;

	private boolean log = true;

	private boolean logFile = true;

	private ERManager erm;


	/**
	 * prints the VHDLNode and its children recursively in the log
	 * @param node
	 * @throws IOException 
	 */

	
	public void run(IAction action) {


		try {

			
			File ff=new File("C:/resultat.txt"); // définir l'arborescence
			ff.createNewFile();
			fichier=new FileWriter(ff);


			// get zamia project
			ZamiaProject zPrj = ZamiaPlugin.findCurrentProject();
			if (zPrj == null) {
				ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
				return;
			}

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
								write("top : "+ architecture.toString() +  " class: " + architecture.getClass().getName());
								write("sourcefile "+architecture.getSourceFile().getFileName()+" AbsolutePath "+architecture.getSourceFile().getAbsolutePath());
								write("   LocalPath "+architecture.getSourceFile().getLocalPath());
							}
						} catch (ZamiaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

			// update markers (for marker creation test )
			ZamiaErrorObserver.updateAllMarkers(zPrj);
			fichier.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		}

	}

	private void write(String string) throws IOException {
		if (log ) {
			logger.info(string);
		}
		if (logFile) {
			fichier.write (string);
			fichier.write ("\n");
		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow aWindow) {
		fWindow = aWindow;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
