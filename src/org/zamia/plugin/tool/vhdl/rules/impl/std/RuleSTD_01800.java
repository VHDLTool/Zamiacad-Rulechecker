package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.io.File;
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

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.HdlPrimitive;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.NumberReportE;
import org.zamia.plugin.tool.vhdl.PathReport;
import org.zamia.plugin.tool.vhdl.manager.EntityManager;
import org.zamia.plugin.tool.vhdl.manager.PrimitiveManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.VHDLPackage;
public class RuleSTD_01800 extends RuleManager {

	RuleE rule = RuleE.STD_01800;
	
	@Override
	public Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId) {
				
				String fileName = "";
				
				Map<String, HdlFile> hdlFiles;
				try {
					hdlFiles = EntityManager.getEntity();
					Map<String, HdlPrimitive> hdlListPrimitive = PrimitiveManager.getPrimitive(hdlFiles);
					
					fileName = dumpXmlPrimitive(hdlListPrimitive, rule.getIdReq());

				} catch (EntityException e) {
					logger.error("some exception message RuleSTD_01800", e);
					return new Pair<Integer, String>(RuleManager.NO_BUILD,"");
				}

				return new Pair<Integer, String> (0,fileName);
			}
	
	
	private String dumpXmlPrimitive(Map<String, HdlPrimitive> hdlListPrimitive,
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

			ToolManager.addHeader(document, racine, ruleId, RuleTypeE.HELP, rule.getRuleName());
			
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
						Element useElement = document.createElement("Use");
						primitiveElement.appendChild(useElement);
						Element fileElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
						fileElement.setTextContent(entityLocation.getFirst().getSource().getLocalPath());
						useElement.appendChild(fileElement);
						Element libraryLocationElement = document.createElement(NodeType.LIBRARY.toString()+NodeInfo.LOCATION.toString());
						libraryLocationElement.setTextContent(String.valueOf(entityLocation.getSecond().fLine));
						useElement.appendChild(libraryLocationElement);
					}
				}
				if (!hdlPrimitive.getListVhdlPackage().isEmpty()) {
					for (Pair<VHDLPackage, SourceLocation> vhdlPackageLocation : hdlPrimitive.getListVhdlPackage()) {
						Element useElement = document.createElement("Use");
						primitiveElement.appendChild(useElement);
						Element fileElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
						fileElement.setTextContent(vhdlPackageLocation.getFirst().getSource().getLocalPath());
						useElement.appendChild(fileElement);
						Element libraryLocationElement = document.createElement(NodeType.FILE.toString()+NodeInfo.NAME.toString());
						libraryLocationElement.setTextContent(String.valueOf(vhdlPackageLocation.getSecond().fLine));
						useElement.appendChild(libraryLocationElement);
					}
				}

			}

			/*
			 * Etape 8 : affichage
			 */
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(document);

			List<String> xmlLogReport = ToolManager.getXmlLogReport("rule", RuleTypeE.HELP);

			if (xmlLogReport.size() != 2) {return "";}
			
			PathReport pathReport = getPathReport(NumberReportE.NAN, xmlLogReport, ruleId, rule.getRuleName())	;
			
 			final StreamResult sortie = new StreamResult(new File(pathReport.getReportPath()));

			//prologue
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");			

			//formatage
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			//sortie
			transformer.transform(source, sortie);
			return pathReport.getReportPath();
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
		return "";
	}


}
