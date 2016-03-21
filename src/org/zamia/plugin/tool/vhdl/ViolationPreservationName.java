package org.zamia.plugin.tool.vhdl;

import org.zamia.SourceLocation;

public class ViolationPreservationName {

	private String entityName;
	
	private String archiName;
	
	private String fileName;
	
	private String signalNameBefore;
	
	private String signalNameAfter;
	
	private String composantName;
	
	private String instanceName;
	
	private SourceLocation location;
	
	public ViolationPreservationName(String entityName, String archiName, String fileName, String signalNameBefore,
			String signalNameAfter, String composantName, String instanceName, SourceLocation location) {
		
		this.entityName = entityName;
		this.archiName = archiName;
		this.fileName = fileName;
		this.signalNameBefore = signalNameBefore;
		this.signalNameAfter = signalNameAfter;
		this.composantName = composantName;
		this.instanceName = instanceName;
		this.location = location;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getArchiName() {
		return archiName;
	}

	public String getFileName() {
		return fileName;
	}

	public String getSignalNameBefore() {
		return signalNameBefore;
	}

	public String getSignalNameAfter() {
		return signalNameAfter;
	}

	public String getComposantName() {
		return composantName;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public SourceLocation getLocation() {
		return location;
	}

}
