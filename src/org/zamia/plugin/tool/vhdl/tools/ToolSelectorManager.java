package org.zamia.plugin.tool.vhdl.tools;

import java.util.Date;
import java.util.List;

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
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.ClockSignal;
import org.zamia.plugin.tool.vhdl.ClockSource;
import org.zamia.plugin.tool.vhdl.EdgeE;
import org.zamia.plugin.tool.vhdl.HdlArchitecture;
import org.zamia.plugin.tool.vhdl.HdlEntity;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.InputOutput;
import org.zamia.plugin.tool.vhdl.LevelE;
import org.zamia.plugin.tool.vhdl.NodeInfo;
import org.zamia.plugin.tool.vhdl.NodeType;
import org.zamia.plugin.tool.vhdl.NumberReportE;
import org.zamia.plugin.tool.vhdl.PathReport;
import org.zamia.plugin.tool.vhdl.Process;
import org.zamia.plugin.tool.vhdl.Register;
import org.zamia.plugin.tool.vhdl.RegisterInput;
import org.zamia.plugin.tool.vhdl.ResetSignal;
import org.zamia.plugin.tool.vhdl.ResetSource;
import org.zamia.plugin.tool.vhdl.SignalSource;
import org.zamia.plugin.tool.vhdl.manager.ReportManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Use;

public abstract class ToolSelectorManager extends ReportManager {

	public static final Integer WRONG_PARAM = -2;
	public static final Integer NO_BUILD = -3;

	public static final String NAMESPACE_PREFIX = "rc:";
	
	public ToolSelectorManager() {
		
	}	
	
