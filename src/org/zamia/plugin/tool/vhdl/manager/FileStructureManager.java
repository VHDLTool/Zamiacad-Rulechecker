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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.VHDLNode;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class FileStructureManager extends ToolManager {


	private static boolean log = true;

	private static boolean logFile = true;

	
	private int incr =0;

	
	/**
	 * method is called by vhdl tool pull down menu for debug
	 */
	public void run(IAction action) {

		init(log, logFile);
		try {

			
			File ff=new File("C:\\resultat.txt"); // définir l'arborescence
			ff.createNewFile();
			fichier=new FileWriter(ff);


			ArrayList<Architecture> listArchi = ArchitectureManager.getArrayListArchitecture();
			for (Architecture archi : listArchi) {
					write("sourcefile "+archi.getSourceFile().getFileName()+ "nb lines:"+archi.getSourceFile().getNumLines());
					nodeToLogger(archi);
//					break;
			}


			// update markers (for marker creation test )
			fichier.close();
		} catch (IOException e) {
			logger.error("some exception message FileStructureManager", e);
			return;
		}

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
					+ " >> numChildren: "+ numChildren
					+ " num line " + node.getLocation().fLine);

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
