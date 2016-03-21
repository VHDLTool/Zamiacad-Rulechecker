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
import org.zamia.ERManager;
import org.zamia.ExceptionLogger;
import org.zamia.Toplevel;
import org.zamia.ZamiaException;
import org.zamia.ZamiaException.ExCat;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.instgraph.IGItem;
import org.zamia.instgraph.IGModule;
import org.zamia.instgraph.IGObject;
import org.zamia.plugin.build.ZamiaErrorObserver;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.VHDLNode;





/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class AuditAction implements IWorkbenchWindowActionDelegate {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	private IWorkbenchWindow fWindow;


	private int incr =0;

	private FileWriter fichier;

	private boolean log = true;

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
	private void nodeToLogger(VHDLNode node) throws IOException{
		if (node==null){
			write(getIncrPrefix()+"CHILD NULL");
		}else{
			boolean isInc=false;
			VHDLNode child;
			int numChildren = node.getNumChildren();
			write(getIncrPrefix()+"=================");
			write(
					getIncrPrefix()
					+"node : "
					+ node.toString() 
					+ " ["
					+ node.getClass().getSimpleName()
					+ " ] "
					+ " >> numChildren: "+ numChildren);

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
	/**
	 * prints the IGItem and its children recursively in the log
	 * @param node
	 * @throws IOException 
	 */
	private void nodeIGToLogger(IGItem node) throws IOException{
		if (node==null){
			write(getIncrPrefix()+"CHILD NULL");
		}else{
			boolean isInc=false;
			IGItem child;
			int numChildren = node.getNumChildren();
			write(getIncrPrefix()+"=================");
			write(
					getIncrPrefix()
					+"node : "
					+ node.toString() 
					+ " ["
					+ node.getClass().getSimpleName()
					+ " ] "
					+ " >> numChildren: "+ numChildren);

			for (int i=0; i<node.getNumChildren(); i++){
				if (!isInc){
					incr=incr+3;
					isInc=true;
				}
				child = node.getChild(i);
				nodeIGToLogger(child);
			}
			if (isInc){
				incr=incr-3;
			}
		}

	}
	public void run(IAction action) {
		
		try {


		File ff=new File("C:\\resultat.txt"); // définir l'arborescence
		ff.createNewFile();
		fichier=new FileWriter(ff);




		// get zamia project
		ZamiaProject zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return;
		}

		ERManager erm = zPrj.getERM();
		BuildPath bp = zPrj.getBuildPath();

		// get infos on toplevel
		write("************************************************");
		write("project : "+zPrj.getId());
		write("buildPath file name: "+ bp.getSourceFile().getFileName());
		if (bp.getNumToplevels()==0){
			logger.error("top level not found. Build the project first");
			return;
		}
		Toplevel tl = bp.getToplevel(0);
		write("toplevel : " + tl.getDUUID());
		write("************************************************");

		write("/////////////////////// CLOCKS /////////////////////// ");
		// get clocks
		IGObject clock;
//		for (long fDBID : zPrj.getClockMgr().getClocksList()){
//			clock = (IGObject)zPrj.getZDB().load(fDBID);
//			SourceLocation loc = clock.computeSourceLocation();
////			write(">> "+clock );
//			write(">>  ["+fDBID+"]  "+clock + " in " + loc.fSF + " - line "+loc.fLine+" col. "+ +loc.fCol);
//			
//			try {
////				cf. code from org.zamia.plugin.search.ReferencesSearchQuery.run(IProgressMonitor)
////				org.zamia.analysis.ast.ASTReferencesSearch.search(DeclarativeItem, boolean, boolean)
//				ASTNode nearest = SourceLocation2AST.findNearestASTNode(loc, true, zPrj);
//				DeclarativeItem declaration = ASTDeclarationSearch.search(nearest, zPrj);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ZamiaException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			
//		}
		
		// read the IG model recursively
		write("/////////////////////// IG /////////////////////// ");
		IGModule module = zPrj.getIGM().findModule(tl);
		if (module==null){
			write("module NULL. Check the Top Level in build path");
		}else{
			write("top : "+ module.toString() +  "class: " + module.getClass().getName());
			nodeIGToLogger(module);
			VHDLNode node;
			try {
				node = (VHDLNode) (zPrj.getDUM().getDM(module.getDUUID()));
				// create an exception to test marker creation
				ZamiaException e = new ZamiaException(ExCat.INTERMEDIATE, true, "altran test marker", node);
				// do we need to get the VHDL node to create a marker in the problems view?
				erm.addError(e);
			} catch (ZamiaException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		// read the AST model recursively
		write("/////////////////////// AST /////////////////////// ");
		Entity entity = null;
		
		// entity
//		try {
//			write("================= ENTITY =============================");
//			entity = ((Entity)zPrj.getDUM().getDM(tl.getDUUID()));
//
//			if (entity==null){
//				write("NODE NULL. Check the Top Level in build path");
//			}else{
//				write("top : "+ entity.toString() +  "class: " + entity.getClass().getName());
//				write("sourcefile "+entity.getSourceFile().getFileName()+ "nb lines:"+entity.getSourceFile().getNumLines());
//				nodeToLogger(entity);
//			}
//
//		} catch (ZamiaException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		// architecture (only the first one)
//		try {
//			write("================= ARCH =============================");
//			Architecture arch = zPrj.getDUM().getArchitecture(entity.getLibId(), entity.getId());
//
//			if (arch==null){
//				write("NODE NULL. Check the Top Level in build path");
//			}else{
//				write("top : "+ arch.toString() +  "class: " + arch.getClass().getName());
//				nodeToLogger(arch);
//			}
//
//		} catch (ZamiaException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


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
		if (log  ) {
			logger.info(string);
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
