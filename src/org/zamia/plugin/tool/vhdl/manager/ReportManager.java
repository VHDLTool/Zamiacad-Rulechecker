package org.zamia.plugin.tool.vhdl.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EdgeE;
import org.zamia.plugin.tool.vhdl.LevelE;
import org.zamia.plugin.tool.vhdl.NumberReportE;
import org.zamia.plugin.tool.vhdl.PathReport;
import org.zamia.plugin.tool.vhdl.rules.RuleService;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.plugin.tool.vhdl.rules.impl.TypeParam;
import org.zamia.util.Pair;

public abstract class ReportManager {

	private static DocumentBuilder builder;

	public static final Integer WRONG_PARAM = -2;
	public static final Integer NO_BUILD = -3;

	protected static Document document;
	protected static Document documentSecond;
	protected static Document documentThird;

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();


	public ReportManager() {
		
	}
	
	/**
	 * This method is launch when user click on "launch" button in "rule selector" or "tool selector" windows. 
	 * Use getXmlParameterFileConfig to used param for the rule.
	 * Use initReportFile to start report.
	 * Use createReportFile to finish report.
	 * Create method "addReport" or "addViolation" to build the report.
	 * And used the method "NewElement" to add info in xml report 
	 * 
	 * @param zPrj
	 * @param ruleId
	 * @return number of violation 
	 * 			//  report fileName 
	 * 
	 */
	public abstract Pair<Integer,String> Launch(ZamiaProject zPrj, String ruleId) ;

	
	protected Element initReportFile(String ruleId, RuleTypeE ruleType, String ruleName) {
		return initReportFile(ruleId, ruleType, ruleName, NumberReportE.NAN);
	}
	protected Element initReportFile(String ruleId, RuleTypeE ruleType, String ruleName, NumberReportE numberReport) {
		/*
		 * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();
			
			final Element racine;
			
			switch (numberReport) {
			case SECOND:
			{
				/*
				 * Etape 3 : création d'un Document
				 */
				documentSecond= builder.newDocument();

				/*
				 * Etape 4 : création de l'Element racine
				 */
				racine = documentSecond.createElement(ruleId);
				documentSecond.appendChild(racine);		

				addHeader(documentSecond, racine, ruleId, ruleType, ruleName);
			}
			break;
			case THIRD:
			{
				/*
				 * Etape 3 : création d'un Document
				 */
				documentThird= builder.newDocument();

				/*
				 * Etape 4 : création de l'Element racine
				 */
				racine = documentThird.createElement(ruleId);
				documentThird.appendChild(racine);		

				addHeader(documentThird, racine, ruleId, ruleType, ruleName);
			}
			break;

			default:
				/*
				 * Etape 3 : création d'un Document
				 */
				document= builder.newDocument();

				/*
				 * Etape 4 : création de l'Element racine
				 */
				racine = document.createElement(ruleId);
				document.appendChild(racine);		

				addHeader(document, racine, ruleId, ruleType, ruleName);

