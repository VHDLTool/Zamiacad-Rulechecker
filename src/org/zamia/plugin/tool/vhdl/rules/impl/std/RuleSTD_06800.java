package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.editors.ZamiaOutlineContentProvider;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.HdlSignalAssignment;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.ArchitectureManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.plugin.tool.vhdl.rules.impl.SensitivityRuleViolation;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.SignalDeclaration;
import org.zamia.vhdl.ast.VHDLNode;

/*
 * Rule 6800 : 'Unsuitability of signal initialization in declaration section'
 * 
 * The objective is to check that no signal is initialized inside the declaration section
 * 
 * No Parameters.
 */
public class RuleSTD_06800 extends Rule {

	private HdlFile _hdlFile;
	private Entity _entity;
	private Architecture _architecture;
	private List<SensitivityRuleViolation> _violations;
	private ZamiaOutlineContentProvider fContentProvider;

	public RuleSTD_06800() {
		super(RuleE.STD_06800);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		/*
		 * Search for signals declaration inside every architectures of PLD project. 
		 * Verify that no signal is initialized. In case of initialization, just after the type declaration, 
		 * the initialization value is written with following format “:= INIT_VALUE;” 
		 * where INIT_VALUE depends on the types of the declared signal
		 */

		initializeRule(parameterSource, ruleId);

		Map<String, HdlFile> hdlFiles = null;
		ReportFile reportFile = new ReportFile(this);
		reportFile.initialize();
				 

		// Retrieve VHDL files inside current project from ArchitectureManager
		try {
			hdlFiles = ArchitectureManager.getArchitecture();
		} catch (EntityException e) {
			logger.error("Current project needs a build.");
			return new Pair<Integer, RuleResult> (RuleManager.NO_BUILD, null);
		}

		
		Pair<Integer, RuleResult> result = null;
		_violations = new ArrayList<SensitivityRuleViolation>();

		for(Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() == null) 
				continue;

			_hdlFile = hdlFile;
			// iterate over each entity contained inside the file
			for (HdlEntity hdlEntityItem : _hdlFile.getListHdlEntity()) {
				_entity = hdlEntityItem.getEntity();
				if (hdlEntityItem.getListHdlArchitecture() == null) 
					continue;
				// iterate over each architecture contained inside the entity
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					_architecture = hdlArchitectureItem.getArchitecture();
					List<HdlSignalAssignment> listSignals = hdlArchitectureItem.getListSignalAssignment();

					int numChildren = _architecture.getNumChildren();
					// retrieve all VHDL's node contained inside the architecture
					for (int i = 0; i < numChildren; i++) {
						VHDLNode child = _architecture.getChild(i);
						// check if node is a signal
						if (child instanceof SignalDeclaration) {
							// for each signal found inside the architecture,
							SignalDeclaration signalDec = (SignalDeclaration) child;
							// determine if its declaration contains the string ":= INIT_VALUE"
							if(signalDec.toString().contains(":=")) {
								// raise a violation
								Element info = reportFile.addViolation(signalDec.getLocation(), _entity, _architecture);
								reportFile.addElement(ReportFile.TAG_SIGNAL, signalDec.getId(), info); 
								reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_06800, new Object[] {signalDec.getId()}, SonarQubeRule.SONAR_MSG_STD_06800, new Object[] {signalDec.getId()});		
							}
						}
					}
				}													
			}
		}


		////Write report

		result = reportFile.save();


		return result;

	}
}
