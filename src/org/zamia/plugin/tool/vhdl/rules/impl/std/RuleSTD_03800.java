package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.Violation;
import org.zamia.plugin.tool.vhdl.manager.RegisterAffectationManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

/*
 * Synchronous Elements Initialization.
 * All synchronous elements (such as registers, counters and FSM states...) are initialized by a reset.
 * No parameters.
 */
public class RuleSTD_03800 extends Rule {

	public RuleSTD_03800() {
		super(RuleE.STD_03800);
	}

	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		//// Make the register affectation list. 
		
		Map<String, HdlFile> hdlFiles;
		
		try {
			hdlFiles = RegisterAffectationManager.getRegisterAffectation();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<Integer, RuleResult> (RuleManager.NO_BUILD, null);
		}

		//// Make the violation list. 
		
		listViolation = new ArrayList<Violation>();
		
		for(Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() == null) continue;
			
			for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
				if (hdlEntityItem.getListHdlArchitecture() == null) continue;
				
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListProcess() == null) continue;
					
					for (Process processItem : hdlArchitectureItem.getListProcess()) {
						if (processItem.getListClockSignal() == null) continue;
						
						for (ClockSignal clockSignalItem : processItem.getListClockSignal()) {
							checkInitialization(clockSignalItem, hdlFile, hdlEntityItem,
									hdlArchitectureItem, processItem);
							checkAffectation(clockSignalItem, hdlFile, hdlEntityItem,
									hdlArchitectureItem, processItem);

						}
					}
				}
			}
		}

		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			for (Violation violation : listViolation) {
				String entity = violation.getEntity().getId();
				String architecture = violation.getArchitecture().getId();
				String fileName = violation.getLocalPath();
				int line = violation.getfLine();
				Element info = reportFile.addViolation(fileName, line, entity, architecture);
				
				String registerId = violation.getName();
				reportFile.addElement(ReportFile.TAG_REGISTER, registerId, info); 
				
				String clockId = violation.getClockSignalItem().getClockSource().toString();
				reportFile.addElement(ReportFile.TAG_CLOCK, clockId, info); 
				
				String processId = violation.getName();
				reportFile.addElement(ReportFile.TAG_PROCESS, processId, info); 
			}
			
			result = reportFile.save();
		}
		
		return result;
	}
}
