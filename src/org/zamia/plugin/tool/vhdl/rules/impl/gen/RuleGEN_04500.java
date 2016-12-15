package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;

/*
 * Name of clock signal.
 * The clock signal name includes some tag defined by rule parameters.
 * No parameters. 
 */
public class RuleGEN_04500 extends Rule {

	public RuleGEN_04500() {
		super(RuleE.GEN_04500);
	}
	
	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		logger.error("Rule GEN_04500 is not implemented.");
		return new Pair<Integer, RuleResult> (-1, null);
	}
}
