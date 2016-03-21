package org.zamia.plugin.tool.vhdl.tools.impl;

import java.util.Map;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.manager.RegisterSourceManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.plugin.tool.vhdl.tools.ToolE;
import org.zamia.plugin.tool.vhdl.tools.ToolSelectorManager;
import org.zamia.util.Pair;

public class Tool_FN_22 extends ToolSelectorManager {

	// Clock domain change
	
	ToolE tool = ToolE.REQ_FEAT_FN22;


	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {

		String fileName = ""; 
		
		Map<String, HdlFile> hdlFiles;
		try {
			hdlFiles = RegisterSourceManager.getRegisterSource();
			fileName = ToolManager.dumpXml(hdlFiles, tool);
		} catch (EntityException e) {
			logger.error("some exception message Tool_FN_22", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		return new Pair<Integer, String> (0, fileName);
	}



}
