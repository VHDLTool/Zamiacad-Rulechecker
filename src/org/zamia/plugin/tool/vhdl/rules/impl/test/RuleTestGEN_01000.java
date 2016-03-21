package org.zamia.plugin.tool.vhdl.rules.impl.test;

import java.util.ArrayList;
import java.util.List;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class RuleTestGEN_01000 extends RuleManager {

//	Rule rule = Rule.GEN_01000;
	
	
	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
		System.out.println("Rule.GEN_01000");
		List<List<Object>> listParam = new ArrayList<List<Object>>();
		List<Object> param = new ArrayList<Object>(); 
		param.add("clockName");
		param.add(String.class);
		param.add("test1");
		param.add("test2");
		listParam.add(param);

		List<List<Object>> xmlParameterFileConfig = getXmlParameterFileConfig(zPrj, ruleId, listParam);

		for (List<Object> list : xmlParameterFileConfig) {
			for (Object paramItem : list) {
				System.out.println(paramItem.toString());
			}
			
		}
		return new Pair<Integer, String>(10,"");
	}



	
	
}
