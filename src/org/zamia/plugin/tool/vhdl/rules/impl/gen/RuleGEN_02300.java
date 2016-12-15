
package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.List;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.ViolationPreservationName;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;

/*
 * Preservation of Clock Name.
 * Clock signal keeps its name through hierarchy levels.
 * No parameters.
 */
public class RuleGEN_02300 extends Rule {
	
	public RuleGEN_02300() {
		super(RuleE.GEN_02300);
	}

	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		//// Makes the clock source list. 
		
		List<ClockSource> clockSources = getAllClockSources();
		if (clockSources == null) {
			return new Pair<Integer, RuleResult> (NO_BUILD, null);
		}

		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (ClockSource clockSource : clockSources) {
				for (ViolationPreservationName violation : clockSource.getViolationPreservationName()) {
					SourceLocation location = violation.getLocation(); 
					String entityId = violation.getEntityName();
					String architectureId = violation.getArchiName();
					Element info = reportFile.addViolation(location, entityId, architectureId);
					reportFile.addElement(ReportFile.TAG_INSTANCE, violation.getComposantName(), info);
					reportFile.addElement(ReportFile.TAG_CLOCK_BEFORE, violation.getSignalNameBefore(), info); 
					reportFile.addElement(ReportFile.TAG_CLOCK_AFTER, violation.getSignalNameAfter(), info);
				}
			}

			result = reportFile.save();
		}
		
		return result;
	}
}
