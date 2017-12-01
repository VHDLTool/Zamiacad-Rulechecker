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
import org.zamia.plugin.tool.vhdl.manager.ReportManager.ParameterSource;
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
	 
/*	private String getGenericName(String ruleId)
	{
		if (ruleId.startsWith("STD_"))
		{
			return ruleId;
		}
		else
		{
			String genericId = "GEN_" + ruleId.substring(ruleId.length() - 5);
			return genericId;
		}
	}*/
	 
	private Boolean isImplemented(String parametersFileName) {
		 gennericName = "";

//		 String ruleId = getGenericName(ruleID.getTextContent());
		 
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

			    //recherche la balise ruleId
			    for (int i = 0; i < nbRuleNodes; i++) 
			    {
		            final Element ruleElement = (Element) ruleNodes.item(i);
		            
		            NodeList ruleIDTagList = ruleElement.getElementsByTagName("hb:RuleUID");
		            if (ruleIDTagList != null && ruleIDTagList.getLength() == 1 && ruleIDTagList.item(0).getTextContent().equalsIgnoreCase(ruleID.getTextContent()))
		            {
		            	NodeList ruleGENTagList = ruleElement.getElementsByTagName("hb:RuleGEN");
		            	if (ruleGENTagList != null && ruleGENTagList.getLength() == 1)
		            	{
			            	gennericName = ruleGENTagList.item(0).getTextContent();
			            	if (gennericName.equalsIgnoreCase("")) {return false;}
		            	}
		            	else
		            	{
		            		return false;
		            	}
		            			 
		            	return true;
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

public ParameterSource getParameterSource() {
	if (!implemented) { return null; }
	
	String fileName = ToolManager.getPathFileName("./rule_checker/rc_config_selected_rules.xml");
	File file = new File(fileName);
	if (!file.exists()) { return null; }
	
	final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
	try {
	    final DocumentBuilder builder = factory.newDocumentBuilder();		
	    
	    final Document document= builder.parse(fileName);
	    
	    final Element ruleSet = document.getDocumentElement();
	    
	    final NodeList ruleNodes = ruleSet.getElementsByTagName("hb:Rule");
	    
	    final int nbRuleNodes = ruleNodes.getLength();
	    if (nbRuleNodes < 1) {
			return null;
	    }
	    
	    for (int i = 0; i < nbRuleNodes; i++) {
	        if(ruleNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
	            final Element ruleNodeElement = (Element) ruleNodes.item(i);
	            
	            String ruleIdS = ruleNodeElement.getAttribute("UID");
	            if (ruleIdS.equalsIgnoreCase(ruleID.getTextContent())) {
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
	if (!implemented) { return false; }
	
	String fileName = ToolManager.getPathFileName("./rule_checker/rc_config_selected_rules.xml");
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