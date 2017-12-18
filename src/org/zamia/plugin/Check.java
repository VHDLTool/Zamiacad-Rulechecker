package org.zamia.plugin;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.zamia.ZamiaLogger;
import org.zamia.plugin.tool.vhdl.manager.ReportManager;

/**
 * The Check class is the RuleChecker entry point when using the RuleChecker from command line.
 */
public final class Check implements IApplication  {

	public Object start(IApplicationContext context) throws Exception {
		String vhdlProjectDirectory = ".";
		String version = ZamiaPlugin.getDefault().ruleCheckerVersion;
		
		ZamiaLogger logger = ZamiaLogger.getInstance();
		logger.info("RuleChecker: version=" + version);
		
		try {
			String[] args = (String[])context.getArguments().get("application.args");
			if (args != null && args.length > 0) {
				vhdlProjectDirectory = args[0];
			}
			
			logger.info("RuleChecker: VHDL project directory=" + vhdlProjectDirectory);
			
			RuleCheckerCmd ruleChecker = new RuleCheckerCmd();
			ruleChecker.checkRules(vhdlProjectDirectory);
		} catch (Exception e) {
			logger.error("RuleChecker: exception=", e);
		}
	    
		logger.info("RuleChecker: execution completed");
		
	    return IApplication.EXIT_OK;
	}
	
	public void stop() {
	}
	
	/*
	 * Test purpose within eclipse. 
	 * Debug as Java Application with main class=org.zamia.plugin.Check and arg=vhdlProjectDirectory root.
	 */
	public static void main(String[] args) {
		String vhdlProjectDirectory = ".";
		if (args != null && args.length > 0) {
			vhdlProjectDirectory = args[0];
			
		}
		
		RuleCheckerCmd ruleChecker = new RuleCheckerCmd();
		ReportManager.setRuleCheckerVersion("<VERSION>");
		ruleChecker.checkRules(vhdlProjectDirectory);
	}
}
