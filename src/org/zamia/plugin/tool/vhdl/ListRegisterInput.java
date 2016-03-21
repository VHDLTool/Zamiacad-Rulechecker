package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ListRegisterInput {

	private ArrayList<RegisterInput> listRegister = new ArrayList<RegisterInput>();  // liste des registres
	
	
	public ListRegisterInput() {
		listRegister = new ArrayList<RegisterInput>();
	}
	
	public ArrayList<RegisterInput> getListRegisterInput() {
		return listRegister;
	}
	
	public int addRegisterInput(RegisterInput register) {
		for (RegisterInput registerInput : listRegister) {
			if (registerInput.equals(register)) {
				registerInput.addListOperand(register.getListOperand());
				return register.getTagNum();
			}
		}
		
		
		if (register.isVector()) {
			ArrayList<RegisterInput> newListRegister = new ArrayList<RegisterInput>();
			for (RegisterInput registerItem : listRegister) {
				if (! register.getVectorName().equalsIgnoreCase(registerItem.getVectorName())) {
					newListRegister.add(registerItem);
				} else {
					register.addListOperand(registerItem.getListOperand());
				}
			}
			listRegister = newListRegister;
		} else if (register.isPartOfVector()) {
			Map<Integer, Boolean> listRegisterIndex = new HashMap<Integer,Boolean>();

			for (int i = register.getIndexMin(); i <= register.getIndexMax(); i++) {
				listRegisterIndex.put(i, true);
			}
			
			for (RegisterInput registerItem : listRegister) {
				if (registerItem.getVectorName().equalsIgnoreCase(register.getVectorName())) {
					if (registerItem.isVector()) {
						return register.getTagNum();
					} else {
						for (int i = registerItem.getIndexMin(); i <= registerItem.getIndexMax(); i++) {
							Boolean used = listRegisterIndex.get(i);
							if (used != null) {
								listRegisterIndex.replace(i, false);
							}
						}
					}
				}
			}
			int num = register.getTagNum();
			for(Entry<Integer, Boolean> entry : listRegisterIndex.entrySet()) {

				if (entry.getValue()) {
					RegisterInput newRegister;
					try {
						newRegister = (RegisterInput) register.clone();
						newRegister.setSubName(register.getVectorName()+"("+String.valueOf(entry.getKey())+")");
						newRegister.setRange(1);
						newRegister.setTagNum(num++);
						listRegister.add(newRegister);
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			return num;
		}
		int num = register.getTagNum();
		listRegister.add(register);
		num++;
		return num;
	}

	public boolean isEmpty() {
		return listRegister.isEmpty();
	}
}
