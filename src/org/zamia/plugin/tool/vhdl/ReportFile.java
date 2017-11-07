package org.zamia.plugin.tool.vhdl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.plugin.tool.vhdl.manager.ReportManager;
import org.zamia.plugin.tool.vhdl.manager.ToolManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.RuleTypeE;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Architecture;
import org.zamia.vhdl.ast.Entity;

public class ReportFile {
	private static final String NAMESPACE_PREFIX = "rc:";
	private static final String EMPTY_CONTENT = "N/A";
	private static final String PER_FILE_SUFFIX = "_R1";
	private static final String PER_PROJECT_SUFFIX = "_R2";
	
	public static final String TAG_PROCESS                   = NAMESPACE_PREFIX + "Process";
	public static final String TAG_CLOCK                     = NAMESPACE_PREFIX + "Clock";
	public static final String TAG_CLOCK_BEFORE              = NAMESPACE_PREFIX + "ClockBefore";
	public static final String TAG_CLOCK_AFTER               = NAMESPACE_PREFIX + "ClockAfter";
	public static final String TAG_RESET                     = NAMESPACE_PREFIX + "Reset";
	public static final String TAG_RESET_BEFORE              = NAMESPACE_PREFIX + "ResetBefore";
	public static final String TAG_RESET_AFTER               = NAMESPACE_PREFIX + "ResetAfter";

	public static final String TAG_REGISTER                  = NAMESPACE_PREFIX + "Register";
	public static final String TAG_INSTANCE                  = NAMESPACE_PREFIX + "Instance";
	public static final String TAG_SOURCE_LEVEL              = NAMESPACE_PREFIX + "Level";
	public static final String TAG_SOURCE_TAG                = NAMESPACE_PREFIX + "SourceTag";
	public static final String TAG_SIGNAL_TYPE               = NAMESPACE_PREFIX + "SignalType";
	public static final String TAG_SIGNAL_EDGE               = NAMESPACE_PREFIX + "Edge";
	public static final String TAG_SENSITIVITY               = NAMESPACE_PREFIX + "Sensitivity";
	public static final String TAG_LIBRARY                   = NAMESPACE_PREFIX + "Library";
	public static final String TAG_SONAR_ERROR              = NAMESPACE_PREFIX + "SonarError";
	public static final String TAG_SONAR_MSG              	 = NAMESPACE_PREFIX + "SonarRemediationMsg";
	
	private static final String TAG_NAMESPACE_PREFIX          = "xmlns:rc";
	private static final String TAG_NAMESPACE                 = "RULECHECKER";
	
	private static final String TAG_SCHEMA_INSTANCE_PREFIX    = "xmlns:xsi";
	private static final String TAG_SCHEMA_INSTANCE_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

	private static final String TAG_ROOT_ELEMENT              = NAMESPACE_PREFIX + "ReportRule";
	private static final String TAG_VERSION                   = NAMESPACE_PREFIX + "RuleCheckerVersion";
	private static final String TAG_RULE_ID                   = NAMESPACE_PREFIX + "RuleName";
	private static final String TAG_EXECUTION_DATE            = NAMESPACE_PREFIX + "ExecutionDate";
	
	private static final String TAG_RULE_FAILURE              = NAMESPACE_PREFIX + "RuleFailure";
	private static final String TAG_FILE                      = NAMESPACE_PREFIX + "File";
	private static final String TAG_LINE                      = NAMESPACE_PREFIX + "Line";
	private static final String TAG_ENTITY                    = NAMESPACE_PREFIX + "Entity";
	private static final String TAG_ARCHITECTURE              = NAMESPACE_PREFIX + "Architecture";

	private Rule _rule;
	private Document _document;
	private int _violationCount;
	private RuleResult _currentResult;
	
	public ReportFile(Rule rule) {
		_rule = rule;
		_currentResult = new RuleResult();
	}
	
	public boolean initialize() {
		boolean ok = false;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			_document = builder.newDocument();
			
			Element rootElement = _document.createElement(TAG_ROOT_ELEMENT);
			rootElement.setAttribute(TAG_NAMESPACE_PREFIX, TAG_NAMESPACE);
			rootElement.setAttribute(TAG_SCHEMA_INSTANCE_PREFIX, TAG_SCHEMA_INSTANCE_NAMESPACE);
			_document.appendChild(rootElement);		

			addReportInfo();
			
			ok = true;
		} catch (Exception e) {
			ToolManager.logger.error("Could not initialize XML report for rule %s", e, _rule.getRuleId());
		}
		
