package org.zamia.plugin.tool.vhdl.rules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;

public class LaunchRules {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();
	
	static String methodName = "Launch";
	

	public static Pair<Integer, RuleResult> Launch(RuleStruct ruleItem, ZamiaProject zPrj) {

		if (RuleE.exist(ruleItem.getIdImpl())) {
			RuleE rule = RuleE.valueOf(ruleItem.getIdImpl());
			Class<? extends RuleManager> classRule = rule.getClassRule();
			
				try {
					
					Class<?> maClass = Class.forName(classRule.getName());
					Object maClassTest = maClass.newInstance();
					
					Class<?>[] paramTypes = new Class[3];
			        paramTypes[0] = ZamiaProject.class;
			        paramTypes[1] = String.class;
			        paramTypes[2] = ParameterSource.class;
					Method setNameMethod = maClassTest.getClass().getDeclaredMethod(methodName, paramTypes);
					
					Object value = setNameMethod.invoke(maClassTest, zPrj, ruleItem.getId(), ruleItem.getParameterSource());
					return (Pair<Integer, RuleResult>) value;
					
				} catch (SecurityException | ClassNotFoundException | 
						InstantiationException | IllegalAccessException | 
						NoSuchMethodException | IllegalArgumentException | InvocationTargetException e) {
					logger.error("some exception message LaunchRules", e);
					return new Pair<Integer, RuleResult>(-1, null);
					
				}
				
		}
		return new Pair<Integer, RuleResult>(-1, null);
	
	}

}