	/**
	 * This method is launch when user click on "launch" button in "tool selector" window. 
	 * Use getXmlParameterFileConfig to used param for the rule.
	 * Use initReportFile to start report.
	 * Use createReportFile to finish report.
	 * Create method "addReport" or "addViolation" to build the report.
	 * And used the method "NewElement" to add info in xml report 
	 * 
	 * @param zPrj The project
	 * @param ruleId The rule identifier
	 * @param parameterSource The source of the parameters
	 * @return number of violation and report fileName 
	 * 
	 */
	public abstract Pair<Integer, String> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource);
	
	protected abstract void addLogContent(Element racine, ParameterSource parameterSource)  throws Exception;
	
	protected Element createFileTypeElement(HdlFile hdlFile)
	{
		Element fileElement = null;
		Element fileNameElement = null;
		Element nblineElement = null;

		fileElement = document.createElement(NAMESPACE_PREFIX + NodeType.FILE.toString());

		fileNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.FILE.toString()+NodeInfo.NAME.toString());
		fileNameElement.setTextContent(hdlFile.getLocalPath());
		fileElement.appendChild(fileNameElement);

		nblineElement = document.createElement(NAMESPACE_PREFIX + NodeType.FILE.toString()+NodeInfo.NB_LINE.toString());
		nblineElement.setTextContent(hdlFile.getNbLine().toString());
		fileElement.appendChild(nblineElement);
		
		return fileElement;
	}
	
	protected Element createEntityTypeElement(HdlEntity entity)
	{
		Element entityElement = document.createElement(NAMESPACE_PREFIX + NodeType.ENTITY.toString());

		Element entityNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
		entityNameElement.setTextContent(entity.getEntity().getId());
		entityElement.appendChild(entityNameElement);

		Element entityLocationElement = document.createElement(NAMESPACE_PREFIX + NodeType.ENTITY.toString()+NodeInfo.LOCATION.toString());
		entityLocationElement.setTextContent(String.valueOf(entity.getEntity().getStartLine()));
		entityElement.appendChild(entityLocationElement);
		
		return entityElement;
	}

	protected Element createLibTypeElement(Use useItem)
	{
		Element libraryElement = document.createElement(NAMESPACE_PREFIX + NodeType.LIBRARY.toString());

		Element libraryNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.LIBRARY.toString()+NodeInfo.NAME.toString());
		libraryNameElement.setTextContent(useItem.getLibId() +"."+ useItem.getPackageId() +(useItem.getItemId() != null ? "."+useItem.getItemId() : ""));
		libraryElement.appendChild(libraryNameElement);
		Element libraryLocElement = document.createElement(NAMESPACE_PREFIX + NodeType.LIBRARY.toString()+NodeInfo.LOCATION.toString());
		libraryLocElement.setTextContent(String.valueOf(useItem.getStartLine()));
		libraryElement.appendChild(libraryLocElement);
		
		return libraryElement;
	}

	protected Element createArchitectureTypeElement(HdlArchitecture architecture)
	{
		Element architectureElement = document.createElement(NAMESPACE_PREFIX + NodeType.ARCHITECTURE.toString());

		Element entityNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString());
		entityNameElement.setTextContent(architecture.getArchitecture().getId());
		architectureElement.appendChild(entityNameElement);

		Element entityLocationElement = document.createElement(NAMESPACE_PREFIX + NodeType.ARCHITECTURE.toString()+NodeInfo.LOCATION.toString());
		entityLocationElement.setTextContent(String.valueOf(architecture.getArchitecture().getStartLine()));
		architectureElement.appendChild(entityLocationElement);
		
		return architectureElement;
	}
	
	protected Element createProcessTypeElement(Process process)
	{
		Element processElement = document.createElement(NAMESPACE_PREFIX + NodeType.PROCESS.toString());

		Element processNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.PROCESS.toString()+NodeInfo.NAME.toString());
		processNameElement.setTextContent(process.getLabel());
		processElement.appendChild(processNameElement);

		Element processLocationElement = document.createElement(NAMESPACE_PREFIX + NodeType.PROCESS.toString()+NodeInfo.LOCATION.toString());
		processLocationElement.setTextContent(String.valueOf(process.getSequentialProcess().getStartLine()));
		processElement.appendChild(processLocationElement);

		Element processNbLineElement = document.createElement(NAMESPACE_PREFIX + NodeType.PROCESS.toString()+NodeInfo.NB_LINE.toString());
		processNbLineElement.setTextContent(String.valueOf(process.getNbLine()));
		processElement.appendChild(processNbLineElement);
		
		Element processIsSynchronousElement = document.createElement(NAMESPACE_PREFIX + NodeType.PROCESS.toString()+NodeInfo.IS_SYNCHRONOUS.toString());
		processIsSynchronousElement.setTextContent(process.isSynchronous() ? "Yes" : "No");
		processElement.appendChild(processIsSynchronousElement);
		
		return processElement;
	}
	
	protected Element createProcessSignalTypeElement(Process process)
	{
		Element processElement = document.createElement(NAMESPACE_PREFIX + NodeType.PROCESS.toString()+NodeInfo.SIGNAL.toString());

		Element processNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.PROCESS.toString()+NodeInfo.NAME.toString());
		processNameElement.setTextContent(process.getLabel());
		processElement.appendChild(processNameElement);

		Element processLocationElement = document.createElement(NAMESPACE_PREFIX + NodeType.PROCESS.toString()+NodeInfo.LOCATION.toString());
		processLocationElement.setTextContent(String.valueOf(process.getSequentialProcess().getStartLine()));
		processElement.appendChild(processLocationElement);
		
		return processElement;
	}
	
	protected Element createResetSourceLevelInfoTypeElement(ResetSource resetSource, LevelE level)
	{
		Element resetSourceLevelInfoElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SOURCE.toString() + NodeInfo.LEVEL.toString() + "Info");
		
		Element resetSourceElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SOURCE.toString());
		resetSourceElement.setTextContent(resetSource.toString());
		resetSourceLevelInfoElement.appendChild(resetSourceElement);

		Element resetSourceLevelElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SOURCE.toString() + NodeInfo.LEVEL.toString());
		resetSourceLevelElement.setTextContent(level.toString());
		resetSourceLevelInfoElement.appendChild(resetSourceLevelElement);
		
		return resetSourceLevelInfoElement;
	}

	protected Element createClockSourceEdgeInfoTypeElement(ClockSource clockSource, EdgeE edge)
	{
		Element clockSourceEdgeInfoElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SOURCE.toString() + NodeInfo.EDGE.toString() + "Info");
		
		Element clockSourceElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SOURCE.toString());
		clockSourceElement.setTextContent(clockSource.toString());
		clockSourceEdgeInfoElement.appendChild(clockSourceElement);

		Element clockSourceEdgeElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SOURCE.toString() + NodeInfo.EDGE.toString());
		clockSourceEdgeElement.setTextContent(edge.toString());
		clockSourceEdgeInfoElement.appendChild(clockSourceEdgeElement);
		
		return clockSourceEdgeInfoElement;
	}

	protected Element createClockSignalTypeElement(ClockSignal clockSignal)
	{
		Element clockSignalElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SIGNAL.toString());

		Element processNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SIGNAL.toString()+NodeInfo.NAME.toString());
		processNameElement.setTextContent(clockSignal.toString());
		clockSignalElement.appendChild(processNameElement);

		Element processLocationElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SIGNAL.toString()+NodeInfo.LOCATION.toString());
		processLocationElement.setTextContent(String.valueOf(clockSignal.getLocation().fLine));
		clockSignalElement.appendChild(processLocationElement);

		Element processEdgeElement = document.createElement(NAMESPACE_PREFIX + NodeType.CLOCK_SIGNAL.toString()+NodeInfo.EDGE.toString());
		processEdgeElement.setTextContent(String.valueOf(clockSignal.getEdge()));
		clockSignalElement.appendChild(processEdgeElement);

		Element processHasSynchroniousResetElement = document.createElement(NAMESPACE_PREFIX + NodeType.PROCESS.toString()+NodeInfo.HAS_ASYNCHRONOUS_RESET.toString());
		processHasSynchroniousResetElement.setTextContent(clockSignal.hasSynchronousReset() ? "Yes" : "No");
		clockSignalElement.appendChild(processHasSynchroniousResetElement);
		
		return clockSignalElement;
	}
	
	protected Element createRegisterIdTypeElement(RegisterInput register, SignalSource signalSource)
	{
		String nodeType = NAMESPACE_PREFIX + NodeType.REGISTER.toString();
		
		Element registerElement = document.createElement(nodeType);

		Element registerTagElement = document.createElement(nodeType+NodeInfo.TAG.toString());
		registerTagElement.setTextContent(register.getTag());
		registerElement.appendChild(registerTagElement);

		Element registerNameElement = document.createElement(nodeType+NodeInfo.NAME.toString());
		registerNameElement.setTextContent(register.toString());
		registerElement.appendChild(registerNameElement);

		Element registerLocationElement = document.createElement(nodeType+NodeInfo.LOCATION.toString());
		registerLocationElement.setTextContent(String.valueOf(register.getLocation().fLine));
		registerElement.appendChild(registerLocationElement);

		Element registerTypeElement = document.createElement(nodeType+NodeInfo.TYPE.toString()+"Declaration");
		registerTypeElement.setTextContent(register.getTypeS());
		registerElement.appendChild(registerTypeElement);

		Element registerTypeSElement = document.createElement(nodeType+NodeInfo.TYPE.toString());
		registerTypeSElement.setTextContent(register.getType().toString());
		registerElement.appendChild(registerTypeSElement);

		Element registerRangeElement = document.createElement(nodeType+NodeInfo.RANGE.toString());
		registerRangeElement.setTextContent(String.valueOf(register.getRangeNb()));
		registerElement.appendChild(registerRangeElement);
			
		if (signalSource != null)
		{
			Element registerSourceElement = document.createElement(nodeType+"Source");
			registerSourceElement.setTextContent(signalSource.toString());
			registerElement.appendChild(registerSourceElement);
			Element registerSourceLocElement = document.createElement(nodeType+"SourceLOC");
			registerSourceLocElement.setTextContent(signalSource.getLocation().toString());
			registerElement.appendChild(registerSourceLocElement);
			Element registerClockSourceElement = document.createElement(nodeType+"ClockSource");
			registerClockSourceElement.setTextContent(signalSource.getClockSource().toString());
			registerElement.appendChild(registerClockSourceElement);
		}
		
		return registerElement;
	}
	
	protected Element createInputTypeElement(RegisterInput register)
	{
		String nodeType = NAMESPACE_PREFIX + NodeType.INPUT.toString();
		
		Element inputElement = document.createElement(nodeType);

		Element registerTagElement = document.createElement(nodeType+NodeInfo.TAG.toString());
		registerTagElement.setTextContent(register.getTag());
		inputElement.appendChild(registerTagElement);

		Element registerNameElement = document.createElement(nodeType+NodeInfo.NAME.toString());
		registerNameElement.setTextContent(register.toString());
		inputElement.appendChild(registerNameElement);

		Element registerLocationElement = document.createElement(nodeType+NodeInfo.LOCATION.toString());
		registerLocationElement.setTextContent(String.valueOf(register.getLocation().fLine));
		inputElement.appendChild(registerLocationElement);

		Element registerTypeElement = document.createElement(nodeType+NodeInfo.TYPE.toString()+"Declaration");
		registerTypeElement.setTextContent(register.getTypeS());
		inputElement.appendChild(registerTypeElement);

		Element registerTypeSElement = document.createElement(nodeType+NodeInfo.TYPE.toString());
		registerTypeSElement.setTextContent(register.getType().toString());
		inputElement.appendChild(registerTypeSElement);

		Element registerRangeElement = document.createElement(nodeType+NodeInfo.RANGE.toString());
		registerRangeElement.setTextContent(String.valueOf(register.getRangeNb()));
		inputElement.appendChild(registerRangeElement);
		
		return inputElement;
	}

	protected Element createResetSignalTypeElement(ResetSignal resetSignal)
	{
		Element resetSignalElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SIGNAL.toString());

		Element resetSignalNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SIGNAL.toString()+NodeInfo.NAME.toString());
		resetSignalNameElement.setTextContent(resetSignal.toString());
		resetSignalElement.appendChild(resetSignalNameElement);

		Element resetSignalLocationElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SIGNAL.toString()+NodeInfo.LOCATION.toString());
		resetSignalLocationElement.setTextContent(String.valueOf(resetSignal.getNode().getLocation().fLine));
		resetSignalElement.appendChild(resetSignalLocationElement);

		Element processLevelElement = document.createElement(NAMESPACE_PREFIX + NodeType.RESET_SIGNAL.toString()+NodeInfo.LEVEL.toString());
		processLevelElement.setTextContent(String.valueOf(resetSignal.getLevel()));
		resetSignalElement.appendChild(processLevelElement);
		
		return resetSignalElement;
	}
	
	protected Element createClockDomainTypeElement(RegisterInput register, SignalSource registerSource)
	{
		Element registerElement = document.createElement(NAMESPACE_PREFIX + NodeType.REGISTER.toString());

		Element registerNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.REGISTER.toString()+NodeInfo.NAME.toString());
		registerNameElement.setTextContent(register.toString());
		registerElement.appendChild(registerNameElement);

		if (register instanceof Register)
		{
			Element registerClockSourceElement = document.createElement(NAMESPACE_PREFIX + NodeType.REGISTER.toString()+NodeInfo.CLOCK_SOURCE.toString()+NodeInfo.TAG.toString());
			registerClockSourceElement.setTextContent(register.getClockSource().getTag());
			registerElement.appendChild(registerClockSourceElement);
		}

		Element registerLocElement = document.createElement(NAMESPACE_PREFIX + NodeType.REGISTER.toString()+NodeInfo.LOCATION.toString());
		registerLocElement.setTextContent(String.valueOf(register.getLocation().fLine));
		registerElement.appendChild(registerLocElement);

		Element registerViolationElement = document.createElement(NAMESPACE_PREFIX + "violationType");
		registerViolationElement.setTextContent(String.valueOf(register.getLocation().fLine));
		if (!register.checkClockDomainChange()) {
			registerViolationElement.setTextContent("clockDomainChange");
		} else {
			registerViolationElement.setTextContent("");
		}
		registerElement.appendChild(registerViolationElement);
		
		if (register instanceof Register && registerSource != null)
		{
			Element registerSourceNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.REGISTER_SOURCE.toString()+NodeInfo.NAME.toString());
			registerSourceNameElement.setTextContent(registerSource.toString());
			registerElement.appendChild(registerSourceNameElement);


			Element registerSourceLocElement = document.createElement(NAMESPACE_PREFIX + NodeType.REGISTER_SOURCE.toString()+NodeInfo.LOCATION.toString());
			registerSourceLocElement.setTextContent(registerSource.getLocation().toString());
			registerElement.appendChild(registerSourceLocElement);

			Element registerSourceClockSourceElement = document.createElement(NAMESPACE_PREFIX + NodeType.REGISTER_SOURCE.toString()+NodeInfo.CLOCK_SOURCE.toString()+NodeInfo.TAG.toString());
			registerSourceClockSourceElement.setTextContent(registerSource.getClockSource().getTag());
			registerElement.appendChild(registerSourceClockSourceElement);
		}
	
		return registerElement;
	}

	protected Element createIOTypeElement(InputOutput inputOutput)
	{
		Element inputOutputElement = document.createElement(NAMESPACE_PREFIX + NodeType.IO.toString());

		Element inputOutputTagElement = document.createElement(NAMESPACE_PREFIX + NodeType.IO.toString()+NodeInfo.TAG.toString());
		inputOutputTagElement.setTextContent(inputOutput.getTag());
		inputOutputElement.appendChild(inputOutputTagElement);

		Element inputOutputNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.IO.toString()+NodeInfo.NAME.toString());
		inputOutputNameElement.setTextContent(inputOutput.getNameS());
		inputOutputElement.appendChild(inputOutputNameElement);

		Element entityLocationElement = document.createElement(NAMESPACE_PREFIX + NodeType.IO.toString()+NodeInfo.DIRECTION.toString());
		entityLocationElement.setTextContent(String.valueOf(inputOutput.getDirection()));
		inputOutputElement.appendChild(entityLocationElement);

		return inputOutputElement;
	}

	protected Element createSignalTypeElement(InputOutput inputOutput)
	{
		Element inputOutputElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString());

		Element inputOutputTagElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString()+NodeInfo.TAG.toString());
		inputOutputTagElement.setTextContent(inputOutput.getTag());
		inputOutputElement.appendChild(inputOutputTagElement);

		Element inputOutputNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString()+NodeInfo.NAME.toString());
		inputOutputNameElement.setTextContent(inputOutput.getNameS());
		inputOutputElement.appendChild(inputOutputNameElement);

		Element inputOutputTypeElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString()+NodeInfo.DIRECTION.toString());
		inputOutputTypeElement.setTextContent(inputOutput.getDirection());
		inputOutputElement.appendChild(inputOutputTypeElement);

		Element inputOutputLocationElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString()+NodeInfo.LOCATION.toString());
		inputOutputLocationElement.setTextContent(String.valueOf(inputOutput.getLocation().fLine));
		inputOutputElement.appendChild(inputOutputLocationElement);

		return inputOutputElement;
	}

	protected Element createSignalTypeElement(RegisterInput register)
	{
		Element registerElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString());

		Element registerTagElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString()+NodeInfo.TAG.toString());
		registerTagElement.setTextContent(register.getTag());
		registerElement.appendChild(registerTagElement);

		Element registerNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString()+NodeInfo.NAME.toString());
		registerNameElement.setTextContent(register.toString());
		registerElement.appendChild(registerNameElement);

		Element registerTypeElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString()+NodeInfo.DIRECTION.toString());
		registerTypeElement.setTextContent(register.getTypeS());
		registerElement.appendChild(registerTypeElement);

		Element registerLocationElement = document.createElement(NAMESPACE_PREFIX + NodeType.ALL.toString()+NodeInfo.LOCATION.toString());
		registerLocationElement.setTextContent(String.valueOf(register.getLocation().fLine));
		registerElement.appendChild(registerLocationElement);

		return registerElement;
	}

	protected Element createEntityArchitectureTypeElement(String fileName, String entityName, String architectureName)
	{
		Element entityElement = document.createElement(NAMESPACE_PREFIX + NodeType.ENTITY.toString());

		Element fileNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.FILE.toString()+NodeInfo.NAME.toString());
		fileNameElement.setTextContent(fileName);
		entityElement.appendChild(fileNameElement);

		Element entityNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.ENTITY.toString()+NodeInfo.NAME.toString());
		entityNameElement.setTextContent(entityName);
		entityElement.appendChild(entityNameElement);

		Element architectureNameElement = document.createElement(NAMESPACE_PREFIX + NodeType.ARCHITECTURE.toString()+NodeInfo.NAME.toString());
		architectureNameElement.setTextContent(architectureName);
		entityElement.appendChild(architectureNameElement);

		return entityElement;
	}

	private void addToolHeader(Document document, Element racine, String ruleId) {

		Element versionElement = document.createElement("rc:ruleCheckerVersion");
		versionElement.setTextContent(ReportManager.getRuleCheckerVersion());
		racine.appendChild(versionElement);

		Element descriptionElement = document.createElement("rc:featureName");
		descriptionElement.setTextContent(ruleId);
		racine.appendChild(descriptionElement);

		Element dateElement = document.createElement("rc:executionDate");

		dateElement.setTextContent((new Date()).toString());
		racine.appendChild(dateElement);
	}

	
	/**
	 * report Tool in xml file
	 * @param tool The tool to dump
	 * @param parameterSource The source of the parameters if any
	 */
	protected String dumpXml(ToolE tool, ParameterSource parameterSource) 
	{
		return dumpXml(tool, parameterSource, NumberReportE.NAN);
	}
	
	/**
	 * report Tool in xml file
	 * @param tool The tool to dump
	 * @param parameterSource The source of the parameters if any
	 * @param numberReport The number of the report
	 */
	protected String dumpXml(ToolE tool, ParameterSource parameterSource, NumberReportE numberReport) 
	{

		/*
		 * Etape 1 : r�cup�ration d'une instance de la classe "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			/*
			 * Etape 2 : cr�ation d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();

			/*
			 * Etape 3 : cr�ation d'un Document
			 */
			document = builder.newDocument();

			/*
			 * Etape 4 : cr�ation de l'Element racine
			 */
			final Element racine = document.createElement(NAMESPACE_PREFIX + "Feature");
			racine.setAttribute("xmlns:rc", "RULECHECKER");
			document.appendChild(racine);		

			addToolHeader(document, racine, tool.getIdReq());

			addLogContent(racine, parameterSource);

			/*
			 *  affichage
			 */
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(document);

			List<String> xmlLogReport = ToolManager.getXmlLogReport("tool");
			if (xmlLogReport.size() != 2) {return "";}
			// fileName for xml log report
			PathReport pathReport = ReportManager.getPathReport(numberReport, xmlLogReport, tool.getIdReq(), tool.getRuleName());

			final StreamResult sortie = new StreamResult(pathReport.getReportPath());

			//prologue
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");			

			//format
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			transformer.transform(source, sortie);	
			
			return numberReport == NumberReportE.NAN? pathReport.getReportPath(): pathReport.getDirectory();
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
		catch (Exception e) {
			e.printStackTrace();
		}
		

		return "";
	}


}
	

