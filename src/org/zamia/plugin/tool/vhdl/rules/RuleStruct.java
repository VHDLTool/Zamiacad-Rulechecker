package org.zamia.plugin.tool.vhdl.rules;

public class RuleStruct {

	//rule Name
	private String name;
	
	// rule ID (in handbook)
	private String id;
	
	// rule implemented ID (id implementation)
	private String idImpl;
	
	// type of rule: Algo or Help
	private String Type;
	
	// if the rule is parametrizable
	private String parameter;
	
	// if the rule is implemented
	private String implemented;

	// if the rule is selected by the user
	private boolean select;
	
	// status of the rule : 
	private String status;

	// status of the rule : 
	private String logFile;

	// if the rule is implemented
	private boolean enable;
	
	// status of the rule : 
	private String logFilename;

	
	public RuleStruct(String id, String idImpl, String name, String type, String parameter,
			String implemented, boolean select, String status, String logFile, boolean enable, String logFilename) {
		this.setName(name);
		this.setId(id);
		this.setIdImpl(idImpl);
		this.setType(type);
		this.setParameter(parameter);
		this.setImplemented(implemented);
		this.setSelect(select);
		this.setStatus(status);
		this.setLogFile(logFile);
		this.setEnable(enable);
		this.setLogFilename(logFilename);
	}
	
	@Override
	public String toString() {
		return "name "+name+" id "+id+" idImpl "+idImpl+" Type "+Type
				+" parameter "+parameter+" status "+status+" logFile "+logFile + " logFilename "+logFilename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public String getImplemented() {
		return implemented;
	}

	public void setImplemented(String enableS) {
		this.implemented = enableS;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getIdImpl() {
		return idImpl;
	}

	public void setIdImpl(String idImpl) {
		this.idImpl = idImpl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setStatus(StatusE status) {
		this.status = status.toString();
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getLogFilename() {
		return logFilename;
	}

	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}
	
	
}
