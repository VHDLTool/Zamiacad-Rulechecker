package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.List;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;

public class RuleGEN_01600 extends Rule{
	
	private boolean withParameter = false;

	public RuleGEN_01600() {
		super(RuleE.GEN_01600);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		List<IHandbookParam> parameterList = getParameterList(zPrj);
		if (parameterList == null && withParameter) {
			return new Pair<Integer, RuleResult> (WRONG_PARAM, null);
		} else if (parameterList != null && withParameter) {
			
		}
		
		return null;
	}

}
