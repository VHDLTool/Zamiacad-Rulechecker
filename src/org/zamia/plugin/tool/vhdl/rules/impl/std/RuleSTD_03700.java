package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.List;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;

/*
 * Reset Assertion and De-assertion.
 * Internal reset is asserted asynchronously and deasserted synchronously. 
 * No parameters.
 */
public class RuleSTD_03700 extends Rule {

	public RuleSTD_03700() {
		super(RuleE.STD_03700);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		//// Make reset signal list
		
		List<ResetSource> resetSources = getAllResetSources();
		if (resetSources == null) {
			return new Pair<Integer, RuleResult> (NO_BUILD, null);
		}

		//// Write report

		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (ResetSource resetSource : resetSources) {	
				SourceLocation location = resetSource.getSignalDeclaration().getLocation();
				String entityId = resetSource.getEntity();
				String architectureId = resetSource.getArchitecture();
				Element info = reportFile.addViolation(location, entityId, architectureId);
				
				reportFile.addElement(ReportFile.TAG_RESET, resetSource.toString(), info);
				reportFile.addElement(ReportFile.TAG_SIGNAL_TYPE, resetSource.getType(), info);
				
				reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_03700, null, SonarQubeRule.SONAR_MSG_STD_03700, null);
			}
			
			result = reportFile.save();
		}
		
		return result;
	}
}