				break;
			}

			return racine;
		}
		catch (final ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;			

	}


	protected void addHeader(Document document, Element racine, String ruleId, RuleTypeE ruleType, String ruleName) {
		ToolManager.addHeader(document, racine, ruleId, ruleType, ruleName);
	}

	protected String createReportFile(String ruleId, String ruleName, RuleTypeE ruleTypeE) {
		String rep;
		if (ruleTypeE == RuleTypeE.IDE) {
			rep = "tool";
		} else {
			rep = "rule";
		}
		return createReportFile(document, ruleId, ruleName, ruleTypeE, rep, NumberReportE.NAN);
	}

	protected String createReportFile(String ruleId, String ruleName, RuleTypeE ruleTypeE, String ruleTool) {
		return createReportFile(document, ruleId, ruleName, ruleTypeE, ruleTool, NumberReportE.NAN);
	}

	protected String createReportFile(String ruleId, String ruleName, RuleTypeE ruleTypeE, String ruleTool, NumberReportE numberReport) {
		switch (numberReport) {
		case FIRST:
			return createReportFile(document, ruleId, ruleName, ruleTypeE, ruleTool, numberReport);
		case SECOND:
			return createReportFile(documentSecond, ruleId, ruleName, ruleTypeE, ruleTool, numberReport);
		case THIRD:
			return createReportFile(documentThird, ruleId, ruleName, ruleTypeE, ruleTool, numberReport);
		default:
			return createReportFile(document, ruleId, ruleName, ruleTypeE, ruleTool, numberReport);
		}
	}

	private String createReportFile(Document document, String ruleId, String ruleName, RuleTypeE ruleTypeE
			, String ruleTool, NumberReportE numberReport) {
		try {
			/*
			 *  affichage
			 */
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(document);

			List<String> xmlLogReport = ToolManager.getXmlLogReport(ruleTool, ruleTypeE);
			if (xmlLogReport.size() != 2) {return "";}

			PathReport pathReport = getPathReport(numberReport, xmlLogReport, ruleId, ruleName);

			final StreamResult sortie = new StreamResult(new File(pathReport.getReportPath()));

			//prologue
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");			

			//format
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			//output
			transformer.transform(source, sortie);	
			
			if (numberReport == NumberReportE.NAN) {
				return pathReport.getReportPath();
			} else {
				return pathReport.getDirectory();
			}
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerException e) {
			//e.printStackTrace();
		}
		return "";			

	}

	public static PathReport getPathReport(NumberReportE numberReport, List<String> xmlLogReport, String ruleId, String ruleName) {
		String directory = "";
		String fileName = "";
			directory = xmlLogReport.get(0) + 
					((xmlLogReport.get(1).indexOf(".") == -1) ? (xmlLogReport.get(1) + "_" + ruleId + "_" + ruleName) 
							: (xmlLogReport.get(1).substring(0, xmlLogReport.get(1).indexOf(".")) + "_" + ruleId + "_" + ruleName))+"/";
			
			// fileName for xml log report
			fileName = 	((xmlLogReport.get(1).indexOf(".") == -1) ? (xmlLogReport.get(1) + "_" + ruleId + "_" + ruleName) 
							: (xmlLogReport.get(1).substring(0, xmlLogReport.get(1).indexOf(".")) + "_" + ruleId + "_" + ruleName)) + numberReport.toString() +".xml";

			if (!new File(directory).exists()) {
			// create directory
			new File(directory).mkdirs();
		}

		return new PathReport(directory, fileName);
	}

	
	protected EdgeE update(EdgeE currentEdge, EdgeE edge) {
		if (currentEdge == EdgeE.NAN) {
			currentEdge = edge;
		} else if (edge == EdgeE.NAN) {
		} else if (currentEdge == edge) {
			//do nothing
		} else {
			currentEdge = EdgeE.BOTH;
		}
		return currentEdge;
	}
	
	protected LevelE update(LevelE currentLevel, LevelE level) {
		if (currentLevel == LevelE.NAN) {
			currentLevel = level;
		} else if (level == LevelE.NAN) {
		} else if (currentLevel == level) {
			//do nothing
		} else {
			currentLevel = LevelE.BOTH;
		}
		return currentLevel;
	}

	
	protected Node NewElement(Document document, String baliseLabel, String baliseInfo) {
		Element element = document.createElement(baliseLabel);
		element.setTextContent(baliseInfo);
		return element;
	}


	protected static List<List<Object>> getXmlParameterFileConfig(ZamiaProject zPrj, String ruleId, List<List<Object>> defaultParam) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<List<Object>> listParameter = new ArrayList<List<Object>>();
		List<Object> param = new ArrayList<Object>(); 
		try {
		     builder = factory.newDocumentBuilder();		
		    
		    String fichierName = RuleService.getInstance().getParametersFile(zPrj);
		    Document document;
		    try {
				
		    	document = builder.parse(fichierName);
			} catch (java.io.FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "<html>Parameters file doesn't exist " + fichierName +"</html>", "Error",
                        JOptionPane.ERROR_MESSAGE);

//				createXmlParameterFileConfig(fichierName, ruleId, defaultParam);
				// pas de param on utilise les param par defaut
				return null;
			}
		    
		    final Element racine = document.getDocumentElement();
		    
		    final NodeList ruleNode = racine.getElementsByTagName("hb:Rule");
		    
		    final int nbRuleNode = ruleNode.getLength();

		    if (nbRuleNode < 1) {
//		    	addRule(racine, ruleId, defaultParam);
//		    	createfile(fichierName);
		    	// pas de param on utilise les param par defaut
				JOptionPane.showMessageDialog(null, "<html>No rule element in parameters file " + fichierName +"</html>", "Error",
                        JOptionPane.ERROR_MESSAGE);

				return null;
		    }
		    
		    //recherche la balise ruleId
		    for (int i = 0; i < nbRuleNode; i++) {
		        if(ruleNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
		            final Element ruleElement = (Element) ruleNode.item(i);
		            
					if (ruleElement.getAttribute("UID").equalsIgnoreCase(ruleId)) {
					    final NodeList ruleParameterNode = ruleElement.getElementsByTagName("hb:RuleParameter");
					    final int nbRuleParameterNode = ruleParameterNode.getLength();

					    if (nbRuleParameterNode < 1) {
//					    	addRuleParameter(ruleElement, defaultParam);
//					    	createfile(fichierName);
					    	// pas de param on utilise les param par defaut
							JOptionPane.showMessageDialog(null, "<html>No parameters for rule " + ruleId +"</html>", "Error",
			                        JOptionPane.ERROR_MESSAGE);

							return null;
					    }
					    
					    //recherche des parametres
					    for (int j = 0; j < nbRuleParameterNode; j++) {
					    	param = new ArrayList<Object>();
					        if(ruleParameterNode.item(j).getNodeType() == Node.ELEMENT_NODE) {
					        	final Element ruleParameterElement = (Element) ruleParameterNode.item(j);
					        	//get paameter name
					        	String paramName = getInfoElement(ruleParameterElement, "name");
					        	param.add(paramName);
					        	String type = getInfoElement(ruleParameterElement, "type");
					        	if (!TypeParam.exist(type)) {
									JOptionPane.showMessageDialog(null, "<html>bad parameter type for the parameter "+paramName+" of rule " + ruleId +", type "+type+" does'nt exist</html>", "Error",
					                        JOptionPane.ERROR_MESSAGE);

					        		return null;
					        	}
					        	
					        	param.add(TypeParam.getClass(type));
					        	//recherche des valeurs des parametres
							    final NodeList valueNode = ruleParameterElement.getElementsByTagName("value");
							    final int nbValueNode = valueNode.getLength();
							    if (nbValueNode < 1) {
									JOptionPane.showMessageDialog(null, "<html>No value for the parameter  "+paramName+" of rule " + ruleId+"</html>", "Error",
					                        JOptionPane.ERROR_MESSAGE);

							    	return null;
							    }
							    
							    //recherche des parametres
							    for (int k = 0; k < nbValueNode; k++) {
							        if(valueNode.item(k).getNodeType() == Node.ELEMENT_NODE) {
							        	final Element valueElement = (Element) valueNode.item(k);
							        	String value = valueElement.getTextContent();
							        	param.add(value);
							        }
							    }
					        }
					        listParameter.add(param);
					    }
					    if (listParameter.isEmpty() || (listParameter.size() != defaultParam.size())) {
					    	// pas de param ou pas assez de param on utilise les param par defaut
							JOptionPane.showMessageDialog(null, "<html>Wrong number of parameters, Rule "+ruleId+" must have "+defaultParam.size()+" parameters</html>", "Error",
			                        JOptionPane.ERROR_MESSAGE);

							return null;
					    } else {
					    	// verif param type
					    	for (int cmptParam = 0; cmptParam < listParameter.size(); cmptParam++) {
					    		List<Object> paramItem = listParameter.get(cmptParam);
					    		List<Object> defaultParamItem = defaultParam.get(cmptParam);
					    		if (paramItem.isEmpty() || paramItem.size() < 3) {
									JOptionPane.showMessageDialog(null, "<html>No value for the parameter "+defaultParamItem.get(0).toString()+" of rule " + ruleId+"</html>", "Error",
					                        JOptionPane.ERROR_MESSAGE);
					    			return null;
					    			}
					    		if (! paramItem.get(0).toString().equalsIgnoreCase(defaultParamItem.get(0).toString())) {
					    			// si le nom du param n'est pas le meme
									JOptionPane.showMessageDialog(null, "<html>Wrong name ("+paramItem.get(0).toString()+") for the parameters "+defaultParamItem.get(0).toString()+" of rule " + ruleId+"</html>", "Error",
					                        JOptionPane.ERROR_MESSAGE);

					    			return null;
					    		} 
					    		if (! paramItem.get(1).equals(defaultParamItem.get(1))) {
									JOptionPane.showMessageDialog(null, "<html>Wrong type for the parameters "+defaultParamItem.get(0).toString()+" of rule " + ruleId+"</html>", "Error",
					                        JOptionPane.ERROR_MESSAGE);

					    			// si le type du param n'est pas le meme
					    			return null;
					    		} 
					    	}
					    	return listParameter;
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
		return listParameter;
	}
	
	private static String getInfoElement(Element ruleElement, String info) {
		final NodeList ruleParameterNameNode = ruleElement.getElementsByTagName(info);
	    final int nbRuleParameterNameNode = ruleParameterNameNode.getLength();

	    if (nbRuleParameterNameNode < 1) {
//	    	addRuleParameter(ruleElement, defaultParam);
//	    	createfile(fichierName);
	    	// pas de param on utilise les param par defaut
			return null;
	    }
    	return((Element)ruleParameterNameNode.item(0)).getTextContent();
	}


}
