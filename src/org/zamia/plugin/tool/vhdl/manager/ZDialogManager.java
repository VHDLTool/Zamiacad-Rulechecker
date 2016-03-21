package org.zamia.plugin.tool.vhdl.manager;

import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;

public class ZDialogManager extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ZDialogManager(ZamiaProject zPrj) {
//		zPrj.getToolVhdlMgr().addObserver(this);
	}
	
	protected void deleteDirectory(String ruleTool) {
		List<String> xmlLogReport = ToolManager.getXmlLogReport(ruleTool, RuleTypeE.NA);
		if (xmlLogReport == null) { return;}
		String directory = xmlLogReport.get(0);
		if (directory == null) {return;}
		
		File rep = new File(directory);
		
		deleteSubDirectory(rep);
	}


	private void deleteSubDirectory(File rep) {
		
		for (File file : rep.listFiles()) {
			if (file.isDirectory()) {
				deleteSubDirectory(file);
			} 
			file.delete();
		}

	}


	@Override
	public void update(Observable arg0, Object arg1) {
		this.setVisible(false);
	}

}
