package org.zamia.plugin.tool.vhdl.rules.impl.std;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;

public class RuleSTD_01100 extends Rule {

	protected RuleSTD_01100() {
		super(RuleE.STD_01100);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		return null;
	}

}
