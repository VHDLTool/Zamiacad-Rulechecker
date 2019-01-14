package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.Map;
import java.util.Map.Entry;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.EntityManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
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
			hdlFiles = EntityManager.getEntity();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<Integer, RuleResult>(RuleManager.NO_BUILD, null);
		}
		
		// TODO report file to be defined
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			if (hdlFiles != null && hdlFiles.size() > 0) {
				for (Entry<String, HdlFile> entry: hdlFiles.entrySet()) {
					HdlFile file;
					if ((file = entry.getValue()) != null) {
						if (file.getListHdlEntity().size() > 0) {
							for (HdlEntity entity: file.getListHdlEntity()) {
								// case insensitive
								if (!file.getFile().getName().toUpperCase().contains(entity.getEntity().getId().toUpperCase())) {
									reportFile.addViolation(entity.getEntity().getLocation());
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
