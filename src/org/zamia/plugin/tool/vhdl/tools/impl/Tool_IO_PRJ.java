package org.zamia.plugin.tool.vhdl.tools.impl;

import java.util.Map;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.manager.InputOutputManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_IO_PRJ extends ToolSelectorManager {

	// Input Output Identification
	
	ToolE tool = ToolE.REQ_FEAT_IO_PRJ;

	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		String fileName = "";
		
		try {
			Map<String, HdlFile> listHdlFile = InputOutputManager.getInputOutputComponent();
			fileName = ToolManager.dumpXml(listHdlFile, tool);
		} catch (EntityException e) {
			logger.error("some exception message Tool_IO_PRJ", e);
			return new Pair<Integer, String> (NO_BUILD, "");
		}

		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (0, fileName);
	}


}
