package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;

public class RuleGEN_02100 extends Rule {
	
	private static final String POSITION = "Equal";
	private static final String [] VALUES = {"Behavioral", "RTL", "Simulation"};

	public RuleGEN_02100() {
		super(RuleE.GEN_02100);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		List<IHandbookParam> parameterList = null;
		parameterList = getParameterList(zPrj);
		if (parameterList == null || parameterList.isEmpty()) {
			parameterList = new ArrayList<>();
			for (String value : VALUES) {
				parameterList.addAll(getDefaultStringParamList(POSITION, value));
			}
		}

		Map<String, HdlFile> hdlFiles = new HashMap<>();
		try {
			hdlFiles = ArchitectureManager.getArchitecture();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<> (NO_BUILD, null);
		}
		
		ReportFile reportFile = new ReportFile(this);
		Pair<Integer, RuleResult> result = null;
		if (reportFile.initialize()) {
			for (Entry<String, HdlFile> entry: hdlFiles.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				for (HdlEntity hdlEntity: hdlFile.getListHdlEntity()) {
					for (HdlArchitecture hdlArchitecture: hdlEntity.getListHdlArchitecture()) {
						Architecture architecture = hdlArchitecture.getArchitecture();
						boolean isValid = false;
						for (IHandbookParam param: parameterList) {
							isValid |= param.isValid(architecture.getId());
						}
						if (!isValid) {
							reportFile.addViolation(architecture.getLocation(), hdlEntity.getEntity(), architecture);
							// TODO add sonar msg and error
						}
					}
				}
			}
			result = reportFile.save();
		}
		return result;
	}

}
