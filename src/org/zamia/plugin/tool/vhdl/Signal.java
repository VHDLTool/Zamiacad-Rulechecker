package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;

import org.zamia.SourceLocation;

public abstract class Signal extends Declaration {

	protected ListRegisterInput listRegister = new ListRegisterInput();
	
	public ArrayList<RegisterInput> getListRegister() {
		return listRegister.getListRegisterInput();
	}

	public int addRegister(Register register) {
		return listRegister.addRegisterInput(register);
	}

	@Override
	public abstract String toString();
	
	public abstract SourceLocation getLocation();
	
}
