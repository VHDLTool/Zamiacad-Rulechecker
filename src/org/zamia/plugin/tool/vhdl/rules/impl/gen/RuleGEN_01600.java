package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.VHDLPackage;

public class RuleGEN_01600 extends Rule{
	
	private static final String POSITION = "Prefix";
	private static final String VALUE = "pkg_";

	public RuleGEN_01600() {
		super(RuleE.GEN_01600);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		List<IHandbookParam> parameterList = null;
		parameterList = getParameterList(zPrj);
		if (parameterList == null || parameterList.isEmpty()) {
			parameterList = getDefaultList();
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
						Element element = reportFile.addViolation(vhdlPackage.getLocation());
						reportFile.addElement(ReportFile.TAG_PACKAGE, vhdlPackage.getId(), element);
						// TODO add sonar msg and error
					}
				}
			}
			result = reportFile.save();
		}
		return result;
	}
	
	private List<IHandbookParam> getDefaultList(){
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element params = document.createElement("hb:StringParam");
			Element id = document.createElement("hb:ParamID");
			id.setTextContent("P1");
			Element position = document.createElement("hb:Position");
			position.setTextContent(POSITION);
			Element value = document.createElement("hb:Value");
			value.setTextContent(VALUE);
			params.appendChild(id);
			params.appendChild(position);
			params.appendChild(value);
			List<IHandbookParam> parameter= new ArrayList<>();
			parameter.add(new StringParam(params));
			return parameter;
		} catch (ParserConfigurationException e) {
			logger.error("Error in #getDefaultList: %s", e.getMessage());
		}
		return new ArrayList<>();
	}

}
