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
import org.zamia.plugin.tool.vhdl.rules.impl.SensitivityRuleViolation;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.SignalDeclaration;
import org.zamia.vhdl.ast.VHDLNode;

/*
 * Sensitivity list for combinational processes.
 * Combinational processes have a sensitivity list including all inputs signals which are read.
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

		initializeRule(parameterSource, ruleId);

		//// Make register list

		Map<String, HdlFile> hdlFiles = null;
		ReportFile reportFile = new ReportFile(this);
		reportFile.initialize();

		try {
			hdlFiles = ArchitectureManager.getArchitecture();
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//// Check rule
		Pair<Integer, RuleResult> result = null;
		_violations = new ArrayList<SensitivityRuleViolation>();

		for(Entry<String, HdlFile> entry : hdlFiles.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() == null) 
				continue;

			_hdlFile = hdlFile;
			for (HdlEntity hdlEntityItem : _hdlFile.getListHdlEntity()) {
				_entity = hdlEntityItem.getEntity();
				if (hdlEntityItem.getListHdlArchitecture() == null) 
					continue;

				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					_architecture = hdlArchitectureItem.getArchitecture();
					//					Object[] objTab = fContentProvider.getChildren(_architecture);
					List<HdlSignalAssignment> listSignals = hdlArchitectureItem.getListSignalAssignment();
					if(listSignals.size() > 0) {
						logger.error("toto");
					}

					int numChildren = _architecture.getNumChildren();
					for (int i = 0; i < numChildren; i++) {
						VHDLNode child = _architecture.getChild(i);

						if (child instanceof SignalDeclaration) {
							SignalDeclaration signalDec = (SignalDeclaration) child;
							if(signalDec.toString().contains(":=")) {
								//								_violations.add(new SensitivityRuleViolation(_architecture.getSourceFile().getFileName(), child.getStartLine(), _entity, _architecture, "process", "sensitivityName", false, false));
								Element info = reportFile.addViolation(signalDec.getLocation(), _entity, _architecture);
								reportFile.addSonarTags(info, SonarQubeRule.SONAR_ERROR_STD_00300, new Object[] {signalDec.toString()}, SonarQubeRule.SONAR_MSG_STD_00300, null);		
							}
						}
					}
					//					listSignals.get(0).getComponentInstantiation();
				}													
			}
		}


		////Write report

		result = reportFile.save();


		return result;

	}
}
