package org.zamia.plugin.tool.vhdl.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.rules.RuleStruct;
import org.zamia.util.Pair;

public class LaunchTools {

	static String methodName = "Launch";
	

	public static Pair<Integer, String> Launch(RuleStruct ruleItem, ZamiaProject zPrj) {

		if (ToolE.exist(ruleItem.getIdImpl())) {
			ToolE rule = ToolE.valueOf(ruleItem.getIdImpl());
			Class<? extends ToolSelectorManager> classRule = rule.getClassRule();
			
				try {
					
					Class<?> maClass = Class.forName(classRule.getName());
					Object maClassTest = maClass.newInstance();
					
					Class<?>[] paramTypes = new Class[2];
			        paramTypes[0] = ZamiaProject.class;
			        paramTypes[1] = String.class;
					Method setNameMethod = maClassTest.getClass().getDeclaredMethod(methodName, paramTypes);
					
					Object value = setNameMethod.invoke(maClassTest, zPrj, ruleItem.getId());
					return (Pair<Integer, String>) value;
					
				} catch (SecurityException | ClassNotFoundException | 
						InstantiationException | IllegalAccessException | 
						NoSuchMethodException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new Pair<Integer, String>(-1,"");
					
				}
				
		}
		return new Pair<Integer, String>(-1,"");
	
	}

}
