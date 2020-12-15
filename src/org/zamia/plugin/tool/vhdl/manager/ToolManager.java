/* 
 * Copyright 2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jan 18, 2009
 */
package org.zamia.plugin.tool.vhdl.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zamia.ExceptionLogger;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlComponentInstantiation;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.HdlSignalAssignment;
import org.zamia.plugin.tool.vhdl.ListClockSource;
import org.zamia.plugin.tool.vhdl.ListResetSource;
import org.zamia.plugin.tool.vhdl.ListUpdateE;
import org.zamia.plugin.tool.vhdl.SignalSource;
import org.zamia.plugin.tool.vhdl.VhdlSignalDeclaration;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.plugin.tool.vhdl.rules.StatusE;
import org.zamia.tool.vhdl.BuildMakeE;
import org.zamia.util.Native;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.AssociationElement;
import org.zamia.vhdl.ast.AssociationList;
import org.zamia.vhdl.ast.ConstantDeclaration;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.FormalPart;
import org.zamia.vhdl.ast.InterfaceDeclaration;
import org.zamia.vhdl.ast.InterfaceList;
import org.zamia.vhdl.ast.Operation;
import org.zamia.vhdl.ast.OperationAggregate;
import org.zamia.vhdl.ast.OperationCompare;
import org.zamia.vhdl.ast.OperationLiteral;
import org.zamia.vhdl.ast.OperationLogic;
import org.zamia.vhdl.ast.OperationMath;
import org.zamia.vhdl.ast.OperationName;
import org.zamia.vhdl.ast.Range;
import org.zamia.vhdl.ast.SequentialIf;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.SignalDeclaration;
import org.zamia.vhdl.ast.VHDLNode;


