package org.zamia.plugin.tool.vhdl.rules.impl.std;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.zamia.SourceLocation;
import org.zamia.ZamiaProject;
import org.zamia.plugin.tool.vhdl.EntityException;
import org.zamia.plugin.tool.vhdl.HdlFile;
import org.zamia.plugin.tool.vhdl.HdlPrimitive;
import org.zamia.plugin.tool.vhdl.ReportFile;
import org.zamia.plugin.tool.vhdl.manager.EntityManager;
import org.zamia.plugin.tool.vhdl.manager.PrimitiveManager;
import org.zamia.plugin.tool.vhdl.rules.RuleE;
import org.zamia.plugin.tool.vhdl.rules.RuleResult;
import org.zamia.plugin.tool.vhdl.rules.impl.Rule;
import org.zamia.plugin.tool.vhdl.rules.impl.RuleManager;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.Entity;
import org.zamia.vhdl.ast.Use;
import org.zamia.vhdl.ast.VHDLPackage;

/*
 * Primitive isolation.
 * Primitive are isolated in a wrapper entity defined in a dedicated VHDL file.
 * No parameters.
 */
public class RuleSTD_01800 extends Rule {
	
	private Dictionary<String, ArrayList<SourceLocation>> _libraryInfos;
	
	public RuleSTD_01800() {
		super(RuleE.STD_01800);
	}

	@Override
	public Pair<Integer, RuleResult> Launch(ZamiaProject zPrj, String ruleId, ParameterSource parameterSource) {
		
		initializeRule(parameterSource, ruleId);
		
		//// Make primitive list
		
		Map<String, HdlPrimitive> hdlPrimitiveList;
		try {
			Map<String, HdlFile> hdlFiles = EntityManager.getEntity();
			hdlPrimitiveList = PrimitiveManager.getPrimitive(hdlFiles);

		} catch (EntityException e) {
			LogNeedBuild();
			return new Pair<Integer, RuleResult>(RuleManager.NO_BUILD, null);
		}

		//// Check rule
		
		_libraryInfos = new Hashtable<String, ArrayList<SourceLocation>>();
		
		for(Entry<String, HdlPrimitive> entry : hdlPrimitiveList.entrySet()) {
			HdlPrimitive hdlPrimitive = entry.getValue();
			
			//// get the library
			
			Use use = hdlPrimitive.getUse();
			String libraryFullName = use.getLibId() + "." + use.getPackageId();
			if (use.getItemId() != null) {
				libraryFullName += "." + use.getItemId();
			}
			
			//// get library uses
			
			if (!hdlPrimitive.getListEntity().isEmpty()) {
				for (Pair<Entity, SourceLocation> entity : hdlPrimitive.getListEntity()) {
					SourceLocation location = entity.getSecond();
					addLibraryUse(libraryFullName, location);
				}
			}
			
			if (!hdlPrimitive.getListVhdlPackage().isEmpty()) {
				for (Pair<VHDLPackage, SourceLocation> vhdlPackage : hdlPrimitive.getListVhdlPackage()) {
					SourceLocation location = vhdlPackage.getSecond();
					addLibraryUse(libraryFullName, location);
				}
			}
		}
		
		//// Write report
		
		Pair<Integer, RuleResult> result = null;
		
		ReportFile reportFile = new ReportFile(this);
		if (reportFile.initialize()) {
			Enumeration<String> libraryNames = _libraryInfos.keys();
			while (libraryNames.hasMoreElements()) {
				String libraryName = libraryNames.nextElement();
				for (SourceLocation location : _libraryInfos.get(libraryName)) {
					Element info = reportFile.addViolation(location);
					reportFile.addElement(ReportFile.TAG_LIBRARY, libraryName, info);
				}
			}

			result = reportFile.save();
		}
		
		return result;
	}	
	
	private void addLibraryUse(String libraryFullName, SourceLocation location) {
		ArrayList<SourceLocation> libraryLocations = _libraryInfos.get(libraryFullName);
		if (libraryLocations == null) {
			libraryLocations = new ArrayList<SourceLocation>();
			_libraryInfos.put(libraryFullName, libraryLocations);
		}
		
		libraryLocations.add(location);
	}
}
