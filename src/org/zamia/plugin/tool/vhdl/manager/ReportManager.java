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
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EdgeE;
import org.zamia.plugin.tool.vhdl.LevelE;
import org.zamia.plugin.tool.vhdl.NumberReportE;
import org.zamia.plugin.tool.vhdl.PathReport;
import org.zamia.plugin.tool.vhdl.rules.IHandbookParam;
import org.zamia.plugin.tool.vhdl.rules.IntParam;
import org.zamia.plugin.tool.vhdl.rules.RangeParam;
import org.zamia.plugin.tool.vhdl.rules.RuleService;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.plugin.tool.vhdl.rules.StringParam;

public abstract class ReportManager {

	private static DocumentBuilder builder;
	private static String _ruleCheckerVersion;
	
	public static final Integer WRONG_PARAM = -2;
	public static final Integer NO_BUILD = -3;

	public enum ParameterSource {HANDBOOK, RULE_CHECKER};
	
	protected static Document document;
	protected static Document documentSecond;
	protected static Document documentThird;

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public ReportManager() {
		
	}
	
	/*
	 * To set the version without loading the ZamiaPlugin bundle.
	 * Used from org.zamia.plugin.Check.main.
	 */
	public static void setRuleCheckerVersion(String ruleCheckerVersion) { 
		_ruleCheckerVersion = ruleCheckerVersion;
	}
	
	public static String getRuleCheckerVersion() { 
		if (_ruleCheckerVersion == null) {
			_ruleCheckerVersion = ZamiaPlugin.getDefault().fVersion;
		}
		return _ruleCheckerVersion; 
	}
	
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

	public static PathReport getPathReport(NumberReportE numberReport, List<String> xmlLogReport, String ruleId, String ruleName) 
	{
		String directory = "";
		String fileName = "";
			directory = xmlLogReport.get(0) + 
					((xmlLogReport.get(1).indexOf(".") == -1) ? (xmlLogReport.get(1) + "_" + ruleId + "_" + ruleName) 
							: (xmlLogReport.get(1).substring(0, xmlLogReport.get(1).indexOf(".")) + "_" + ruleId + "_" + ruleName))+ File.separator;
			
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


	/**
	 * Retrieve the parameters for the given rule and from the given source
	 * @param zPrj The Zamia project
	 * @param ruleId The Rule identifier
	 * @param paramSource The source of the parameters
	 * @return The list of parameters, null if a parameter is wrong or no parameter is found
	 */
	protected static List<IHandbookParam> getXmlParameterFileConfig(ZamiaProject zPrj, String ruleId, ParameterSource paramSource) 
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<IHandbookParam> listParameter = new ArrayList<IHandbookParam>();

		try {
		     builder = factory.newDocumentBuilder();		
		    
		    String fichierName = RuleService.getInstance().getParametersFile(zPrj, ruleId, paramSource);
		    Document document;
		    try {
				
		    	document = builder.parse(fichierName);
			} catch (java.io.FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "<html>Parameters file doesn't exist " + fichierName +"</html>", "Error",
                        JOptionPane.ERROR_MESSAGE);

				return null;
			}
		    
		    final Element racine = document.getDocumentElement();
		    
		    final NodeList ruleNode = racine.getElementsByTagName("hb:Rule");
		    
		    final int nbRuleNode = ruleNode.getLength();

		    if (nbRuleNode < 1) {
		    	// pas de param on utilise les param par defaut
				JOptionPane.showMessageDialog(null, "<html>No rule element in parameters file " + fichierName +"</html>", "Error",
                        JOptionPane.ERROR_MESSAGE);

				return null;
		    }
		    
		    //recherche la balise ruleId
		    for (int i = 0; i < nbRuleNode; i++) 
		    {
	            final Element ruleElement = (Element) ruleNode.item(i);
	            
	            NodeList ruleIDTagList = ruleElement.getElementsByTagName("hb:RuleUID");
	            if (ruleIDTagList != null && ruleIDTagList.getLength() == 1 && ruleIDTagList.item(0).getTextContent().equalsIgnoreCase(ruleId))
	            {
	            	NodeList ruleParamsList = ruleElement.getElementsByTagName("hb:RuleParams");
	            	if (ruleParamsList != null && ruleParamsList.getLength() == 1)
	            	{
	            		Element ruleParams = (Element) ruleParamsList.item(0);
	            		int nbParams = ruleParams.getChildNodes().getLength();
	            		for (int j = 0; j < nbParams; j++)
	            		{
	            			Node current = ruleParams.getChildNodes().item(j);
	            			if (current.getNodeType() == Node.ELEMENT_NODE)
	            			{
	            				Element param = (Element) current;
	            				IHandbookParam handbookParameter = null;
	            				switch (param.getTagName())
	    						{
	    						case IntParam.INT_PARAM_TAG:
	    							handbookParameter = new IntParam(param);
	    							break;
	    						case StringParam.STRING_PARAM_TAG:
	    							handbookParameter = new StringParam(param);
	    							break;
	    						case RangeParam.RANGE_PARAM_TAG:
	    							handbookParameter = new RangeParam(param);
	    							break;
	    						}
	            				
	            				if (handbookParameter != null && handbookParameter.isParamValid())
	            				{
	            					listParameter.add(handbookParameter);
	            				}
	            				else
	            				{
									JOptionPane.showMessageDialog(null, "<html>bad parameter type for the parameter "+param.getTagName()+" of rule " + ruleId +"</html>", "Error",
					                        JOptionPane.ERROR_MESSAGE);
									return null;
	            				}
	            			}
	            		}		            		
	            	}
	            	else
	            	{
						JOptionPane.showMessageDialog(null, "<html>No parameters for rule " + ruleId +"</html>", "Error",
		                        JOptionPane.ERROR_MESSAGE);
						return null;
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
}
