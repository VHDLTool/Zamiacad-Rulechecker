package org.zamia.plugin.tool.vhdl.manager;

import java.io.File;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;

public class SonarQubeReport {
	private class ReportLog {
		private String _vhdlFileName;
		private int _line;
		private String _ruleId;
		
		public ReportLog(String vhdlFileName, int line, String ruleId) {
			_vhdlFileName = vhdlFileName;
			_line = line;
			_ruleId = ruleId;
		}
		
		public String getVhdlFileName() { return _vhdlFileName; }
		public int getLine() { return _line; }
		public String getRuleId() { return _ruleId; }
	}

	private static String ReportFileName = "rc_sonarqube_rule_report.xml";
	
	private static final String TAG_ROOT          = "coverage";
	private static final String TAG_VERSION       = "version";
	private static final String TAG_FILE          = "file";
	private static final String TAG_PATH          = "path";
	private static final String TAG_LINE_TO_COVER = "lineToCover";
	private static final String TAG_LINE          = "lineNumber";
	private static final String TAG_COVERED       = "covered";
	private static final String TAG_RULE          = "rule";

	private SortedMap<String, ReportLog> _keyToReportLogs;
	
	public SonarQubeReport() {
		_keyToReportLogs = new TreeMap<String, ReportLog>();
	}
	
	public void addLogs(String ruleId, RuleResult ruleResult) {
		for (String vhdlFileName : ruleResult.getVhdlFiles()) {
			Set<Integer> lines = ruleResult.getErrorLines(vhdlFileName);
			for (int line : lines) {
				String key = makeKey(vhdlFileName, line);
				if (!_keyToReportLogs.containsKey(key)) {
					ReportLog reportLog = new ReportLog(vhdlFileName, line, ruleId);
					_keyToReportLogs.put(key, reportLog);
				}
			}
		}
	}
	
	public void saveToFile() {
		if (_keyToReportLogs.size() == 0) 
			return;

		Document document = initializeXmlDoc();
		
		String ruleReportDirectoryPath = ToolManager.getRuleReportDirectory();
		String reportFilePath = ruleReportDirectoryPath + "/" + ReportFileName;
		
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);

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
			
		} catch (Exception e) {
			ToolManager.logger.error("Could not save SonarQube report file", e);
		}		
	}
	
	private String makeKey(String vhdlFileName, int line) {
		String key = null;
		
		String name = vhdlFileName.toUpperCase();
		int dotIdx = name.lastIndexOf('.');
		if (dotIdx > 0) {
			name = name.substring(0, dotIdx);
		}
		
		key = String.format("%1$-25s%2$05d", name, line);
		return key;
	}
	
	private Document initializeXmlDoc() {
		Document doc = null;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();

			Element rootElement = doc.createElement(TAG_ROOT);
			rootElement.setAttribute(TAG_VERSION, "1"); // related to SonarQube 
			doc.appendChild(rootElement);		

			String currentVhdlFileName = "";
			Element currentFileElement = null;
			for (String key : _keyToReportLogs.keySet()) {
				ReportLog reportLog = _keyToReportLogs.get(key);
				if (!currentVhdlFileName.equals(reportLog.getVhdlFileName())) {
					currentVhdlFileName = reportLog.getVhdlFileName();
					currentFileElement = doc.createElement(TAG_FILE);
					rootElement.appendChild(currentFileElement);
					currentFileElement.setAttribute(TAG_PATH, currentVhdlFileName);
				}

				Element lineToCoverElement = doc.createElement(TAG_LINE_TO_COVER);
				currentFileElement.appendChild(lineToCoverElement);
				lineToCoverElement.setAttribute(TAG_LINE, String.valueOf(reportLog.getLine()));
				lineToCoverElement.setAttribute(TAG_COVERED, "false");
				lineToCoverElement.setAttribute(TAG_RULE, String.valueOf(reportLog.getRuleId()));
			}
		} catch (Exception e) {
			ToolManager.logger.error("Could not initialize SonarQube report.", e);
		}
		
		return doc;
	}

}
