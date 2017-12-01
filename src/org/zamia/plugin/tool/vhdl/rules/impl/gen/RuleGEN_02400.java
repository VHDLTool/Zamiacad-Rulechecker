package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.ViolationPreservationName;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;

/*
 * Preservation of Reset Name.
 * Reset signal keeps its name through hierarchy levels.
 * No parameters.
 */
public class RuleGEN_02400 extends Rule {
	
	public RuleGEN_02400() {
		super(RuleE.GEN_02400);
	}

	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		//// Makes the reset read source source list. 
		
		List<ResetSource> resetSources = getAllResetReadSources();
		if (resetSources == null) {
			return new Pair<Integer, RuleResult> (NO_BUILD, null);
		}

		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (ResetSource resetSource : resetSources) {
				for (ViolationPreservationName violation : resetSource.getViolationPreservationName()) {
					SourceLocation location = violation.getLocation(); 
					String entityId = violation.getEntityName();
					String architectureId = violation.getArchiName();
					Element info = reportFile.addViolation(location, entityId, architectureId);
					reportFile.addElement(ReportFile.TAG_INSTANCE, violation.getComposantName(), info);
					reportFile.addElement(ReportFile.TAG_RESET_BEFORE, violation.getSignalNameBefore(), info); 
					reportFile.addElement(ReportFile.TAG_RESET_AFTER, violation.getSignalNameAfter(), info);
					
					reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_GEN_02400, new Object[] {violation.getSignalNameBefore(), violation.getSignalNameAfter()}, SonarQubeRule.SONAR_MSG_GEN_02400, new Object[] {violation.getSignalNameAfter(), violation.getSignalNameBefore()});
				}
			}

			result = reportFile.save();
		}
		
		return result;
	}
}
