package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.manager.EntityManager;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.StringParam;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.SonarQubeRule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.VHDLPackage;

public class RuleGEN_01600 extends Rule{
	


	public RuleGEN_01600() {
		super(RuleE.GEN_01600);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		List<IHandbookParam> parameterList = null;
		//// Initialize the parameter from rule configuration.
		parameterList = getParameterList(zPrj);
		if (parameterList == null) {
			return new Pair<Integer, RuleResult> (WRONG_PARAM, null);
		}

		Map<String, HdlFile> hdlFiles = new HashMap<>();
		try {
			hdlFiles = EntityManager.getEntity();
		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<> (NO_BUILD, null);
		}
		
		ReportFile reportFile = new ReportFile(this);
		Pair<Integer, RuleResult> result = null;
		if (reportFile.initialize()) {
			for (Entry<String, HdlFile> entry: hdlFiles.entrySet()) {
				HdlFile hdlFile = entry.getValue();
				for (VHDLPackage vhdlPackage: hdlFile.getListHdlPackage()) {
					boolean isValid = false;
					for (IHandbookParam param: parameterList) {
						isValid |= param.isValid(vhdlPackage.getId());
					}
					if (!isValid) {
						Element element = reportFile.addViolation(vhdlPackage.getLocation(), " ", " ");
						reportFile.addElement(ReportFile.TAG_PACKAGE, vhdlPackage.getId(), element);
						
						//FIXME: this is copy pasted from RuleSTD__00200.java
						String paramString = null;
						HashMap<StringParam.Position, String> params = new HashMap<StringParam.Position, String>();
						for (IHandbookParam param : parameterList)
						{
							if (param instanceof StringParam)
							{
								StringParam stringParam = (StringParam) param;
								
								if (params.containsKey(stringParam.getPosition()))
								{
									String positionValues = (String) params.get(stringParam.getPosition());
									params.put(stringParam.getPosition(), positionValues + ", " + stringParam.getValue());
								}
								else
								{
									params.put(stringParam.getPosition(), stringParam.getValue());
								}
							}
						}
						for (Map.Entry<StringParam.Position, String> entry2: params.entrySet())
						{
							paramString = paramString != null? paramString + " or to ": "";
							paramString = paramString + entry2.getKey().toString() + " " + entry2.getValue();
						}
						//endFIXME
						
						reportFile.addSonarTags(
								element,
								SonarQubeRule.SONAR_ERROR_GEN_01600,
								new Object[] {vhdlPackage.getId()},
								SonarQubeRule.SONAR_MSG_GEN_01600,
								new Object[] {vhdlPackage.getId(), paramString.toLowerCase()});
					}
				}
			}
			result = reportFile.save();
		}
		return result;
	}
}
