package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.HashMap;
import java.util.List;
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
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Entity;

public class RuleSTD_01300 extends Rule {

	protected RuleSTD_01300() {
		super(RuleE.STD_01300);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		// get entities
		Map<String, HdlFile> fileMap = new HashMap<>();
		try { 
			fileMap = EntityManager.getEntity();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<>(NO_BUILD, null);
		}
		
		// write a report
		// TODO define to format
		ReportFile reportFile = new ReportFile(this);
		for (Entry<String, HdlFile> hdlFile: fileMap.entrySet()) {
			List<HdlEntity> hdlEntities = hdlFile.getValue().getListHdlEntity();
			for (HdlEntity hdlEntity: hdlEntities) {
				Entity entity = hdlEntity.getEntity();
				int line = 0;
				for (int i = 0; i < entity.getNumInterfaceDeclarations(); i++) {
					if (line == entity.getPorts().get(i).getLocation().fLine) {
						
					}
				}
			}
		}
		return null;
	}

}
