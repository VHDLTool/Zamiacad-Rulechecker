package org.zamia.plugin.tool.vhdl.tools;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_AR_6;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_AR_7;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_CLK_PRJ;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_CNT_PROC;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_COMB_INPUT;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_FN_15;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_FN_18;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_FN_19;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_FN_20;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_FN_22;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_IO_PRJ;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_LOGICAL_CONE;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_OBJ_ID;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_REG_PRJ;
import org.zamia.plugin.tool.vhdl.tools.impl.Tool_RST_PRJ;

	public enum ToolE {
		
		//tool
		REQ_FEAT_FN15("REQ_FEAT_FN15", Tool_FN_15.class, RuleTypeE.IDE, false, "Clock Identification"),
		REQ_FEAT_FN18("REQ_FEAT_FN18", Tool_FN_18.class, RuleTypeE.IDE, false, "Reset Identification"),
		REQ_FEAT_FN19("REQ_FEAT_FN19", Tool_FN_19.class, RuleTypeE.IDE, false, "Package Library Identification"),
		REQ_FEAT_FN20("REQ_FEAT_FN20", Tool_FN_20.class, RuleTypeE.IDE, false, "Line Counter"),
		REQ_FEAT_FN22("REQ_FEAT_FN22", Tool_FN_22.class, RuleTypeE.IDE, false, "Clock domain change"),
		
		
		REQ_FEAT_AR6("REQ_FEAT_AR6", Tool_AR_6.class, RuleTypeE.IDE, false, "Clock Mix Edges"),
		REQ_FEAT_AR7("REQ_FEAT_AR7", Tool_AR_7.class, RuleTypeE.IDE, false, "Reset Mix Levels"),

		REQ_FEAT_CNT_PROC("REQ_FEAT_CNT_PROC", Tool_CNT_PROC.class, RuleTypeE.IDE, false, "Number of line per process"),
		REQ_FEAT_CLK_PRJ("REQ_FEAT_CLK_PRJ", Tool_CLK_PRJ.class, RuleTypeE.IDE, false, "Clock Per Project"),
		REQ_FEAT_RST_PRJ("REQ_FEAT_RST_PRJ", Tool_RST_PRJ.class, RuleTypeE.IDE, false, "Reset Per Project"),
		REQ_FEAT_REG_PRJ("REQ_FEAT_REG_PRJ", Tool_REG_PRJ.class, RuleTypeE.IDE, false, "Register Identification"),
		REQ_FEAT_COMB_INPUT("REQ_FEAT_COMB_INPUT", Tool_COMB_INPUT.class, RuleTypeE.IDE, false, "Input of combinational process"),
		REQ_FEAT_IO_PRJ("REQ_FEAT_IO_PRJ", Tool_IO_PRJ.class, RuleTypeE.IDE, false, "Input Output Identification"), 
		REQ_FEAT_OBJ_ID("REQ_FEAT_OBJ_ID", Tool_OBJ_ID.class, RuleTypeE.IDE, false, "Object Identification"), 
		REQ_FEAT_AR3("REQ_FEAT_AR3", Tool_LOGICAL_CONE.class, RuleTypeE.IDE, true, "Logical Cone");

		
		
		// generic rule ID
		private String idReq;
		// class where is implemented rule
		private Class<? extends ToolSelectorManager> className;
		// rule type : Algo or Help
		private RuleTypeE type;
		// if rule is parameterizable
		private boolean rulesParameterization;
		String ruleName;
		
		//Constructeur
		ToolE(String idReq, Class<? extends ToolSelectorManager> className, RuleTypeE type, 
				boolean rulesParameterization, String ruleName){
		    this.idReq = idReq;
		    this.className = className;
		    this.type = type;
		    this.rulesParameterization = rulesParameterization;
		    this.ruleName = ruleName;
		  }
		
		
		public Class<? extends ToolSelectorManager> getClassRule() {
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
			for(ToolE r : ToolE.values()) { 
		        if (r.getIdReq().equalsIgnoreCase(ruleName)) {
		        	return true;
		        }
		     }
			return false;
		}


		public ParameterSource getParameterSource() {
			String fileName = ToolManager.getPathFileName("./rule_checker/rc_config_selected_tools.xml");
			File file = new File(fileName);
			if (!file.exists()) { return null; }
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    
			try {
			    final DocumentBuilder builder = factory.newDocumentBuilder();		
			    
			    final Document document= builder.parse(fileName);
			    
			    final Element ruleSet = document.getDocumentElement();
			    
			    final NodeList ruleNodes = ruleSet.getElementsByTagName("hb:Tool");
			    
			    final int nbRuleNodes = ruleNodes.getLength();
			    if (nbRuleNodes < 1) {
					return null;
			    }
			    
			    for (int i = 0; i < nbRuleNodes; i++) {
			        if(ruleNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
			            final Element ruleNodeElement = (Element) ruleNodes.item(i);
			            
			            String ruleIdS = ruleNodeElement.getAttribute("UID");
			            if (ruleIdS.equalsIgnoreCase(idReq)) {
			            	
				            String parameterSource = ruleNodeElement.getAttribute("ParameterSource");
				            
				            for (ParameterSource p : ParameterSource.values())
				    		{
				            	if (parameterSource.equalsIgnoreCase(p.toString()))
				            	{
				            		return p;
				            	}
				    		}
			            }
			            
			        }				

			    }

			    
			}
			catch (final ParserConfigurationException e) {
			    e.printStackTrace();
			}
			catch (final SAXException e) {
			    e.printStackTrace();
			}
			catch (final IOException e) {
			    e.printStackTrace();
			}

		return null;
		}
		
		
		public boolean isSelected() {
			String fileName = ToolManager.getPathFileName("./rule_checker/rc_config_selected_tools.xml");
			File file = new File(fileName);
			if (!file.exists()) { return false; }
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    
			try {
			    final DocumentBuilder builder = factory.newDocumentBuilder();		
			    
			    final Document document= builder.parse(fileName);
			    
			    final Element ruleSet = document.getDocumentElement();
			    
			    final NodeList ruleNodes = ruleSet.getElementsByTagName("hb:Tool");
			    
			    final int nbRuleNodes = ruleNodes.getLength();
			    if (nbRuleNodes < 1) {
					return false;
			    }
			    
			    for (int i = 0; i < nbRuleNodes; i++) {
			        if(ruleNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
			            final Element ruleNodeElement = (Element) ruleNodes.item(i);
			            
			            String ruleIdS = ruleNodeElement.getAttribute("UID");
			            if (ruleIdS.equalsIgnoreCase(idReq)) {
			            	return true;
			            }
			            
			        }				

			    }

			    
			}
			catch (final ParserConfigurationException e) {
			    e.printStackTrace();
			}
			catch (final SAXException e) {
			    e.printStackTrace();
			}
			catch (final IOException e) {
			    e.printStackTrace();
			}

		return false;
		}
		
	}
