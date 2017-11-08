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
import org.zamia.ExceptionLogger;
import org.zamia.Toplevel;
import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.plugin.build.ZamiaErrorObserver;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.DMUID.LUType;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationLiteral;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.SequenceOfStatements;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.VHDLNode;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class FindClock implements IWorkbenchWindowActionDelegate {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	private IWorkbenchWindow fWindow;


	private int incr =0;

	private FileWriter  fichier;

	private boolean log = false;

	private boolean logFile = true;

	private Architecture architecture;
	


	/**
	 * prints the VHDLNode and its children recursively in the log
	 * @param node
	 * @throws IOException 
	 */
	private void searchProcess(VHDLNode node) throws IOException{
		if (node==null){
			//			write(getIncrPrefix()+"CHILD NULL");
		}else{
			
			boolean isInc=false;
			VHDLNode child;
			
			for (int i=0; i<node.getNumChildren(); i++){
				if (!isInc){
					incr=incr+3;
					isInc=true;
				}
				child = node.getChild(i);
				if (child instanceof SequentialProcess) {
					write("process "+((SequentialProcess) child).getLabel());
					searchSequence(child);
					
				}
			}
			if (isInc){
				incr=incr-3;
			}
		}

	}

	
	private void searchSequence(VHDLNode node) {
		VHDLNode child;
		for (int i=0; i<node.getNumChildren(); i++){
			child = node.getChild(i);
			if (child instanceof SequenceOfStatements) {
				if (searchClock(child)) {return;}
				if(child!= null) {searchSequence(child);}
			} else if(child!= null) {
				searchSequence(child);
			}
		}
	
	}

	private boolean searchClock(VHDLNode node) {

		write("SequenceOfStatements child "+node.getNumChildren());
		VHDLNode child;
		for (int j=0; j<node.getNumChildren(); j++){
			child = node.getChild(j);
			if (child instanceof SequentialIf) {
				write("SequentialIf child "+child.getNumChildren());
				VHDLNode subChild;
				for (int k=0; k<child.getNumChildren(); k++){
					subChild = child.getChild(k);
					if (subChild instanceof OperationName) {
						write("OperationName : "+subChild.toString());
						getClock(subChild, "Cas1");
					}
					if (subChild instanceof OperationLogic) {
						write("OperationLogic : "+subChild.toString());
						getClock(subChild, "Cas2");
					}
				}

			}
		}

		return false;
	}

	private void getClock(VHDLNode node, String cas) {
		switch (cas) {
		case "Cas1":
			if (node.toString().contains("RISING_EDGE") || node.toString().contains("FALLING_EDGE")) {
				write("CLOCK "+((OperationName)node).getName());
				for (int i = 0; i < ((OperationName)node).getName().getNumExtensions(); i++) {
					write("getExtension  "+((OperationName)node).getName().getExtension(i).getNumChildren());
					VHDLNode child = ((OperationName)node).getName().getExtension(i).getChild(0);
					if (child != null) {
//						Report.report(child, process, architecture);
					}
				}

			}
			break;
		case "Cas2":
			VHDLNode signal = searchSignal(node);
			searchKeyWord(node, signal);
			break;
		default:
			write("wrong Case");
			break;
		}
	}
	
	private VHDLNode searchSignal(VHDLNode node) {
		VHDLNode child;
		for (int j=0; j<node.getNumChildren(); j++){
			child = node.getChild(j);
			if (child instanceof OperationCompare) {
				if (child.getNumChildren() == 2) {
					VHDLNode signal = child.getChild(0);
					if (signal != null && signal instanceof OperationName) {
					VHDLNode value = child.getChild(1);
						if (value != null && value instanceof OperationLiteral) {
							if (value.toString().equals("0") || value.toString().equals("1")) {
								return signal;
							}
						}
					}
				} 
			}
		}
		return null;
	}


/**
 * search key word EVENT, STABLE
 * @param node
 * @param signal 
 */
	private void searchKeyWord(VHDLNode node, VHDLNode signal) {
		if (signal == null) { return;}
		
		
		VHDLNode child;
		for (int j=0; j<node.getNumChildren(); j++){
			child = node.getChild(j);
			if (child instanceof OperationName) {
				if (child.toString().contains(signal.toString())) {
					if (child.toString().contains("EVENT")) {
//						Report.report(signal, process, architecture);
					}
				}
			} else if (child instanceof OperationLogic) {
				searchNot();
				
				VHDLNode subChild_0,subChild_1;
				if (child.getNumChildren() == 2){
					subChild_0 = child.getChild(0);
					subChild_1 = child.getChild(1);
					// verif du not : 
					if (subChild_1 == null && subChild_0 instanceof OperationName) {
						
						if (subChild_0.toString().contains("STABLE")) {
//							Report.report(signal, process, architecture);
						}
					}
				}

			}
		}
		
	}


	private void searchNot() {
	// TODO Auto-generated method stub
	
}


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
			for (int j = 0; j < m; j++) {
				DesignModuleStub stub = dum.getStub(j);
				if (stub.getDUUID().getLibId().equalsIgnoreCase("WORK")) { // pour prendre les fichiers de travail
					if (stub.getDUUID().getType() == LUType.Architecture) {
						try {
							architecture = ((Architecture)zPrj.getDUM().getDM(stub.getDUUID()));

							if (architecture==null){
								write("NODE NULL. Check the Top Level in build path");
							}else{
								write("sourcefile "+architecture.getSourceFile().getFileName()+ "nb lines:"+architecture.getSourceFile().getNumLines());
								searchProcess(architecture);
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

	private void write(String string) {
		if (log ) {
			logger.info(string);
		}

		
		if (logFile ) {
			try {
				fichier.write (string);
				fichier.write ("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
