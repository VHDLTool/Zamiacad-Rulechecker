package org.zamia.plugin.tool.vhdl.manager;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ListUpdateE;

public class InputOutputManager extends ToolManager {

	private boolean log = true;

	private boolean logFile = true;

	private static ListUpdateE info;

	@Override
	public void run(IAction arg0) {
		init(log, logFile);

		zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return;
		}
		
		init(zPrj);
		

		try {
			getInputOutputComponent();
//			dumpXml(listHdlFile, "REQ_FEAT_REG_ID", "register Identification");
			logger.info("Rule Checker: tool INPUT OUTPUT identification (REQ_FEAT_IO_ID) has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message InputOutputManager", e);
		}
		

		close();
	}

	public static void resetInfo() {
		info = ListUpdateE.NO;
	}

	public static Map<String, HdlFile> getInputOutputComponent() throws EntityException {
		info = updateInfo(info);

		if (info == ListUpdateE.YES && listHdlFile != null) {
			return listHdlFile;
		}

		ArchitectureManager.getArchitecture();
		info = ListUpdateE.YES;

		int num = 0;
		
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
					num = hdlEntityItem.setInputOutput(num);
				}
			}

		}

		return listHdlFile;

	}


}