/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public abstract class ToolManager implements IWorkbenchWindowActionDelegate {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public final static ExceptionLogger el = ExceptionLogger.getInstance();

	protected static IWorkbenchWindow fWindow;

	protected static FileWriter  fichier;

	private static boolean log = false;

	private static boolean logFile = false;

	private static boolean initialization = false;

	protected static Document document;

	protected static ZamiaProject zPrj;

	protected static List<String> listFileToWork;

	protected static List<String> listPathToWork;

	private static Document documentReport;

	protected static Map<String, HdlFile> listHdlFile;

	protected static ListClockSource listClockSource;

	protected static ListResetSource listResetSource;

	protected static ListUpdateE infoComponent = ListUpdateE.NO;

	private static boolean _fromPlugin = true;
	
	/**
	 * init log   don't forget call method close 
	 * @param _log
	 * @param _logFile
	 */
	protected void init(boolean _log, boolean _logFile) {

		try {
			// TODO BGT
			File ff=new File("C:/resultat.txt"); // d�finir l'arborescence
			ff.createNewFile();
			fichier=new FileWriter(ff);
		} catch (IOException e) {
			logger.error("some exception message ToolManager init", e);
		}
		initialization  = true;
		log = _log;
		logFile = _logFile;
	}

	public static void init(ZamiaProject zPrj_) {
		zPrj = zPrj_;
	}

	public static void setFromPlugin(boolean fromPlugin) {
		_fromPlugin = fromPlugin;
	}
	
	protected void close() {
		try {
			fichier.close();
		} catch (IOException e) {
			logger.error("some exception message ToolManager close", e);
		}

	}
	
	/*
	 * 
	 */
	public static ZamiaProject getZamiaProject() {
		return zPrj;
	}

	public static String getZamiaProjectPath() {
		String zamiaProjectPath = "";
		
		if (_fromPlugin) {
			zamiaProjectPath = ResourcesPlugin.getWorkspace().getRoot().findMember("/"+ zPrj.getId()).getLocation().toString();
		} else {
		    File[] files = null;
		    try {
		    	files = zPrj.fBasePath.getFiles();
			    if (files.length > 0) {
			    	String filePath = files[0].getAbsolutePath().replace("\\", "/");
			    	int indexChar = filePath.lastIndexOf("/");
			    	zamiaProjectPath = filePath.substring(0, indexChar);
			    }
		    } catch (Exception e) {
		    	logger.error("Could not get Zamia project path.", e);
		    }
		}
		
	    return zamiaProjectPath;
	}
	
	public static String getConfigFilePath(String fileName) {
		String relativePath = "rule_checker" + "/" + fileName;
		String absolutePath = getZamiaProjectPath() + "/" + relativePath;
		return absolutePath;
	}

	public static String getRuleReportDirectory() {

		String path = getConfigFilePath("reporting") + "/" + "rule";
		return path;
	}
	
	public static String getToolReportDirectory() {

		String path = getConfigFilePath("reporting") + "/" + "tool";
		return path;
	}
	
	public static void deleteDirectory(String ruleTool) {
		List<String> xmlLogReport = ToolManager.getXmlLogReport(ruleTool.toLowerCase(), RuleTypeE.NA);
		if (xmlLogReport == null) { return;}
		String directory = xmlLogReport.get(0);
		if (directory == null) {return;}
		
		File rep = new File(directory);
		
		deleteSubDirectory(rep);
	}

	private static void deleteSubDirectory(File rep) {
		for (File file : rep.listFiles()) {
			if (file.isDirectory()) {
				deleteSubDirectory(file);
			}
			
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {
				logger.error(String.format("Could not delete %s.", file.getPath().replaceAll("\\", "/")), e);
			}
		}
	}

	/**
	 * to log in console and in file
	 * @param string
	 */
	protected static void write(String string) {

		if (log ) {
			logger.info(string);
		}


		if (initialization && logFile ) {
			try {
				fichier.write (string);
				fichier.write ("\n");
			} catch (IOException e) {
				logger.error("some exception message ToolManager write", e);
			}
		}
	}

	/** search a signal name for Clock identification and reset identification
	 * 
	 * 
	 * @param node
	 * @return
	 */
	protected static List<Pair<VHDLNode, String>> searchSignal(VHDLNode node) {
		VHDLNode child;
		List<Pair<VHDLNode, String>> listResult = new ArrayList<Pair<VHDLNode,String>>();
		for (int j=0; j<node.getNumChildren(); j++){
			child = node.getChild(j);
			if (child instanceof OperationCompare) {
				Pair<VHDLNode, String> searchSignalInOpComp = searchSignalInOpComp(child);
				if (searchSignalInOpComp != null) {
					listResult.add(searchSignalInOpComp);
				}
			} else if (child instanceof OperationLogic) {
				int numSubChildren = child.getNumChildren();
				for (int i = 0; i < numSubChildren; i++) {
					VHDLNode subChild = child.getChild(i);
					if (subChild instanceof OperationCompare) {
						Pair<VHDLNode, String> searchSignalInOpComp = searchSignalInOpComp(subChild);
						if (searchSignalInOpComp != null) {
							listResult.add(searchSignalInOpComp);
						}
					}
						
				}
			} else if (child instanceof SequentialIf){
			} else if (child instanceof OperationLogic){
				System.out.println("search RESET node "+child.toString()+" type "+ child.getClass().getSimpleName()+ " location "+child.getLocation());
			}
		}
		return listResult;
	}


	private static Pair<VHDLNode, String> searchSignalInOpComp(VHDLNode child) {
		if (child.getNumChildren() == 2) {
			VHDLNode signal = child.getChild(0);
			if (signal != null && signal instanceof OperationName) {
				VHDLNode value = child.getChild(1);
				if (value != null && value instanceof OperationLiteral) {
					if (value.toString().equals("0") || value.toString().equals("1")) {
						return new Pair<VHDLNode, String>(signal,value.toString());
					}
				} else if (value != null && value instanceof OperationAggregate) {
					value = value.getChild(0).getChild(0);
					if (value.toString().equals("0") || value.toString().equals("1")) {
						return new Pair<VHDLNode, String>(signal,value.toString());
					}
				}
			} else if (signal != null ) {
				System.out.println("signal NOT opName "+signal.toString()+"  type "+signal.getClass().getSimpleName());
			}
		} 
		return null;
	}

	@SuppressWarnings("resource")
	/**
	 * get rc_config.txt to select file vhdl
	 * @param zPrj
	 * @return
	 */
	protected static boolean getFileConfig(ZamiaProject zPrj) {
		boolean fileIsEmpty = true;
		listFileToWork = new ArrayList<>();
		listPathToWork = new ArrayList<>();
		String fichierName = getConfigFilePath("rc_config.txt");
		java.io.File fichier = new java.io.File(fichierName);
		try {
			java.util.Scanner lecteur ;
			lecteur = new Scanner(fichier);

			while (lecteur.hasNextLine()) {
				String chaine = lecteur.nextLine().trim();
				if (chaine.length() == 0 || chaine.startsWith("#")) {
					// comment in rc_config.txt
				} else if (chaine.substring(chaine.length()-1).equalsIgnoreCase("/")) {
					fileIsEmpty = false;
					listPathToWork.add(chaine.trim());
				} else if (chaine.substring(chaine.length()-4).equalsIgnoreCase(".vhd")) {
					fileIsEmpty = false;
					listFileToWork.add(chaine.trim());
				}
			}

		} catch (FileNotFoundException e) {
			ZamiaPlugin.showError(fWindow.getShell(), "Config file doesn't exist.", "Create config file: rc_config.txt.", "Config file doesn't exist.");

			logger.error("some exception message ToolManager getFileConfig", e);
			return false;
		}


		if (fileIsEmpty) {
			ZamiaPlugin.showError(fWindow.getShell(), "Config file is empty.", "Config file is empty.", "Config file is empty.");

			return false;
		}

		return true;

	}

	/**
	 *  to select all parent directories of file
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	protected static List<String> createListFilePath(String filePath) {
		List<String> listFilePath = new ArrayList<String>();
		File current = new File(filePath);
		String tmp="";
		listFilePath.add(current.getPath().replace("\\", "/"));
		
		do{
			tmp = current.getParent().replace("\\", "/");
			current = new File(tmp);
			listFilePath.add(tmp + "/");
			
		}while(!tmp.equals("/"));
		listFilePath.add("/");
		listFilePath.retainAll(listPathToWork); 
		
		return listFilePath;		
		


	}

	/**
	 * get list of handbook filename
	 * @param zPrj
	 * @return list of handbook file name
	 */
	public static List<String> getXmlHandbookFileConfig(ZamiaProject zPrj) {
		List<String> listHandbookFile = new ArrayList<String>();

		try {

			final Element racine = parseXmlFile(zPrj);
			if (racine == null) { return new ArrayList<String>();}
			// search handBook node
			final NodeList handbookNode = racine.getElementsByTagName("handBook");

			if (handbookNode.getLength() < 1) {
				ZamiaPlugin.showError(fWindow.getShell(), "No handbook path.", "Please create.", "No handbook path.");
				return listHandbookFile;

			}

			final NodeList handBookFileNameNode = ((Element)handbookNode.item(0)).getElementsByTagName("handBook_fileName");

			final int nbRacineNoeuds = handBookFileNameNode.getLength();

			for (int i = 0; i<nbRacineNoeuds; i++) {
				if(handBookFileNameNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element configFile = (Element) handBookFileNameNode.item(i);
					// add handbook fileName in a result list
					listHandbookFile.add(configFile.getTextContent());
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

		listHandbookFile = getPathFileName(listHandbookFile);

		return listHandbookFile;
	}

	private static Element parseXmlFile(ZamiaProject zPrj) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		// parse xml file in a document
		String fichierName = ToolManager.getConfigFilePath("rc_config.xml");
		try {

			final Document document= builder.parse(fichierName);
			return document.getDocumentElement();

		} catch (FileNotFoundException e) {
			logger.error("some exception message ToolManager parseXmlFile", e);
			ZamiaPlugin.showError(fWindow.getShell(), "No config file.", "Please create.", "No config file.");
			return null;
		}

	}

	/**
	 * get log report filename and directory
	 * @param type : rule or tool
	 * @return list of handbook file name
	 */
	public static List<String> getXmlLogReport(String type, RuleTypeE ruleType) {

		List<String> xmlLogReport = getXmlLogReport(type);
		String directory = xmlLogReport.get(0);
		switch (ruleType) {
		case ALGO:
			directory += "Algo/";
			break;
		case HELP:
			directory += "Help/";
			break;

		default:
			break;
		}
		xmlLogReport.set(0, directory);


		// verify log reporting directory exist
		if (!new File(directory).exists()) {
			// create directory
			new File(directory).mkdirs();
		}

		return xmlLogReport;

	}

	/**
	 * get log report filename and directory
	 * @param type : rule or tool
	 * @return list of handbook file name
	 */
	public static List<String> getXmlLogReport(String type) {
		List<String> pathFileNameLogReport = new ArrayList<String>();

		try {

			final Element racine = parseXmlFile(zPrj);
			if (racine == null) { return new ArrayList<String>();}

			final NodeList logReportNode = racine.getElementsByTagName("log");

			if (logReportNode.getLength() < 1) {
				ZamiaPlugin.showError(fWindow.getShell(), "No log report file.", "Please create.", "No log report file.");
				return pathFileNameLogReport;

			}

			final NodeList logDirectoryNode = ((Element)logReportNode.item(0)).getElementsByTagName(type+"_directory");

			if (logDirectoryNode.getLength() < 1) {
				ZamiaPlugin.showError(fWindow.getShell(), "No log report file.", "Please create.", "No log report file.");
				return pathFileNameLogReport;
			}

			final Element directory = (Element) logDirectoryNode.item(0);
			pathFileNameLogReport.add(directory.getTextContent().replace("\\","/"));

			final NodeList logFileNameNode = ((Element)logReportNode.item(0)).getElementsByTagName(type+"_fileName");

			if (logFileNameNode.getLength() < 1) {
				ZamiaPlugin.showError(fWindow.getShell(), "No log report file.", "Please create.", "No log report file.");
				return pathFileNameLogReport;
			}

			final Element fileName = (Element) logFileNameNode.item(0);
			pathFileNameLogReport.add(fileName.getTextContent());

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

		pathFileNameLogReport = getPathFileName(pathFileNameLogReport);
		return pathFileNameLogReport;
	}

	public static void updateConfigSelectedRules(Map<String, String> selectedRuleList) {
		String pathFileName = ToolManager.getPathFileName("./rule_checker/rc_config_selected_rules.xml");
		
		File file = new File(pathFileName);
		if (file.exists()) {
			file.delete();
		}
		
		Element racine = initReportXml("config_selected_rules", RuleTypeE.NA);
		
		for (String ruleId : selectedRuleList.keySet()) {
			String paramSource = selectedRuleList.get(ruleId);
			addRuleSelectedXml(racine, ruleId, paramSource);
		}
		
		finishReportXml(pathFileName);
	}
	
	public static void addHeader(Document document, Element racine, String ruleId, RuleTypeE ruleType, String ruleName) {

		Element versionElement = document.createElement("rc:ruleCheckerVersion");
		versionElement.setTextContent(ReportManager.getRuleCheckerVersion());
		racine.appendChild(versionElement);

		Element dateElement = document.createElement("rc:executionDate");
		dateElement.setTextContent((new Date()).toString());
		racine.appendChild(dateElement);

/*		Element descriptionElement = document.createElement("description");
		if (ruleType == null) {
			descriptionElement.setTextContent("config rules selected");
		} else {

			switch (ruleType) {
			case ALGO:
				descriptionElement.setTextContent("violation report for rule "+ruleId);
				break;
			case HELP:
				descriptionElement.setTextContent("report for rule "+ruleId);
				break;
			case IDE:
				descriptionElement.setTextContent("report for rule "+ruleId);
				break;

			default:
				descriptionElement.setTextContent("rule reporting");
				break;
			}
		}
		racine.appendChild(descriptionElement);*/

		if (ruleName.length() != 0) {
			Element nameElement = document.createElement("ruleName");
			nameElement.setTextContent(ruleName);
			racine.appendChild(nameElement);
		}

	}
	
	public static void addAnalyzedListFile(Document document, Element racine) {
		if((document != null) && (racine != null) && (!listHdlFile.isEmpty())) {
			for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
				Element fileNameElement = document.createElement("rc:File");
				fileNameElement.setTextContent(entry.getValue().getLocalPath());
				racine.appendChild(fileNameElement);
			}			
		}		
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow aWindow) {
		fWindow = aWindow;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}


	/**
	 *get absolute or relative path to a file
	 * @param fileName
	 * @return
	 */
	public static String getPathFileName(String fileName) {
		if (fileName.startsWith("$")) {
			String alias = getAliasRootDirectory(fileName);
			fileName = getRootDirectory(alias)+fileName.replace("$"+alias, "");
		}
		
		if (fileName.startsWith(".")) {
			// relative path
			String defaultRootPath = ToolManager.getZamiaProjectPath(); 
			fileName = defaultRootPath + "/" + fileName;
			fileName = fileName.replace("/./", "/");
		} else {
			// absolute path
		}
		return fileName;
	}

	private static String getAliasRootDirectory(String fileName) {
		if (!fileName.contains("/")) {
			return fileName;
		}

		return fileName.substring(1, fileName.indexOf("/"));
	}

	private static String getRootDirectory(String alias) {

		String defaultRootPath =  ToolManager.getZamiaProjectPath(); 
		try {

			final Element racine = parseXmlFile(zPrj);
			if (racine == null) { return defaultRootPath;}
			// search handBook node
			final NodeList rootNode = racine.getElementsByTagName("root_directory");

			if (rootNode.getLength() < 1) {
				return defaultRootPath;
			}

			for (int i = 0; i < rootNode.getLength(); i++) {
				Element rootElement = ((Element)rootNode.item(i));
				final NodeList aliasNode = rootElement.getElementsByTagName("alias");
				if (aliasNode.getLength() < 1) {
					return defaultRootPath;
				}
				if (((Element)aliasNode.item(0)).getTextContent().equalsIgnoreCase(alias)) {
					final NodeList pathNode = rootElement.getElementsByTagName("path");
					if (pathNode.getLength() < 1) {
						return defaultRootPath;
					}
					return ((Element)pathNode.item(0)).getTextContent();
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
		return defaultRootPath;	

	}

	/**
	 *get absolute or relative path to a list of file
	 * @param fileName
	 * @return
	 */
	public static List<String> getPathFileName(List<String> fileName) {
		List<String> listResult = new ArrayList<String>();
		for (String filePath : fileName) {
			listResult.add(getPathFileName(filePath));
		}

		return listResult;
	}


	/**
	 * report IDE rule in xml file
	 * @param racineName 
	 * @param ruleType 
	 * @param hdlFiles
	 * @param ruleId
	 * @return 
	 */
	public static Element initReportXml(String racineName, RuleTypeE ruleType) {
		try {
			/*
			 * Etape 1 : r�cup�ration d'une instance de la classe "DocumentBuilderFactory"
			 */
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			/*
			 * Etape 2 : cr�ation d'un parseur
			 */
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();

			/*
			 * Etape 3 : cr�ation d'un Document
			 */
			documentReport = builder.newDocument();

			/*
			 * Etape 4 : cr�ation de l'Element racine
			 */
			final Element racine = documentReport.createElement(racineName);
			racine.setAttribute("xmlns:rc", "RULECHECKER");
			documentReport.appendChild(racine);		

			addHeader(documentReport, racine, "", ruleType, "");
			
			if(racineName.equals("rc:ruleReporting")) {
				Element inputsElement = documentReport.createElement("rc:Inputs");
				racine.appendChild(inputsElement);
			}

			return racine;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void finishReportXml(String fileName) {
		try {
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(documentReport);

			if(fileName.toLowerCase().contains("rc_report_rule.xml")) {
				Node rootNode = documentReport.getElementsByTagName("rc:Inputs").item(0);
				Element racine = null;
				if((rootNode != null) && (rootNode.getNodeType() == Node.ELEMENT_NODE)) {
					racine = (Element)rootNode;
					addAnalyzedListFile(documentReport, racine);
				}
			}
			
			final StreamResult sortie = new StreamResult(new File(fileName));

			//prologue
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");			

			//format
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			//output
			// verify log reporting directory exist
			transformer.transform(source, sortie);	
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerException e) {
			e.printStackTrace();
		}			
	}

	public static String finishReportXml(RuleTypeE type) {
		String fileName = "";
		List<String> xmlLogReport;
		if (type == RuleTypeE.TOOL) {
			xmlLogReport = getXmlLogReport("tool", RuleTypeE.NA);
		} else {
			xmlLogReport = getXmlLogReport("rule", RuleTypeE.NA);
		}
		if (xmlLogReport.size() != 2) {return "";}
		// fileName for xml log report

		String directory = xmlLogReport.get(0);

		fileName = directory + 
				((xmlLogReport.get(1).indexOf(".") == -1) ? (xmlLogReport.get(1)) 
						: (xmlLogReport.get(1).substring(0, xmlLogReport.get(1).indexOf(".")))) + ".xml";

		if (!new File(directory).exists()) {
			// create directory
			new File(directory).mkdirs();
		}

		finishReportXml(fileName);

		return fileName;
	}

	public static void addReportToolStatusXml(Element racine, String ruleId, StatusE statusE, String fileName) {
		Element ruleElement = documentReport.createElement("rc:Tool");
		ruleElement.setAttribute("UID", ruleId);
		racine.appendChild(ruleElement);

		Element statusElement = documentReport.createElement("rc:status");
		statusElement.setTextContent(statusE.toString());
		ruleElement.appendChild(statusElement);

		if (statusE == StatusE.REPORTED) {
			Element fileNameElement = documentReport.createElement("rc:fileName");
			fileNameElement.setTextContent(fileName);
			ruleElement.appendChild(fileNameElement);
		}
	}

	public static void addReportRuleStatusXml(Element racine, String ruleId, StatusE statusE, Integer nbFailed,
			String fileName) {
		Element ruleElement = documentReport.createElement("rc:Rule");
		ruleElement.setAttribute("UID", ruleId);
		racine.appendChild(ruleElement);

		Element statusElement = documentReport.createElement("rc:status");
		statusElement.setTextContent(statusE.toString());
		ruleElement.appendChild(statusElement);

		if ((statusE == StatusE.REPORTED) || (nbFailed > 0) ) {
			if (nbFailed > 0) {
				Element nbFailedElement = documentReport.createElement("rc:nbFailed");
				nbFailedElement.setTextContent(nbFailed.toString());
				ruleElement.appendChild(nbFailedElement);
			}
			Element fileNameElement = documentReport.createElement("rc:fileName");
			fileNameElement.setTextContent(fileName);
			ruleElement.appendChild(fileNameElement);
		}
	}
	
	public static void addItemSelectedXml(Element racine, String selectedRule, String paramSource, String item) {
		Element ruleElement = documentReport.createElement("hb:"+item);
		ruleElement.setAttribute("UID", selectedRule);
		
		if (!paramSource.isEmpty())
		{
			ruleElement.setAttribute("ParameterSource", paramSource);
		}
		
		racine.appendChild(ruleElement);
	}

	public static void addRuleSelectedXml(Element racine, String selectedRule, String paramSource) {
		addItemSelectedXml(racine, selectedRule, paramSource, "Rule");
	}

	public static void addToolSelectedXml(Element racine, String selectedTool, String paramSource) {
		addItemSelectedXml(racine, selectedTool, paramSource, "Tool");
	}

	protected static ListUpdateE updateInfo(ListUpdateE info) throws EntityException {
		return updateInfo(info, false);
	}

	protected static ListUpdateE updateInfo(ListUpdateE info, boolean fileManager) throws EntityException {
		if (_fromPlugin) {
			if (zPrj.getToolVhdlMgr().getBuildMake() == BuildMakeE.USED) {
				if (info == null) {
					info = ListUpdateE.NO;
				}
			} else if (zPrj.getToolVhdlMgr().getBuildMake() == BuildMakeE.NO) {
				JOptionPane.showMessageDialog(null, "<html>A full project build was requested tu used Rule Checker</html>", "Error",
						JOptionPane.ERROR_MESSAGE);
				throw new EntityException();
			} else {
				updateInfoImpl(fileManager);
				info = ListUpdateE.NO;
			}
		} 
		
		return info;
	}

	protected static void updateInfoImpl(boolean fileManager) {
		if (fileManager) {
			zPrj.getToolVhdlMgr().setBuildMake(BuildMakeE.USED);
			ArchitectureManager.resetInfo();
			ClockSignalReadManager.resetInfo();
			ClockSignalManager.resetInfo();
			ClockSignalSourceManager.resetInfo();
			EntityManager.resetInfo();
			InputCombinationalProcessManager.resetInfo();
			InputOutputManager.resetInfo();
			LogicalConeManager.resetInfo();
			LogicalConeReadManager.resetInfo();
			ProcessManager.resetInfo();
			RegisterAffectationManager.resetInfo();
			RegisterSourceManager.resetInfo();
			ResetSignalReadManager.resetInfo();
			ResetSignalManager.resetInfo();
			ResetSignalSourceManager.resetInfo();
			PackageBodyManager.resetInfo();
            SubProgramManager.resetInfo();
            infoComponent = ListUpdateE.NO;
		}
	}
	
//	public static Map<String, HdlFile> searchCompoment() throws EntityException {
//		
//		ArchitectureManager.getArchitecture();
//
//		if (infoComponent == ListUpdateE.YES) {
//			return listHdlFile;
//		}
//		infoComponent = ListUpdateE.YES;
//System.out.println("searchCompoment");
//		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
//			HdlFile hdlFile = entry.getValue();
//			System.out.println("hdlFile "+hdlFile.getLocalPath());
//			if (hdlFile.getListHdlEntity() != null) {
//				for (HdlEntity hdlEntity : hdlFile.getListHdlEntity()) {
//					System.out.println("hdlEntity "+hdlEntity.getEntity().getId());
//					if (hdlEntity.getListHdlArchitecture() != null) {
//						for (HdlArchitecture hdlArchitecture : hdlEntity.getListHdlArchitecture()) {
//							System.out.println("hdlArchitecture "+hdlArchitecture.getArchitecture().toString());
//							// in each architecture, search component
//							hdlArchitecture.setListComponent(hdlEntity);
//						}
//					}
//				}
//			}
//		}
//		
//		return listHdlFile;
//	}

	public static void searchSignalAssignement() throws EntityException {
			ArchitectureManager.getArchitecture();

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() != null) {
				for (HdlEntity hdlEntity : hdlFile.getListHdlEntity()) {
					if (hdlEntity.getListHdlArchitecture() != null) {
						for (HdlArchitecture hdlArchitecture : hdlEntity.getListHdlArchitecture()) {
							// in each architecture, search component
							hdlArchitecture.setListSignalAssignment(getSignalAssignment(hdlArchitecture.getArchitecture()));
						}
					}
				}
			}
		}

	}

	
	private static ArrayList<HdlSignalAssignment> getSignalAssignment(
			Architecture architecture) {
		ArrayList<HdlSignalAssignment> listSignalAssignment = new ArrayList<HdlSignalAssignment>();
		int numChildren = architecture.getNumChildren();
		VHDLNode child;
		for (int i = 0; i < numChildren; i++) {
			child = architecture.getChild(i);
			if (child instanceof SequentialSignalAssignment) {
				listSignalAssignment.add(new HdlSignalAssignment((SequentialSignalAssignment) child));
			}
		}
		return listSignalAssignment;
	}



	public static List<SignalSource> searchSignalOrigin(String signalName, HdlEntity hdlEntity,
			HdlArchitecture hdlArchitecture, boolean findClockResetSource) {
			
		List<SignalSource> listOrigin = new ArrayList<SignalSource>();
		// cas 1 PORT IN du composant
		int numChildren = hdlEntity.getEntity().getNumChildren();
		VHDLNode child;

		for (int i=0; i < numChildren; i++){
			child = hdlEntity.getEntity().getChild(i);
			if (child instanceof InterfaceList) {

				VHDLNode subChild;
				int numSubChildren = child.getNumChildren();
				for (int numInput=0; numInput < numSubChildren; numInput++){
					subChild = child.getChild(numInput);
					InterfaceDeclaration intDeclaration;
					if (subChild instanceof InterfaceDeclaration) {
						intDeclaration = (InterfaceDeclaration)subChild;
						if (signalName.equalsIgnoreCase(intDeclaration.getId()) &&  (intDeclaration.getDir() == OIDir.IN || intDeclaration.getDir() == OIDir.INOUT) ) {

							List<Pair<HdlComponentInstantiation,Pair<HdlEntity, HdlArchitecture>>> listSearchInstantiationComponent = searchInstantiationComponent(hdlEntity.getEntity());
							if (listSearchInstantiationComponent.isEmpty()) {
								//TOP FPGA
								listOrigin.add( new SignalSource(new VhdlSignalDeclaration(intDeclaration), hdlEntity, hdlArchitecture));
								return listOrigin;
							}
							for (Pair<HdlComponentInstantiation, Pair<HdlEntity, HdlArchitecture>> searchInstantiationComponent : listSearchInstantiationComponent) {
								
								List<Operation> searchSignalConnexion = searchSignalConnexion(searchInstantiationComponent.getFirst(), intDeclaration.getId(), numInput);
								if (!searchSignalConnexion.isEmpty()) {
									for (Operation operation : searchSignalConnexion) {
										List<SignalSource> searchSignalOrigin = searchSignalOrigin(operation.toString(), searchInstantiationComponent.getSecond().getFirst(), 
												searchInstantiationComponent.getSecond().getSecond(), findClockResetSource);
										if (searchSignalOrigin.isEmpty()){
											searchSignalOrigin = searchSignalOrigin(getVectorName(operation.toString()), searchInstantiationComponent.getSecond().getFirst(), 
													searchInstantiationComponent.getSecond().getSecond(), findClockResetSource);
										}
										listOrigin.addAll(searchSignalOrigin);
									}
									
								} else {
									System.out.println("searchSignalConnexion null");
								}
							}
							return listOrigin;
						}
					}
				}
			}

		}
		

		// cas 3 sortie d'un autre composant
		for (HdlArchitecture archiItem : hdlEntity.getListHdlArchitecture()) {
			for (HdlComponentInstantiation componentInst : archiItem.getListComponent()) {

//				logger.debug("componentInst  "+componentInst.getName()+ " location "+componentInst.getComponentInstantiation().getLocation());
				HdlEntity componentEntity = componentInst.getEntity();
//				logger.debug("componentEntity "+componentEntity);
				if (componentEntity != null) {
					componentEntity.setGeneric(componentInst.getComponentInstantiation());
				}
				int numChild = componentInst.getComponentInstantiation().getNumChildren();
				for (int i = 0; i < numChild; i++) {
					child = componentInst.getComponentInstantiation().getChild(i);
					if (child instanceof AssociationList) {

						int numsubChild = child.getNumChildren();
						for (int numOutput = 0; numOutput < numsubChild; numOutput++) {
							VHDLNode subChild = child.getChild(numOutput);
							if (subChild instanceof AssociationElement) {
								VHDLNode opName = subChild.getChild(0);
								if (opName instanceof OperationName) {
									String vectorName = (signalName.toString().indexOf("(") == -1 ? signalName
											: signalName.toString().substring(0, signalName.toString().indexOf("(")));
									if (opName.toString().equalsIgnoreCase(signalName) || opName.toString().equalsIgnoreCase(vectorName)) {
										
										if (componentEntity == null) {

											if (archiItem.isOutputComponent(componentInst.getName(), numOutput)) {
												listOrigin.add(new SignalSource(new VhdlSignalDeclaration(subChild), hdlEntity, archiItem));
												return listOrigin;
											}
										} else {

											InterfaceDeclaration interfaceDeclaration = componentEntity.getInterfaceDeclaration(vectorName);
											if (interfaceDeclaration != null &&
													interfaceDeclaration.getDir() == OIDir.OUT) {

												List<VHDLNode> listSearchOriginIncomponent = componentEntity.searchOriginIncomponent(signalName);
												for (VHDLNode searchOriginIncomponent : listSearchOriginIncomponent) {
													
													if (searchOriginIncomponent != null) {
														listOrigin.add(new SignalSource(new VhdlSignalDeclaration(searchOriginIncomponent), componentEntity, componentInst.getHdlArchi()));
													} else {
														listOrigin.add(new SignalSource(new VhdlSignalDeclaration(interfaceDeclaration), componentEntity, componentInst.getHdlArchi()));
													}
												}
												return listOrigin;
											}
										}
									}
								}
							}
						}
					}
				}
			}

		}

		

			// cas 2 SIGNAL DECLARATION
			for (HdlArchitecture archiItem : hdlEntity.getListHdlArchitecture()) {
				int numChild = archiItem.getArchitecture().getNumChildren();
				VHDLNode childArchi;
				for (int i = 0; i < numChild; i++) {
					childArchi = archiItem.getArchitecture().getChild(i);
					SignalDeclaration signalDecl;
					if (childArchi instanceof SignalDeclaration) {

						signalDecl = (SignalDeclaration) childArchi;
						if (signalName.equalsIgnoreCase(signalDecl.getId())) {
							List<VHDLNode> listSearchOriginIncomponent = hdlEntity.searchOriginIncomponent(signalName);
							for (VHDLNode searchOriginIncomponent : listSearchOriginIncomponent) {
								if (searchOriginIncomponent!= null) {
									listOrigin.add(new SignalSource(new VhdlSignalDeclaration(searchOriginIncomponent), hdlEntity, archiItem));
								}
							}
							return listOrigin;
						}
					}
				}
			}
//		logger.debug("No Origin for signalName "+ signalName+" in hdlEntityItem "+  hdlEntity
//				+" in hdlArchitecture "+ hdlArchitecture);
		return listOrigin;
	}



	protected static List<Operation> searchSignalConnexion(HdlComponentInstantiation searchInstantiationComponent, String portName, int numInput) {
		int numChildren = searchInstantiationComponent.getComponentInstantiation().getNumChildren();
		VHDLNode child;
		List<Operation> actualPart = new ArrayList<Operation>();
		for (int i = 0; i < numChildren; i++) {
			if (!actualPart.isEmpty()) {
				return actualPart;
			}
			child = searchInstantiationComponent.getComponentInstantiation().getChild(i);
			if (child instanceof AssociationList) {
				AssociationList associationList = (AssociationList)child;
				int numAssociation = associationList.getNumChildren();
				VHDLNode association;
				if (numAssociation == 0) {
					continue;
				}
				association = associationList.getChild(0);
				if (association instanceof AssociationElement) {
					if (((AssociationElement) association).getFormalPart() == null) {
						// par position
						if (numInput < numAssociation) {
							AssociationElement associationElement = (AssociationElement) associationList.getChild(numInput);
							actualPart.add(associationElement.getActualPart());
							continue;
						} else {
							continue;
						}
					} else {
						// par label
						for (int j = 0; j < numAssociation; j++) {
							AssociationElement associationElement = (AssociationElement) associationList.getChild(j);
							if (associationElement.getFormalPart().toString().equalsIgnoreCase(portName)) {
								actualPart.add(associationElement.getActualPart());
								continue;
							} else if (associationElement.getFormalPart().toString().startsWith(portName+"(")) {
								actualPart.add(associationElement.getActualPart());
							}
						}
					}
				}
			}
		}
		return actualPart;

	}


	protected static List<Pair<HdlComponentInstantiation,Pair<HdlEntity, HdlArchitecture>>> searchInstantiationComponent(Entity entity) {
		List<Pair<HdlComponentInstantiation,Pair<HdlEntity, HdlArchitecture>>> listResult = new ArrayList<Pair<HdlComponentInstantiation,Pair<HdlEntity,HdlArchitecture>>>();
		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();
			if (hdlFile.getListHdlEntity() == null) { continue;}
			for (HdlEntity hdlEntityItem : hdlFile.getListHdlEntity()) {
				if (hdlEntityItem.getListHdlArchitecture() == null || hdlEntityItem.getEntity().equals(entity)) { continue;}
				for (HdlArchitecture hdlArchitectureItem : hdlEntityItem.getListHdlArchitecture()) {
					if (hdlArchitectureItem.getListComponent() == null) { continue;}
					for (HdlComponentInstantiation hdlComponentItem : hdlArchitectureItem.getListComponent()) {
						if (hdlComponentItem.getName().equalsIgnoreCase(entity.getId())) {
							listResult.add(new Pair<HdlComponentInstantiation,Pair<HdlEntity, HdlArchitecture>>(hdlComponentItem,new Pair<HdlEntity, HdlArchitecture>(hdlEntityItem, hdlArchitectureItem)));
						}
					}
				}
			}
		}

		return listResult;

	}


	protected static int searchSignalInAssociationList(AssociationList assoList,
			String signalName) {

		for (int numInput = 0; numInput < assoList.getNumAssociations(); numInput++) {
			VHDLNode child = assoList.getChild(numInput);
			if (child instanceof AssociationElement) {
				AssociationElement associationElem = (AssociationElement)child;
				if (((FormalPart)associationElem.getChild(1)).toString().equalsIgnoreCase(signalName)) {
					return numInput;
				}

			}
		}
		return -1;

	}


	public static int getnbInput(VHDLNode extChild, AssociationList generic, InterfaceList genericInstantiation) {
		Range range = (Range) extChild;
		
		int opLitInt1 = getInt(range.getLeft(), generic, genericInstantiation);
		int opLitInt2 = Integer.parseInt(range.getRight().toString());
		int nbImput;
		if (opLitInt1 > opLitInt2) {
			nbImput = opLitInt1 - opLitInt2 +1;
		} else {
			nbImput = opLitInt2 - opLitInt1 +1;
		}
		return nbImput;
	}
	public static int getnbInput(VHDLNode extChild, InterfaceList generic, InterfaceList genericInstantiation) {
		Range range = (Range) extChild;
		
		int opLitInt1 = 0;//getInt(range.getLeft(), generic, genericInstantiation);
		int opLitInt2 = Integer.parseInt(range.getRight().toString());
		int nbImput;
		if (opLitInt1 > opLitInt2) {
			nbImput = opLitInt1 - opLitInt2 +1;
		} else {
			nbImput = opLitInt2 - opLitInt1 +1;
		}
		return nbImput;
	}

	private static int getInt(Operation operation, AssociationList generic, InterfaceList genericInstantiation) {
		if (operation instanceof OperationLiteral) {
			return Integer.parseInt(operation.toString());
		} else if (operation instanceof OperationMath) {
			OperationMath opMath = (OperationMath) operation;
			int operandA = operandeToInt(opMath.getOperandA(), generic, genericInstantiation);
			int operandB = operandeToInt(opMath.getOperandB(), generic, genericInstantiation);
			
			switch (opMath.getOp()) {
//			case ABS:
//				return "\"ABS\"";
			case ADD:
				return operandA+operandB;
			case DIV:
				return operandA/operandB;
//			case MOD:
//				return "\"MOD\"";
			case MUL:
				return operandA*operandB;
//			case NEG:
//				return "\"-\"";
//			case POS:
//				return "\"+\"";
			case POWER:
				return (int) Math.pow(operandA,operandB);
//			case REM:
//				return "\"REM\"";
			case SUB:
				return operandA-operandB;
			default :
					return 1;
			}
			
		}
		
		
		return 0;
	}
	


	private static int operandeToInt(Operation operand, AssociationList generic, InterfaceList genericInstantiation) {
		int numChildren = genericInstantiation.getNumChildren();
		for (int i = 0; i < numChildren; i++) {
			InterfaceDeclaration interfDecl = (InterfaceDeclaration)genericInstantiation.getChild(i);
			if (interfDecl.getId().equalsIgnoreCase(operand.toString())) {
				AssociationElement assoElem = (AssociationElement)generic.getChild(i);
				return Integer.parseInt(assoElem.toString());
			}
			
		}
		return Integer.parseInt(operand.toString());
	}
	
	public static String getVectorName(String name) {
		int indexOf = name.indexOf("(");
		if (indexOf == -1) { return name;}
		
		return name.substring(0, indexOf);
	}

	public static int getOp(Operation op, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		if (op instanceof OperationMath) {
			OperationMath opMath = (OperationMath) op;
			Operation operandA = opMath.getOperandA();
			int opA = getOperandInt(operandA, hdlEntity, hdlArchitecture);
			Operation operandB = opMath.getOperandB();
			int opB = getOperandInt(operandB, hdlEntity, hdlArchitecture);
			switch (opMath.getOp()) {
			case ADD:
				return opA + opB;
			case DIV:
				return opA / opB;
			case MUL:
				return opA * opB;
			case SUB:
				return opA - opB;
			default:
				return 0;
			}
		} else if (op instanceof OperationLiteral) {
			return Integer.valueOf(op.toString());
		} else if (op != null) {
			System.out.println("getOp op "+op.toString()+ " type "+op.getClass().getSimpleName()+" loc "+ op.getLocation());
			
		}
		return 0;
	}

	private static int getOperandInt(Operation operand, HdlEntity hdlEntity, HdlArchitecture hdlArchitecture) {
		if (operand instanceof OperationLiteral) {
			return Integer.valueOf(operand.toString());
		} else if (operand instanceof OperationName) {
			int numChildren = hdlArchitecture.getArchitecture().getNumChildren();
			for (int i = 0; i < numChildren; i++) {
				VHDLNode child = hdlArchitecture.getArchitecture().getChild(i);
				if (child instanceof ConstantDeclaration) {
					if (operand.toString().equalsIgnoreCase(((ConstantDeclaration)child).getId())) {
						int numChildren2 = child.getNumChildren();
						for (int j = 0; j < numChildren2; j++) {
							VHDLNode child2 = child.getChild(j);
							if (child2 instanceof OperationLiteral) {
								return Integer.valueOf(child2.toString());
							} else if (child2 instanceof OperationMath) {
								return getOp((OperationMath) child2, hdlEntity, hdlArchitecture);
							}
						}
					}
				}
				
			}
			
			// generic
			numChildren = hdlEntity.getEntity().getNumChildren();
			for (int i = 0; i < numChildren; i++) {
				VHDLNode child = hdlEntity.getEntity().getChild(i);
				if (child instanceof InterfaceList) {
					int numChildren2 = child.getNumChildren();
					for (int j = 0; j < numChildren2; j++) {
						VHDLNode child2 = child.getChild(j);
						if (child2 instanceof InterfaceDeclaration) {
							InterfaceDeclaration interfaceDec = (InterfaceDeclaration) child2;
							if (interfaceDec.getId().equalsIgnoreCase(operand.toString())) {
								Operation value = interfaceDec.getValue();
								if (value instanceof OperationLiteral) {
									return Integer.valueOf(value.toString());
								}
							}
						}
					}
				}
			}
		} else if (operand instanceof OperationMath) {
			return getOp(operand, hdlEntity, hdlArchitecture);
		}
		return 0;
	}

}
