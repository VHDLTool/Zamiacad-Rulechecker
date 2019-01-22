package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ArchitectureManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;


public class RuleSTD_00900 extends Rule {

	public RuleSTD_00900() {
		super(RuleE.STD_00900);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		Pair<Integer, RuleResult> result = null;
		Map<String, HdlFile> hdlFiles = null;
		
		// get all the entities
		try {
			hdlFiles = ArchitectureManager.getArchitecture();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<>(RuleManager.NO_BUILD, null);
		}
		
		// write report
		ReportFile reportFile = new ReportFile(this);
		String fileName;
		if (reportFile.initialize()) {
			if (hdlFiles != null && hdlFiles.size() > 0) {
				for (Entry<String, HdlFile> entry: hdlFiles.entrySet()) {
					HdlFile file;
					if ((file = entry.getValue()) != null) {
						if (!file.getListHdlEntity().isEmpty()) {
							fileName = file.getFile().getName();
							for (HdlEntity entity: file.getListHdlEntity()) {
								// case insensitive
								if (!fileName.toUpperCase().contains(entity.getEntity().getId().toUpperCase())) {
									Element element = reportFile.addViolation(
											entity.getEntity().getLocation(),
											entity.getEntity().getId(),
		                                    entity.getListHdlArchitecture().isEmpty() ? " " : entity.getListHdlArchitecture().get(0).getArchitecture().getId());
									reportFile.addSonarTags(element, SonarQubeRule.SONAR_ERROR_STD_00900, new Object[] {fileName, entity.getEntity().getId()},
											SonarQubeRule.SONAR_MSG_STD_00900, new Object[] {fileName, entity.getEntity().getId()});
								}
							}
						}
					}
				}
				result = reportFile.save();
			}
		}
		return result;
	}

}
