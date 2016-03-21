
package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.manager.ProcessManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class RuleSTD_00400 extends RuleManager {

	// Label for Process
	
	RuleE rule = RuleE.STD_00400;


	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {

		String fileName = ""; 
		
		Map<String, HdlFile> hdlFiles;
		try {
			hdlFiles = ProcessManager.getProcess();
		} catch (EntityException e) {
			logger.error("some exception message RuleSTD_00400", e);
			return new Pair<Integer, String> (RuleManager.NO_BUILD,"");
		}

		Element racine = initReportFile(ruleId, rule.getType(), rule.getRuleName());

		Integer cmptViolation = 0;
		for(Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() == null) { continue;}
			for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
				if (hdlEntityItem.getListHdlArchitecture() == null) { continue;}
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListProcess() == null) { continue;}
					for (Process processItem : hdlArchitectureItem.getListProcess()) {
						if (!processItem.hasLabel()) {
							cmptViolation++;
							addViolation(racine, processItem, "noLabelForProcess", hdlFile, hdlEntityItem, hdlArchitectureItem);
						}
					}
				}
			}
		}
		
		if (cmptViolation != 0) {
			fileName = createReportFile(ruleId, rule.getRuleName(), rule.getType());
		}
		//		ZamiaErrorObserver.updateAllMarkers(zPrj);
		return new Pair<Integer, String> (cmptViolation, fileName);
	}


	private void addViolation(Element racine, Process processItem,
			String error, HdlFile hdlFile, HdlEntity hdlEntityItem,
			HdlArchitecture hdlArchitectureItem) {
		Element processElement = document.createElement(NodeType.PROCESS.toString());
		racine.appendChild(processElement);

		Element violationTypeElement = document.createElement("violationType");
		violationTypeElement.setTextContent(error);
		processElement.appendChild(NewElement(document, "violationType"
				, error));
		
		processElement.appendChild(NewElement(document, NodeType.FILE.toString()+NodeInfo.NAME.toString()
				, hdlFile.getLocalPath()));
		
		processElement.appendChild(NewElement(document, NodeType.ENTITY.toString()+NodeInfo.NAME.toString()
				, hdlEntityItem.getEntity().getId()));
		
		processElement.appendChild(NewElement(document, NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString()
				, hdlArchitectureItem.getArchitecture().getId()));

		processElement.appendChild(NewElement(document, NodeType.PROCESS.toString()+NodeInfo.LOCATION.toString()
				, String.valueOf(processItem.getLocation().fLine)));
}


}

