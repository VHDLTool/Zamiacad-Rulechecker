package org.zamia.plugin.tool.vhdl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.zamia.vhdl.ast.Use;
import org.zamia.vhdl.ast.VHDLPackage;

public class HdlFile  implements Cloneable {
	
	private File file;

	private ArrayList<HdlEntity> listHdlEntity = new ArrayList<HdlEntity>();
	
	private ArrayList<VHDLPackage> listHdlPackage = new ArrayList<VHDLPackage>();
	
	private Integer nbLine;
	
	private List<Use> uses = new ArrayList<Use>();

	private String localPath;
	
	public HdlFile(File vhdlFile, String localPath) {
		this.setFile(vhdlFile);
		this.localPath = localPath;
		// pb avec le getNumLines
//		nbLine = sourceFile.getNumLines();
		nbLine = 0;
		try {
		FileInputStream fis = new FileInputStream(vhdlFile.getAbsolutePath());
		LineNumberReader l = new LineNumberReader(       
		       new BufferedReader(new InputStreamReader(fis)));
						while ((l.readLine())!=null)
						 {
							nbLine  = l.getLineNumber();
						 }
						l.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}


	public Integer getNbLine() {
		return nbLine;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	

	public ArrayList<HdlEntity> getListHdlEntity() {
		return listHdlEntity;
	}

	public void setListHdlEntity(ArrayList<HdlEntity> listHdlEntity) {
		this.listHdlEntity = listHdlEntity;
	}

	public void addHdlEntityElement(HdlEntity hdlEntity) {
		if (listHdlEntity == null) {
			listHdlEntity = new ArrayList<HdlEntity>();
		}
		this.listHdlEntity.add(hdlEntity);
	}

	public void addHdlPackageElement(VHDLPackage vhdlPackage) {
		if (listHdlPackage == null) {
			listHdlPackage = new ArrayList<VHDLPackage>();
		}
		this.listHdlPackage.add(vhdlPackage);
	}

	public ArrayList<VHDLPackage> getListHdlPackage() {
		return listHdlPackage;
	}


	public List<Use> getLibraries() {
		return uses;
	}


	public void setLibraries(List<Use> uses) {
		this.uses = uses;
	}

	public void addLibrary(Use use) {
		if (uses == null) {
			uses = new ArrayList<Use>();
		}
		if (uses.contains(use)) { return;}
		uses.add(use);
	}

	public void addLibraries(List<Use> uses) {
		this.uses.addAll(uses);
	}


	public void replaceHdlEntityElement(HdlEntity hdlEntities) {
		// TODO Auto-generated method stub
		
	}


	public String getLocalPath() {
		return "." + localPath;
	}
	
	public void clearLibrary() {
		uses = new ArrayList<Use>();
		
	}


	public void clearEntity() {
		listHdlEntity = new ArrayList<HdlEntity>();
		
	}


	public ClockSource isSignalRegister(SignalSource signalSource) {
		
		for (HdlEntity hdlEntity : listHdlEntity) {
			ClockSource clockSourceRegister = hdlEntity.isSignalRegister(signalSource);
			if (clockSourceRegister != null) {
				return clockSourceRegister;
			}
		}
			
		return null;
	}


	
}
