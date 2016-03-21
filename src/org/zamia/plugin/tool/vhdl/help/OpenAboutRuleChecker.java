package org.zamia.plugin.tool.vhdl.help;

import javax.swing.JOptionPane;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.zamia.plugin.ZamiaPlugin;

public class OpenAboutRuleChecker implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction arg0) {
		JOptionPane.showMessageDialog(null, "<html>Rule Checker V"+ZamiaPlugin.getDefault().ruleCheckerVersion +" date "+ZamiaPlugin.getDefault().ruleCheckerDate +"</html>", "About Rule Checker",
		        JOptionPane.INFORMATION_MESSAGE);
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

