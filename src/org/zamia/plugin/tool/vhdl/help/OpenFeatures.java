package org.zamia.plugin.tool.vhdl.help;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.zamia.ZamiaLogger;


public class OpenFeatures implements IWorkbenchWindowActionDelegate {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();
	
	@Override
	public void run(IAction arg0) {

		InputStream pdfInJar = getClass().getClassLoader().getResourceAsStream("/help/ZamiaCad_Rule_Checker_Features_Description.pdf");
		try {
		    File pdf = new File("ZamiaCad_Rule_Checker_Features_Description.pdf");
		    FileOutputStream fos = new java.io.FileOutputStream(pdf);
		    while (pdfInJar.available() > 0) {
		          fos.write(pdfInJar.read());
		    }
		    fos.close();
		    Desktop.getDesktop().open(pdf);
		} catch (IOException e) {
//			logger.debug("IOException in OpenFeatures");
			logger.error("some exception message", e);
		}
		
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
