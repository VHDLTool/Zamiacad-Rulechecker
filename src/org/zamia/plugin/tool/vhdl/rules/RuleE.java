package org.zamia.plugin.tool.vhdl.rules;

import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.plugin.tool.vhdl.rules.impl.gen.RuleGEN_01200;
import org.zamia.plugin.tool.vhdl.rules.impl.gen.RuleGEN_02300;
import org.zamia.plugin.tool.vhdl.rules.impl.gen.RuleGEN_02400;
import org.zamia.plugin.tool.vhdl.rules.impl.gen.RuleGEN_04500;
import org.zamia.plugin.tool.vhdl.rules.impl.gen.RuleGEN_04900;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_00200;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_00300;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_00400;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_01800;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_03600;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_03700;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_03800;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_04500;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_04600;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_04700;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_04800;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_05000;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_05300;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_06800;
import org.zamia.plugin.tool.vhdl.rules.impl.std.RuleSTD_01700;

	public enum RuleE {
		
		//generic
		GEN_01200("GEN_01200", RuleGEN_01200.class, RuleTypeE.ALGO, true, "Identification of process label"),
		GEN_02300("GEN_02300", RuleGEN_02300.class, RuleTypeE.ALGO, false, "Preservation of Clock Name"),
		GEN_02400("GEN_02400", RuleGEN_02400.class, RuleTypeE.ALGO, false, "Preservation of Reset Name"),
		GEN_04500("GEN_04500", RuleGEN_04500.class, RuleTypeE.ALGO, false, "Reset Registers"),
		GEN_04900("GEN_04900", RuleGEN_04900.class, RuleTypeE.ALGO, false, "Use of clock signal"),
		
		// standard
		STD_01800("STD_01800", RuleSTD_01800.class, RuleTypeE.HELP, false, "Primitive isolation"),
		STD_00200("STD_00200", RuleSTD_00200.class, RuleTypeE.ALGO, true, "Name of clock signal"),
		STD_00300("STD_00300", RuleSTD_00300.class, RuleTypeE.ALGO, true, "Name of reset signal"),
		STD_00400("STD_00400", RuleSTD_00400.class, RuleTypeE.ALGO, false, "Label for Process"),
		STD_03600("STD_03600", RuleSTD_03600.class, RuleTypeE.ALGO, false, "Reset Sensitive Level"),
		STD_03700("STD_03700", RuleSTD_03700.class, RuleTypeE.HELP, false, "Reset Assertion and Deassertion"),
		STD_03800("STD_03800", RuleSTD_03800.class, RuleTypeE.ALGO, false, "Synchronous Elements Initialization"),
		STD_04500("STD_04500", RuleSTD_04500.class, RuleTypeE.ALGO, false, "Clock Reassignment"), 
		STD_04600("STD_04600", RuleSTD_04600.class, RuleTypeE.ALGO, true, "Clock domain number in the design"), 
		STD_04700("STD_04700", RuleSTD_04700.class, RuleTypeE.ALGO, true, "Number of clock domains per modules"), 
		STD_04800("STD_04800", RuleSTD_04800.class, RuleTypeE.ALGO, false, "Clock Edge Sensitivity"), 
		STD_05000("STD_05000", RuleSTD_05000.class, RuleTypeE.ALGO, false, "Sensitivity List for Synchronous Processes"), 
		STD_05300("STD_05300", RuleSTD_05300.class, RuleTypeE.ALGO, false, "Sensitivity list for combinational processes"),
		STD_06800("STD_06800", RuleSTD_06800.class, RuleTypeE.ALGO, false, "Unsuitability of signal initialization in declaration section"),
		STD_01700("STD_01700", RuleSTD_01700.class, RuleTypeE.HELP, false, "Entity special ports");

		
		
		// generic rule ID
		private String idReq;
		// class where is implemented rule
		private Class<? extends RuleManager> className;
		// rule type : Algo or Help
		private RuleTypeE type;
		// if rule is parameterizable
		private boolean rulesParameterization;
		String ruleName;
		
		//Constructeur
		RuleE(String idReq, Class<? extends RuleManager> className, RuleTypeE type, 
				boolean rulesParameterization, String ruleName){
		    this.idReq = idReq;
		    this.className = className;
		    this.type = type;
		    this.rulesParameterization = rulesParameterization;
		    this.ruleName = ruleName;
		  }
		
		
		public Class<? extends RuleManager> getClassRule() {
			return className;	
		}
		
		public String getIdReq() {
			return idReq;	
		}
		
		public RuleTypeE getType() {
			return type;	
		}
		
		public boolean isParam() {
			return rulesParameterization;
		}
		
		public String getRuleName() {
			return ruleName;
		}
		
		/**
		 *  to verify if rule is implemented, use this before call valueOf
		 * @param ruleName
		 * @return
		 */
		public static boolean exist(String ruleName) {
			for(RuleE r : RuleE.values()) { 
		        if (r.getIdReq().equalsIgnoreCase(ruleName)) {
		        	return true;
		        }
		     }
			return false;
		}
		
	}
