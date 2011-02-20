/* 
 * Copyright 2011 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Feb 20, 2011
 */
package org.zamia.plugin.views.navigator;

import org.zamia.SourceLocation;
import org.zamia.ToplevelPath;
import org.zamia.ZamiaProject;
import org.zamia.rtl.RTLManager;
import org.zamia.rtl.RTLModule;
import org.zamia.util.HashSetArray;
import org.zamia.vhdl.ast.DMUID;

/**
 * Represents synthesized modules in the navigator tree
 * 
 * @author Guenter Bartsch
 *
 */

public class RTLModuleWrapper implements Comparable<RTLModuleWrapper> {

	public enum RTLMWOp {
		TOP
	};

	private RTLMWOp fOp;

	private DMUID fDUUID;

	private ToplevelPath fPath;

	private ZamiaProject fZPrj;

	private NavigatorWrapperCache fCache;

	public RTLModuleWrapper(RTLMWOp aOp, String aSignature, DMUID aDUUID, ToplevelPath aPath, NavigatorWrapperCache aCache) {
		//fSignature = aSignature;
		fDUUID = aDUUID;
		fPath = aPath;
		fOp = aOp;
		fCache = aCache;
		fZPrj = fCache.getZPrj();
	}

	public ToplevelPath getPath() {
		return fPath;
	}

	public RTLMWOp getOp() {
		return fOp;
	}

	@Override
	public boolean equals(Object aObject) {

		if (!(aObject instanceof RTLModuleWrapper)) {
			return false;
		}

		RTLModuleWrapper wrapper2 = (RTLModuleWrapper) aObject;

		if (getOp() != wrapper2.getOp())
			return false;

		ToplevelPath p1 = getPath();
		ToplevelPath p2 = wrapper2.getPath();
		if (p1 != null && p2 != null && !p1.equals(p2)) {
			return false;
		}
		if (p1 == null && p2 != null)
			return false;
		if (p1 != null && p2 == null)
			return false;

		return getDUUID().equals(wrapper2.getDUUID());
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public DMUID getDUUID() {
		return fDUUID;
	}

	public void setDUUID(DMUID aDUUID) {
		fDUUID = aDUUID;
	}

	public void setZPrj(ZamiaProject aZPrj) {
		fZPrj = aZPrj;
	}

	public ZamiaProject getZPrj() {
		return fZPrj;
	}

	public Object[] getChildren() {

		//		RTLManager igm = fZPrj.getRTLM();
		//
		//		switch (fOp) {
		//
		//		case TOP:
		//
		//			ArrayList<RTLModuleWrapper> res = new ArrayList<RTLModuleWrapper>();
		//			RTLModule module = igm.findModule(fPath.getToplevel());
		//
		//			if (module != null) {
		//
		//				res.add(fCache.getLocalsWrapper(module, fPath));
		//				res.add(fCache.getGlobalsWrapper(module, fPath));
		//
		//				RTLStructure structure = module.getStructure();
		//
		//				ArrayList<RTLModuleWrapper> subs = getChildren(structure);
		//
		//				Collections.sort(subs);
		//
		//				int n = subs.size();
		//				for (int i = 0; i < n; i++) {
		//					res.add(subs.get(i));
		//				}
		//			}
		//
		//			return res.toArray();
		//
		//		}

		HashSetArray<Object> res = new HashSetArray<Object>();

		return res.toArray();

	}

	@Override
	public String toString() {

		String label = "???";

		switch (fOp) {
		case TOP:
			label = fDUUID.toCompactString();
			break;

		}

		return label;
	}

	@Override
	public int compareTo(RTLModuleWrapper aO) {
		String s1 = toString();
		String s2 = aO.toString();
		return s1.compareTo(s2);
	}

	public boolean hasChildren() {
		// FIXME
		return false;
	}

	public SourceLocation getLocation() {

		SourceLocation location = null;

		RTLManager igm = fZPrj.getRTLM();

		RTLModule module;
		switch (fOp) {
		case TOP:
			module = igm.findModule(fPath.getToplevel());
			location = module.computeSourceLocation();
			break;

		}

		return location;
	}

	public ToplevelPath getEditorPath() {
		//		switch (fOp) {
		//		case BLUERTL:
		//		case INSTANTIATION:
		//			if (fPath != null) {
		//				return fPath.getNullParent();
		//			}
		//			break;
		//		}
		return getPath();
	}

}
