package org.zamia.plugin.tool.vhdl.rules;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleSruct;

public class RuleService {

	private static RuleService instance;
	
	 private RuleService() {
	        
	    }
	 
	 public static synchronized RuleService getInstance() {
	        if (instance == null) {
	            instance = new RuleService();
	        }

	        return instance;
	    }
	 
	 /**
	  * find rule in handbook defined in file rc_config.xml
	  * @param zPrj
	  * @return
	  */
	public List<RuleStruct> findAllRules(ZamiaProject zPrj) {
		
		List<RuleStruct> listRules = new ArrayList<RuleStruct>();
		
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    
		try {
		    final DocumentBuilder builder = factory.newDocumentBuilder();		
		    
		    // list of handbook files
			  List<String> xmlFileConfig = ToolManager.getXmlHandbookFileConfig(zPrj);
			  
			  // file name verifiers param
			  String parametersFileName = getParametersFile(zPrj);
			  
				try { // test file exist
					new FileReader(parametersFileName);
				}catch (final FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "<html>Parameters file doesn't exist " + parametersFileName +"</html>", "Error",
	                        JOptionPane.ERROR_MESSAGE);
					
					e.printStackTrace();
					return new ArrayList<RuleStruct>();
				}	

			  for (String fichierName : xmlFileConfig) {
				  // foreach handbook file 
				  
				  try { // test file exist
						new FileReader(fichierName);
					}catch (final FileNotFoundException e) {
						JOptionPane.showMessageDialog(null, "<html>handbook file doesn't exist " + fichierName +"</html>", "Error",
		                        JOptionPane.ERROR_MESSAGE);
						
						e.printStackTrace();
						return new ArrayList<RuleStruct>();
					}
				    final Document document= builder.parse(fichierName);
				    
				    final Element ruleSet = document.getDocumentElement();
				    
				    final NodeList ruleNodes = ruleSet.getElementsByTagName("hb:Rule");
				    
				    final int nbRacineNoeuds = ruleNodes.getLength();
				    for (int i = 0; i<nbRacineNoeuds; i++) {
				        if(ruleNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				        	// create rule with info in handbook
				        	RuleSruct rule = new RuleSruct((Element) ruleNodes.item(i), parametersFileName);
			            	boolean exist = RuleE.exist(rule.gennericName);
				            RuleTypeE type = (exist ? RuleE.valueOf(rule.gennericName).getType() : RuleTypeE.NA);
				            String parameter = (exist ? (RuleE.valueOf(rule.gennericName).isParam() ? "Yes": "No") : "NA");
				            String enable = (exist ? "Implemented" : "Not Implemented");
				            String status = (exist ? StatusE.NOT_EXECUTED.toString() : StatusE.NOT_IMPLEPMENTED.toString());
				            listRules.add(new RuleStruct(rule.ruleID.getTextContent(), rule.gennericName, rule.name.getTextContent(), type.toString(), parameter, enable, rule.isSelected(), status, "", exist, ""));
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
		return listRules;
	}
	
	
	/**
	 * get verifier param file name, in rc_config.xml
	 * @param zPrj 
	 * @return
	 */
	public String getParametersFile(ZamiaProject zPrj) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
		    final DocumentBuilder builder = factory.newDocumentBuilder();
		    
		    String fichierName = ResourcesPlugin.getWorkspace().getRoot().findMember("/"+ zPrj.getId()).getLocation().toString()+"\\rule_checker\\rc_config.xml";
		    final Document document= builder.parse(fichierName);
		    
		    final Element racine = document.getDocumentElement();
		    
		    final NodeList parametersNode = racine.getElementsByTagName("verifiers_parameters");
		    if (parametersNode.getLength() < 1) {
				return null;

		    }
		    
		    String fileName = ((Element)parametersNode.item(0)).getTextContent();
		    return ToolManager.getPathFileName(fileName);
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
		return "";	
		

}

}

