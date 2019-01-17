package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
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
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;

public class RuleSTD_01100 extends Rule {

	public RuleSTD_01100() {
		super(RuleE.STD_01100);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		Pair<Integer, RuleResult> result = null;
		Map<String, HdlFile> hdlFiles = null;
		
		// get all the architectures
		try {
			hdlFiles = ArchitectureManager.getArchitecture();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<>(RuleManager.NO_BUILD, null);
		}
		
		// report
		ReportFile reportFile = new ReportFile(this);
		HdlFile hdlFile;
		String fileName;
		if (reportFile.initialize()) {
			if (hdlFiles != null && hdlFiles.size() > 0) {
				for (Entry<String, HdlFile> entry: hdlFiles.entrySet()) {
					hdlFile = entry.getValue();
					if (hdlFile == null || hdlFile.getListHdlEntity().size() == 0) {
						continue;
					} else if (hdlFile.getListHdlEntity().size() == 1) {
						// if there is only one entity in the file
						fileName = hdlFile.getFile().getName();
						logger.info(">>>>>>> File: %s <1 entity>", fileName);
						HdlEntity hdlEntity = hdlFile.getListHdlEntity().get(0);
						List<HdlArchitecture> hdlArchitectures = hdlEntity.getListHdlArchitecture();
						logger.info("<<<<<<<<< %d architextures in this entity", hdlArchitectures.size());
						if (hdlArchitectures.size() > 1) {
							for (HdlArchitecture hdlArchitecture: hdlArchitectures) {
								Element element = reportFile.addViolation(hdlArchitecture.getArchitecture().getLocation(), hdlEntity.getEntity(), hdlArchitecture.getArchitecture());
								reportFile.addSonarTags(element, SonarQubeRule.SONAR_ERROR_STD_01100, new Object[] {fileName}, SonarQubeRule.SONAR_MSG_STD_01100, new Object[] {fileName});
							}
						}
					} else {
						// if there are multiple entities in the file
						fileName = hdlFile.getFile().getName();
						logger.info(">>>>>>> File: %s  <n entities>", fileName);
						List<HdlEntity> entities = hdlFile.getListHdlEntity();
						List<HdlArchitecture> hdlArchitectures = new ArrayList<>();
						for (HdlEntity hdlEntity: entities) {
							hdlArchitectures.addAll(hdlEntity.getListHdlArchitecture());
							logger.info("<<<<<<<<< %d architextures in entity: %s", hdlEntity.getListHdlArchitecture().size(), hdlEntity.getEntity().getId());
						}
						if (hdlArchitectures.size() > 1) {
							for (HdlArchitecture hdlArchitecture: hdlArchitectures) {
								Element element = reportFile.addViolation(hdlArchitecture.getArchitecture().getLocation(), hdlArchitecture.getEntityName(), hdlArchitecture.getArchitecture().getId());
								reportFile.addSonarTags(element, SonarQubeRule.SONAR_ERROR_STD_01100, new Object[] {fileName}, SonarQubeRule.SONAR_MSG_STD_01100, new Object[] {fileName});
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
