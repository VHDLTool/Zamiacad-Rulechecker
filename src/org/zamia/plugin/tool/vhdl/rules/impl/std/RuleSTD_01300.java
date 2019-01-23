package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ArchitectureManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.InterfaceDeclaration;

public class RuleSTD_01300 extends Rule {

	public RuleSTD_01300() {
		super(RuleE.STD_01300);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		// get entities
		Map<String, HdlFile> fileMap = new HashMap<>();
		try { 
			fileMap = ArchitectureManager.getArchitecture();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<>(NO_BUILD, null);
		}
		
		// write a report
		Pair<Integer, RuleResult> result = null;
		ReportFile reportFile = new ReportFile(this);
		if(reportFile.initialize()) {
			for (Entry<String, HdlFile> hdlFile: fileMap.entrySet()) {
				List<HdlEntity> hdlEntities = hdlFile.getValue().getListHdlEntity();
				for (HdlEntity hdlEntity: hdlEntities) {
					Entity entity = hdlEntity.getEntity();
					List<HdlArchitecture> architectureList = hdlEntity.getListHdlArchitecture();
					int line = 0;
					InterfaceDeclaration firsPort = null;
					for (int i = 0; i < entity.getNumInterfaceDeclarations(); i++) {
						InterfaceDeclaration port = entity.getPorts().get(i);
						SourceLocation location = port.getLocation();
						if (line == location.fLine) {
							if (firsPort != null) {
								Element element = reportFile.addViolation(location, entity.getId(), architectureList.isEmpty() ? " " : architectureList.get(0).getArchitecture().getId());
								reportFile.addElement(ReportFile.TAG_PORT, firsPort.getId(), element);
								reportFile.addSonarTags(element, SonarQubeRule.SONAR_ERROR_STD_01300, new Object[] {firsPort.getId()}, SonarQubeRule.SONAR_MSG_STD_01300, new Object[] {firsPort.getId()});
								firsPort = null;
							}
							Element info = reportFile.addViolation(location, entity.getId(), architectureList.isEmpty() ? " " : architectureList.get(0).getArchitecture().getId());
							reportFile.addElement(ReportFile.TAG_PORT, port.getId(), info);
							reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_01300, new Object[] {port.getId()}, SonarQubeRule.SONAR_MSG_STD_01300, new Object[] {port.getId()});
						} else {
							line = location.fLine;
							firsPort = port;
						}
					}
				}
			}
			result = reportFile.save();
		}
		return result;
	}

}
