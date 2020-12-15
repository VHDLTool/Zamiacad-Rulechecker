package org.zamia.plugin.tool.vhdl.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.action.IAction;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.HdlSubProgram;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.vhdl.ast.BlockDeclarativeItem;
import org.zamia.vhdl.ast.PackageBody;
import org.zamia.vhdl.ast.SequentialProcess;
import org.zamia.vhdl.ast.SubProgram;
import org.zamia.vhdl.ast.VHDLPackage;

public class SubProgramManager extends ToolManager {
	
	private static boolean log = true;
	private static boolean logFile = true;
	private static ListUpdateE info;
	private static List<HdlSubProgram> hdlSubPrograms = null;
	
	public static List<HdlSubProgram> getSubProgram() throws EntityException {
		info = updateInfo(info);

		if (info == ListUpdateE.YES && hdlSubPrograms != null) {
			return hdlSubPrograms;
		}
		
		hdlSubPrograms = new ArrayList<>();
		info = ListUpdateE.YES;
		
		PackageBodyManager.getPackageBody();
		Map<String, HdlFile> hdlFiles = ProcessManager.getProcess();
		for (Entry<String, HdlFile> entry: hdlFiles.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			for (HdlEntity hdlEntity: hdlFile.getListHdlEntity()) {
				// search function in entity
				List<BlockDeclarativeItem> items = hdlEntity.getEntity().fDeclarations;
				for (BlockDeclarativeItem item: items) {
					if (item instanceof SubProgram) {
						searchSubProgram((SubProgram)item, hdlEntity, null);
					}
				}
				for (HdlArchitecture hdlArchitecture: hdlEntity.getListHdlArchitecture()) {
					List<BlockDeclarativeItem> itemList = hdlArchitecture.getArchitecture().fDeclarations;
					// search function in architecture
					for (BlockDeclarativeItem item: itemList) {
						if (item instanceof SubProgram) {
							searchSubProgram((SubProgram)item, hdlEntity, hdlArchitecture);
						}
					}
					// search function in process
					for (Process process : hdlArchitecture.getListProcess()) {
						SequentialProcess sequentialProcess = process.getSequentialProcess();
						int n;
						if ((n = sequentialProcess.getNumDeclarations()) > 0) {
							BlockDeclarativeItem item;
							for (int i = 0; i < n; i++) {
								item = sequentialProcess.getDeclaration(i);
								if (item instanceof SubProgram) {
									searchSubProgram((SubProgram)item, hdlEntity, hdlArchitecture);
								}
							}
						}
					}
				}
			}
			// search function in package
			for (VHDLPackage vhdlPackage: hdlFile.getListHdlPackage()) {
				List<BlockDeclarativeItem> items = vhdlPackage.fDeclarations;
				for (BlockDeclarativeItem item: items) {
					if (item instanceof SubProgram) {
						searchSubProgram((SubProgram)item, null, null);
					}
				}
			}
			// search function in package body
			for (PackageBody packageBody: hdlFile.getListPackageBody()) {
				List<BlockDeclarativeItem> items = packageBody.fDeclarations;
				for (BlockDeclarativeItem item: items) {
					if (item instanceof SubProgram) {
						searchSubProgram((SubProgram)item, null, null);
					}
				}
			}
		}
		return hdlSubPrograms;
	}
	
	private static void searchSubProgram(SubProgram subProgram, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		hdlSubPrograms.add(new HdlSubProgram(subProgram, hdlEntity, hdlArchitecture));
		for (int i = 0; i < subProgram.getNumDeclarations(); i++) {
			BlockDeclarativeItem item = subProgram.getDeclaration(i);
			if (item instanceof SubProgram) {
				// search function in function
				searchSubProgram((SubProgram) item, hdlEntity, hdlArchitecture);
			}
		}
	}

	@Override
	public void run(IAction arg0) {
		init(log, logFile);

		// get zamia project
		zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return;
		}
		init(zPrj);
		try {
			getSubProgram();
		} catch (EntityException e) {
			logger.error("some exception message EntityManager getEntity", e);
		}
		close();
	}

	public static void resetInfo() {
		info = ListUpdateE.NO;
	}

}