		return ok;
	}
	
	public Element addViolation(SourceLocation sourceLocation) {
		return addViolation(sourceLocation, "", "");
	}
	
	public Element addViolation(SourceLocation sourceLocation, Entity entity, Architecture architecture) {
		return addViolation(sourceLocation, entity.getId(), architecture.getId());
	}
	
	public Element addViolation(SourceLocation sourceLocation, String entityId, String architectureId) {
		String fileName = sourceLocation.fSF.getFileName();
		int line = sourceLocation.fLine;
		Element infoElement = addViolation(fileName, line, entityId, architectureId);
		
		return infoElement;
	}
	
	public Element addViolation(String fileName, int line, String entityId, String architectureId) {
		return addViolation("", fileName, line, entityId, architectureId);
	}

	public Element addViolationPerFile(String fileName, int line, String entityId, String architectureId) {
		return addViolation(PER_FILE_SUFFIX, fileName, line, entityId, architectureId);
	}

	public Element addViolationPerProject(String fileName, int line, String entityId, String architectureId) {
		return addViolation(PER_PROJECT_SUFFIX, fileName, line, entityId, architectureId);
	}

	private Element addViolation(String violationSuffix, String fileName, int line, String entityId, String architectureId) {
		++_violationCount;
		
		Element violationElement = _document.createElement(TAG_RULE_FAILURE);
		Element rootElement = _document.getDocumentElement();
		rootElement.appendChild(violationElement);
		
		addElement(TAG_FILE, fileName, violationElement);
		addElement(TAG_LINE, String.valueOf(line), violationElement);
		addElement(TAG_ENTITY, entityId, violationElement);
		addElement(TAG_ARCHITECTURE, architectureId, violationElement);
		
		String tagInfo = NAMESPACE_PREFIX + _rule.getRuleId() + violationSuffix;
		Element infoElement = _document.createElement(tagInfo);
		violationElement.appendChild(infoElement);
		
		_currentResult.addError(fileName, line);
		
		return infoElement;
	}
	
	/*
	 * All generated elements are mandatory (minOccurs="1" maxOccurs="1").
	 * For empty content, N/A is generated. 
	 */
	public void addElement(String tag, String textContent, Element parentElement) {
		Element element = _document.createElement(tag);
		
		if (textContent != null && textContent.length() > 0) {
			element.setTextContent(textContent);
		} else {
			element.setTextContent(EMPTY_CONTENT);
		}
		
		parentElement.appendChild(element);		
	}
	
	public Pair<Integer, RuleResult> save() {
		return save(NumberReportE.NAN);
	}
	
	public Pair<Integer, RuleResult> save(NumberReportE number) {
		Pair<Integer, RuleResult> result = null;
		PathReport pathReport;
		
		if (_violationCount == 0) {
			result = new Pair<Integer, RuleResult> (_violationCount, null);
		} else  {
			pathReport  = getPathReport("rule", number);
			String reportFilePath = pathReport.getReportPath();
			
			try {
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(_document);
	
				StreamResult output = new StreamResult(new File(reportFilePath));
	
				//header
				transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");			
	
				//format
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	
				//output
				transformer.transform(source, output);	
				
				if (number != NumberReportE.NAN)
				{
					reportFilePath = pathReport.getDirectory();
				}
				
				_currentResult.setReportFileName(reportFilePath);
				result = new Pair<Integer, RuleResult> (_violationCount, _currentResult);
			} catch (Exception e) {
				ToolManager.logger.error("Could not save report file for rule %s (exception thrown).", e, _rule.getRuleId());
			}
		}
		
		return result;
	}

	private void addReportInfo() {
		Element rootElement = _document.getDocumentElement();

		// Add Version element.
		Element versionElement = _document.createElement(TAG_VERSION);
		versionElement.setTextContent(ReportManager.getRuleCheckerVersion());
		rootElement.appendChild(versionElement);

		// Add Rule Id element.
		Element ruleElement = _document.createElement(TAG_RULE_ID);
		String ruleId = _rule.getRuleId();
		ruleElement.setTextContent(ruleId);
		rootElement.appendChild(ruleElement);
		
		// Add Date element.
		Element dateElement = _document.createElement(TAG_EXECUTION_DATE);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date now = new Date();
		dateElement.setTextContent(dateFormat.format(now));
		rootElement.appendChild(dateElement);
	}

	private PathReport getPathReport(String ruleOrTool, NumberReportE number) {
		List<String> xmlLogReport = ToolManager.getXmlLogReport(ruleOrTool, RuleTypeE.RULE);
		RuleE ruleE = _rule.getRuleE();
		PathReport pathReport = ReportManager.getPathReport(number, xmlLogReport, _rule.getRuleId(), ruleE.getRuleName());
		return pathReport;
	}
}
