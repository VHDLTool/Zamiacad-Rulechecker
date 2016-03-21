package org.zamia.plugin.tool.vhdl;

import java.util.ArrayList;

import org.zamia.SourceLocation;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.Use;
import org.zamia.vhdl.ast.VHDLPackage;

public class HdlPrimitive {
	
	private Use use;

	private ArrayList<Pair<Entity, SourceLocation>> listEntity = new ArrayList<>();;
	
	private ArrayList<Pair<VHDLPackage, SourceLocation>> listVhdlPackage = new ArrayList<>();;
	
	public HdlPrimitive(Use use, Entity entity) {
		this.setUse(use);
		this.setEntity(entity, use.getLocation());
	}

	public HdlPrimitive(Use use, VHDLPackage vhdlPackage) {
		this.setUse(use);
		this.setVhdlPackage(vhdlPackage, use.getLocation());
	}

	public Use getUse() {
		return use;
	}

	private void setUse(Use use) {
		this.use = use;
	}

	public ArrayList<Pair<Entity, SourceLocation>> getListEntity() {
		return listEntity;
	}

	public void setListEntity(ArrayList<Pair<Entity, SourceLocation>> listEntity) {
		this.listEntity = listEntity;
	}

	public void setEntity(Entity entity, SourceLocation sourceLocation) {
		this.listEntity = new ArrayList<Pair<Entity, SourceLocation>>();
		this.listEntity.add(new Pair<Entity, SourceLocation>(entity, sourceLocation));
	}

	public void addEntity(Entity entity, SourceLocation sourceLocation) {
		this.listEntity.add(new Pair<Entity, SourceLocation> (entity, sourceLocation));
	}
	
	public void setVhdlPackage(VHDLPackage vhdlPackage, SourceLocation sourceLocation) {
		this.listVhdlPackage = new ArrayList<Pair<VHDLPackage, SourceLocation>>();
		this.listVhdlPackage.add(new Pair<VHDLPackage, SourceLocation> (vhdlPackage, sourceLocation));
	}

	public void addPackage(VHDLPackage vhdlPackage, SourceLocation sourceLocation) {
		this.listVhdlPackage.add(new Pair<VHDLPackage, SourceLocation> (vhdlPackage, sourceLocation));
	}

	public ArrayList<Pair<VHDLPackage, SourceLocation>> getListVhdlPackage() {
		return listVhdlPackage;
	}

}
