package org.zamia.plugin.tool.vhdl.rules.impl;

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

public class RuleSruct {
	
	// rule name in handook
	 public Element name;
	 
	// rule ID in handook
	 public Element ruleID;
	 
	 // is implemented in rule checker
	 public Boolean implemented;
	 
	 // rule ID in rule cheker code
	 public String gennericName;
	 
	 public RuleSruct(Element element, String parametersFileName) {
		 ruleID = ((Element)element.getElementsByTagName("hb:RuleUID").item(0));
         final Element ruleContent = ((Element)element.getElementsByTagName("hb:RuleContent").item(0));
         name = ((Element)ruleContent.getElementsByTagName("hb:Name").item(0));
         implemented = isImplemented(parametersFileName);
		 
	}
	 
	private Boolean isImplemented(String parametersFileName) {
		 gennericName = "";
		 
		 	if (parametersFileName == null || parametersFileName == "") {return false;}
		 	
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    
			try {
			    final DocumentBuilder builder = factory.newDocumentBuilder();		
			    
			    final Document document= builder.parse(parametersFileName);
			    
			    final Element ruleSet = document.getDocumentElement();
			    
			    final NodeList ruleNodes = ruleSet.getElementsByTagName("hb:Rule");
			    
			    final int nbRuleNodes = ruleNodes.getLength();
			    if (nbRuleNodes < 1) {
					return false;
			    }
			    
			    for (int i = 0; i < nbRuleNodes; i++) {
			        if(ruleNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
			            final Element ruleNodeElement = (Element) ruleNodes.item(i);
			            
			            String ruleIdS = ruleNodeElement.getAttribute("UID");
			            if (ruleIdS.equalsIgnoreCase(ruleID.getTextContent())) {
			            	NodeList ruleGenNodeList = ruleNodeElement.getElementsByTagName("hb:RuleGEN");
			            	if (ruleGenNodeList.getLength() < 1) { return false;}
			            	
			            	gennericName = ((Element)ruleGenNodeList.item(0)).getTextContent();
			            	if (gennericName.equalsIgnoreCase("")) {return false;}
			            	
			            	// TODO recup param
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

@Override
public String toString() {
	return "gennericName "+gennericName+ " implemented "+implemented;
}



public boolean isSelected() {
	if (!implemented) { return false; }
	
	String fileName = ToolManager.getPathFileName("/rule_checker/rc_config_selected_rules.xml");
	File file = new File(fileName);
	if (!file.exists()) { return false; }
	
	final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
	try {
	    final DocumentBuilder builder = factory.newDocumentBuilder();		
	    
	    final Document document= builder.parse(fileName);
	    
	    final Element ruleSet = document.getDocumentElement();
	    
	    final NodeList ruleNodes = ruleSet.getElementsByTagName("hb:Rule");
	    
	    final int nbRuleNodes = ruleNodes.getLength();
	    if (nbRuleNodes < 1) {
			return false;
	    }
	    
	    for (int i = 0; i < nbRuleNodes; i++) {
	        if(ruleNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
	            final Element ruleNodeElement = (Element) ruleNodes.item(i);
	            
	            String ruleIdS = ruleNodeElement.getAttribute("UID");
	            if (ruleIdS.equalsIgnoreCase(ruleID.getTextContent())) {
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