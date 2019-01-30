package org.zamia.plugin.tool.vhdl.help;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.zamia.ZamiaLogger;

import edu.stanford.ejalbert.BrowserLauncher;


public class OpenFeatures implements IWorkbenchWindowActionDelegate {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();
	
	@Override
	public void run(IAction arg0) {
		try {
			BrowserLauncher launcher = new BrowserLauncher();
			launcher.openURLinBrowser("https://github.com/VHDLTool/Zamiacad-Rulechecker/wiki/features");
		} catch (Exception e) {
			e.printStackTrace();
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
