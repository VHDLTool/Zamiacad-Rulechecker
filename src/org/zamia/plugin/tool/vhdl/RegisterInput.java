package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;
import java.util.List;

import org.zamia.SourceLocation;
import org.zamia.vhdl.ast.SequentialSignalAssignment;
import org.zamia.vhdl.ast.TypeDefinitionRecord;
import org.zamia.vhdl.ast.VHDLNode;

public class RegisterInput extends Declaration implements Cloneable {

	protected SourceLocation location;
	
	protected String subName = null;
	
	protected List<String> listOperand;
	
	protected List<SignalSource> listRegisterSource = new ArrayList<SignalSource>();
	
	protected ClockSource clockSource;

	protected ClockSignal clockSignal;

	private List<SignalSource> listSource = new ArrayList<SignalSource>();

	protected List<RegisterInputRead> listReadRegisterInput = new ArrayList<RegisterInputRead>();
	
	protected List<RegisterInputSource> listSourceRegisterInput = new ArrayList<RegisterInputSource>();
	
	protected String tagS = "";

	protected int tagNum = 0;

	private StopConditionE stopCondition = StopConditionE.NAN;

	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

	public boolean isRecord() {
		if (type == RegisterTypeE.RECORD || type == RegisterTypeE.RECORD_PART) {
			return true;
		}
		return false;
	}

	public TypeDefinitionRecord getRecord() {
		return record;
	}

	@Override
	public String toString() {
		if (subName != null) {
			return subName;
		}
		return getName().toString();
	}
	
	
	public void setSubName(String subName) {
		this.subName = subName;
	}

	public void setRange(int i) {
		range = i;
	}

	public SourceLocation getLocation() {
		return location;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj == null) { return false;}
		
		if (! (obj instanceof RegisterInput)) { return false;}
		
		RegisterInput register = (RegisterInput) obj;

		if (! toString().equalsIgnoreCase(register.toString())) { return false;}
		
		return true;
	}
	
	public List<String> getListOperand() {
		return listOperand;
	}

	public void addListOperand(List<String> _listOperand) {
		for (String operand : _listOperand) {
			if (!toString().equalsIgnoreCase(operand)) {
				listOperand.add(operand);
			}
		}
	}


	public boolean isSameSignal(SignalSource signalSource) {
		
		String signalName = (signalSource.toString().indexOf("(") == -1 ? 
				(signalSource.toString().indexOf(".") == -1 ? signalSource.toString() : signalSource.toString().substring(0, signalSource.toString().indexOf("."))) 
				: signalSource.toString().substring(0, signalSource.toString().indexOf("(")));
		if (isVector()) {
			return signalName.equalsIgnoreCase(getVectorName());
		}
		if (isRecord()) {
			return signalName.equalsIgnoreCase(getRecordName());
		}
		return signalName.equalsIgnoreCase(toString());
	}


	public void addRegisterSource(SignalSource signalSource) {
		if (! listRegisterSource.contains(signalSource)) {
			listRegisterSource.add(signalSource);
		}
		
	}

	public List<SignalSource> getListRegisterSource() {
		return listRegisterSource;
	}


	public boolean checkClockDomainChange() {
		for (SignalSource signalSource : listRegisterSource) {
			if (!signalSource.isSameClockSource(clockSource)) {
				System.out.println("checkClockDomainChange");
				System.out.println("signal "+signalSource.getClockSource().getTag()+ " clockSource "+clockSource.getTag());
				return false;
			}
		}
		return true;
	}

	public ClockSource getClockSource() {
		return clockSource;
	}

	public ClockSignal getClockSignal() {
		return clockSignal;
	}
	
	@Override
	public VHDLNode getVhdlNode() {
		return null;
	}

	public void addSource(SignalSource signalSource) {
		if (! listSource.contains(signalSource)) {
			listSource.add(signalSource);
		}

	}

	public List<SignalSource> getSource() {
		return listSource;

	}

	public void clearSource() {
		listSource = new ArrayList<SignalSource>();
	}

	public boolean isSameClock(SignalSource signalSource) {
		if (! getLocation().fSF.equals(signalSource.getLocation().fSF)) { return false;}
		
		for (RegisterInput register : clockSignal.getListRegister()) {
			if (register.toString().equalsIgnoreCase(signalSource.toString())) {
				return true;
			}
			String signalSourceName = (signalSource.toString().indexOf("(") == -1 ? 
					(signalSource.toString().indexOf(".") == -1 ? signalSource.toString() : signalSource.toString().substring(0, signalSource.toString().indexOf("."))) 
					: signalSource.toString().substring(0, signalSource.toString().indexOf("(")));
			String registerName = (register.toString().indexOf("(") == -1 ? 
					(register.toString().indexOf(".") == -1 ? register.toString() : register.toString().substring(0, register.toString().indexOf("."))) 
					: register.toString().substring(0, register.toString().indexOf("(")));
			if (registerName.equalsIgnoreCase(signalSourceName)) {
				return true;
			}

		}
		return false;
	}

	public int addReadRegisterInput(RegisterInputRead registerInputRead) {
		if (!listReadRegisterInput.contains(registerInputRead)) {
			this.listReadRegisterInput.add(registerInputRead);
			return this.listReadRegisterInput.size();
		}
		return 0;
	}
	
	public void clearListReadRegisterInput() {
		listReadRegisterInput = new ArrayList<RegisterInputRead>();
	}

	public List<RegisterInputRead> getListReadRegisterInput() {
		return listReadRegisterInput;
	}

	public List<RegisterInputRead> getListReadRegisterInputClone() {
		List<RegisterInputRead> clone = new ArrayList<RegisterInputRead>(listReadRegisterInput.size());
	    for(RegisterInputRead item: listReadRegisterInput)
			try {
				clone.add((RegisterInputRead) item.clone());
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    return clone;
	}

	public void updateListReadRegisterInput(
			List<RegisterInputRead> listReadRegisterInputToUsed) {
		for (RegisterInputRead registerInputReadUsed : listReadRegisterInputToUsed) {
			listReadRegisterInput.remove(registerInputReadUsed);
			listReadRegisterInput.add(registerInputReadUsed);
		}
	}

	public boolean ListReadRegisterInputContains(
			SequentialSignalAssignment assign) {
		for (RegisterInputRead register : listReadRegisterInput) {
			if (register.getTarget() != null) {
				if (register.getTarget().getName().toString().equalsIgnoreCase(assign.getTarget().getName().toString())) {
					return true;
				}
			}
		}
		return false;
	}

	public String getTag() {
		return tagS + tagNum;
	}

	public int getTagNum() {
		return tagNum;
	}

	public void setTagNum(int num) {
		tagNum = num;
		
	}

	public void addSourceRegisterInput(RegisterInputSource registerInputSource) {
		if (!listSourceRegisterInput.contains(registerInputSource)) {
			this.listSourceRegisterInput.add(registerInputSource);
		}
	}

	public void clearListSourceRegisterInput() {
		listSourceRegisterInput = new ArrayList<RegisterInputSource>();
	}

	public List<RegisterInputSource> getListSourceRegisterInput() {
		return listSourceRegisterInput;
	}

	public void setStopCondition(StopConditionE _stopCondition) {
		stopCondition  = _stopCondition;
	}

	public StopConditionE getStopCondition() {
		return stopCondition;
	}


}
