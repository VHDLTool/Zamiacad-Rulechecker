package org.zamia.plugin.tool.vhdl;

public class PathReport {

	private String directory;
	
	private String fileName;
	
	public PathReport(String _directory, String _fileName) {
		directory = _directory;
		fileName = _fileName;
	}

	public String getDirectory() {
		return directory;
	}

	public String getFileName() {
		return fileName;
	}

	public String getReportPath() {
		return directory+fileName;
	}
}
