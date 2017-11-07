package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.List;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.ClockSourceRead;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;

/*
 * Use of clock signal.
 * Clock signal is not used inside combinational function like multiplexor or digital gate.
 * No Parameters.
 */
public class RuleGEN_04900 extends Rule {
	
	public RuleGEN_04900() {
		super(RuleE.GEN_04900);
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
				for (ClockSourceRead clockRead : clockSource.getListReadClockSource()) {
					if (clockRead.isWrongUsesClock()) {
						SourceLocation location = clockRead.getRead().getLocation();
						String entityId = clockRead.getEntityName();
						String architectureId =clockRead.getArchitectureName();
						Element info = reportFile.addViolation(location, entityId, architectureId);
						reportFile.addElement(ReportFile.TAG_CLOCK, clockSource.toString(), info); 
						
						reportFile.addElement(ReportFile.TAG_SONAR_ERROR, "Reset signal " + clockSource.toString() + " is misused", info);
						reportFile.addElement(ReportFile.TAG_SONAR_MSG, "Use " + clockSource.toString() + " for clock inputs only and not as common", info);
					}
				}
			}
			
			result = reportFile.save();
		}
		
		return result;
	}
}
