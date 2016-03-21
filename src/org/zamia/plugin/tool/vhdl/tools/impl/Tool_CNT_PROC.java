package org.zamia.plugin.tool.vhdl.tools.impl;

import java.util.Map;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.manager.ProcessManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_CNT_PROC extends ToolSelectorManager {

	// Number of line per process
	
	ToolE tool = ToolE.REQ_FEAT_CNT_PROC;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		String fileName = "";
		
		try {
			Map<String, HdlFile> listHdlFile = ProcessManager.getProcess();
			fileName = ToolManager.dumpXml(listHdlFile, tool);
		} catch (EntityException e) {
			logger.error("some exception message Tool_CNT_PROC", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}

}
