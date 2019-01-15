package org.zamia.plugin.tool.vhdl.rules.impl.std;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;

public class RuleSTD_01700 extends Rule {

	public RuleSTD_01700() {
		super(RuleE.STD_01700);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		return null;
	}

}
