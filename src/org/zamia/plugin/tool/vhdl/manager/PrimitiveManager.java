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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import org.eclipse.jface.action.IAction;
import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.HdlPrimitive;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.VHDLPackage;



/**
 * This action uses the projects managers to print model into the log
 * @author altran
 * 
 */

public class PrimitiveManager extends ToolManager {

	private static boolean log = true;

	private static boolean logFile = true;

	/**
	 * method is called by vhdl tool pull down menu
	 */
	public void run(IAction action) {

		init(log, logFile);

		// get zamia project
		zPrj = ZamiaPlugin.findCurrentProject();
		if (zPrj == null) {
			ZamiaPlugin.showError(fWindow.getShell(), "No project selected.", "Please select a project in the navigator.", "No project selected.");
			return;
		}
		
		init(zPrj);
		

		try {
			EntityManager.getEntity();
			Map<String, HdlPrimitive> hdlListPrimitive = getPrimitive(listHdlFile);
			
			dumpXmlPrimitive(hdlListPrimitive, "REQ_STD_01800");
			logger.info("Rule Checker: tool primitive isolation (REQ_STD_01800) has been executed with success.");
		} catch (EntityException e) {
			logger.error("some exception message PrimitiveManager", e);
		}


		close();
	}


	/**
	 * search all libraries used in vhdl file and search where this library is used 
	 * @param hdlFiles
	 * @return
	 */
	public static Map<String, HdlPrimitive> getPrimitive(Map<String, HdlFile> hdlFiles) {
		Map<String, HdlPrimitive> listPrimitive = new HashMap<String, HdlPrimitive>();

		for(Entry<String, HdlFile> entry : listHdlFile.entrySet()) {
			HdlFile hdlFile = entry.getValue();

			if (hdlFile.getListHdlEntity() != null && !hdlFile.getListHdlEntity().isEmpty()) { 

				for (HdlEntity entity : hdlFile.getListHdlEntity()) {
					for (int i = 0; i < entity.getEntity().getContext().getNumUses(); i++) {
						if (listPrimitive.containsKey(entity.getEntity().getContext().getUse(i).toString())) {
							HdlPrimitive hdlPrimitive = listPrimitive.get(entity.getEntity().getContext().getUse(i).toString());
							hdlPrimitive.addEntity(entity.getEntity(), entity.getEntity().getContext().getUse(i).getLocation());
						} else {
							listPrimitive.put(entity.getEntity().getContext().getUse(i).toString(), new HdlPrimitive(entity.getEntity().getContext().getUse(i), entity.getEntity()));
						}
					}
				}
			}

			if (hdlFile.getListHdlPackage() == null) { continue;}
			if (hdlFile.getListHdlPackage().isEmpty()) { continue;}

			for (VHDLPackage vhdlPackage : hdlFile.getListHdlPackage()) {
				for (int i = 0; i < vhdlPackage.getContext().getNumUses(); i++) {
					if (listPrimitive.containsKey(vhdlPackage.getContext().getUse(i).toString())) {
						HdlPrimitive hdlPrimitive = listPrimitive.get(vhdlPackage.getContext().getUse(i).toString());
						hdlPrimitive.addPackage(vhdlPackage,vhdlPackage.getContext().getUse(i).getLocation());
					} else {
						listPrimitive.put(vhdlPackage.getContext().getUse(i).toString(), new HdlPrimitive(vhdlPackage.getContext().getUse(i), vhdlPackage));
					}
				}
			}
}
		return listPrimitive;
	}


	private void dumpXmlPrimitive(Map<String, HdlPrimitive> hdlListPrimitive,
			String ruleId) {


		/*
		 * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();

			/*
			 * Etape 3 : création d'un Document
			 */
			document = builder.newDocument();

			/*
			 * Etape 4 : création de l'Element racine
			 */
			final Element racine = document.createElement(ruleId);
			document.appendChild(racine);		

			addHeader(document, racine, ruleId, RuleTypeE.HELP, "");

			for(Entry<String, HdlPrimitive> entry : hdlListPrimitive.entrySet()) {
				HdlPrimitive hdlPrimitive = entry.getValue();

				Element primitiveElement = document.createElement("primitive");
				racine.appendChild(primitiveElement);
				
				Element libraryNameElement = document.createElement(NodeType.LIBRARY.toString()+NodeInfo.NAME.toString());
				libraryNameElement.setTextContent( hdlPrimitive.getUse().getLibId() +"."+ 
						hdlPrimitive.getUse().getPackageId() +
						(hdlPrimitive.getUse().getItemId() != null ? "."+hdlPrimitive.getUse().getItemId() : ""));
				primitiveElement.appendChild(libraryNameElement);

				// add libraries
				if (!hdlPrimitive.getListEntity().isEmpty()) {
					for (Pair<Entity, SourceLocation> entityLocation : hdlPrimitive.getListEntity()) {
						Element fileElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
						fileElement.setTextContent(entityLocation.getFirst().getSource().getLocalPath());
						primitiveElement.appendChild(fileElement);
						Element libraryLocationElement = document.createElement(NodeType.LIBRARY.toString()+NodeInfo.LOCATION.toString());
						libraryLocationElement.setTextContent(String.valueOf(entityLocation.getSecond().fLine));
						primitiveElement.appendChild(libraryLocationElement);
					}
				}
				if (!hdlPrimitive.getListVhdlPackage().isEmpty()) {
					for (Pair<VHDLPackage, SourceLocation> vhdlPackageLocation : hdlPrimitive.getListVhdlPackage()) {
						Element fileElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
						fileElement.setTextContent(vhdlPackageLocation.getFirst().getSource().getLocalPath());
						primitiveElement.appendChild(fileElement);
						Element libraryLocationElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
						libraryLocationElement.setTextContent(String.valueOf(vhdlPackageLocation.getSecond().fLine));
						primitiveElement.appendChild(libraryLocationElement);
					}
				}

			}

			/*
			 * Etape 8 : affichage
			 */
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(document);

			List<String> xmlLogReport = getXmlLogReport("tool");
			if (xmlLogReport.size() != 2) {return;}
			final StreamResult sortie = new StreamResult(new File(xmlLogReport.get(0) + 
					((xmlLogReport.get(1).indexOf(".") == -1) ? (xmlLogReport.get(1) + "_" + ruleId) 
							: (xmlLogReport.get(1).substring(0, xmlLogReport.get(1).indexOf(".")) + "_" + ruleId)) + ".xml"));

			//prologue
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");			

			//formatage
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			//sortie
			transformer.transform(source, sortie);	
		}
		catch (final ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerException e) {
			e.printStackTrace();
		}			

	}




}
