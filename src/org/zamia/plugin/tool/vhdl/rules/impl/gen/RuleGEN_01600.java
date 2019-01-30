package org.zamia.plugin.tool.vhdl.rules.impl.gen;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.StringParam;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;

public class RuleGEN_01600 extends Rule{
	
	private boolean withParameter = false;

	public RuleGEN_01600() {
		super(RuleE.GEN_01600);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		initializeRule(parameterSource, ruleId);
		
		List<IHandbookParam> parameterList;
		if (withParameter) {
			parameterList = getParameterList(zPrj);
			if (parameterList == null) {
				return new Pair<> (WRONG_PARAM, null);
			}
		} else {
			parameterList = getDefaultList();
		}
		
		return null;
	}
	
	private List<IHandbookParam> getDefaultList(){
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = document.createElement("hb:RuleParams");
			Element params = document.createElement("hb:StringParam");
//			params.appendChild(document.create)
			document.appendChild(root);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

}
