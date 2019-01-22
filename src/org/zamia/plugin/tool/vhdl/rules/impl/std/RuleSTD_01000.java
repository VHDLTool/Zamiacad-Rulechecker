package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
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


public class RuleSTD_01000 extends Rule {

	public RuleSTD_01000() {
		super(RuleE.STD_01000);
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
		
		// report
		ReportFile reportFile = new ReportFile(this);
		HdlFile file;
		String fileName;
		if (reportFile.initialize()) {
			if (hdlFiles != null && hdlFiles.size() > 0) {
				for (Entry<String, HdlFile> entry: hdlFiles.entrySet()) {
					file = entry.getValue();
					fileName = file.getFile().getName();
					logger.info("File %s contains %d entity", fileName, file.getListHdlEntity().size());
					if (file.getListHdlEntity().size() > 1) {
						ArrayList<HdlEntity> entities = file.getListHdlEntity();
						for (HdlEntity entity: entities) {
							Element element = reportFile.addViolation(
									entity.getEntity().getLocation(),
									entity.getEntity().getId(),
									entity.getListHdlArchitecture().isEmpty() ? " " : entity.getListHdlArchitecture().get(0).getArchitecture().getId());
							reportFile.addSonarTags(element, SonarQubeRule.SONAR_ERROR_STD_01000, new Object[] {fileName}, SonarQubeRule.SONAR_MSG_STD_01000, new Object[] {fileName});
						}
					}
				}
				result = reportFile.save();
			}
		}
		return result;
	}

}
