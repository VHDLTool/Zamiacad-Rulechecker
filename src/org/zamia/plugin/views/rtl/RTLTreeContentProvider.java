/* 
 * Copyright 2010,2011 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 */
package org.zamia.plugin.views.rtl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.zamia.plugin.views.rtl.RTLTreeCat.TreeCat;
import org.zamia.rtl.RTLModule;
import org.zamia.rtl.RTLNode;
import org.zamia.rtl.RTLPort;
import org.zamia.rtl.RTLSignal;

/**
 * 
 * @author guenter bartsch
 *
 */

public class RTLTreeContentProvider implements ITreeContentProvider {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object[] getChildren(Object aNode) {

		if (aNode instanceof RTLModule) {

			RTLModule module = (RTLModule) aNode;

			RTLTreeCat cats[] = new RTLTreeCat[4];
			cats[0] = new RTLTreeCat("Ports", module, TreeCat.PORTS);
			if (module instanceof RTLModule) {
				cats[1] = new RTLTreeCat("Subs", module, TreeCat.SUBS);
				cats[2] = new RTLTreeCat("Builtins", module, TreeCat.BUILTINS);
				cats[3] = new RTLTreeCat("Signals", module, TreeCat.SIGNALS);
			}

			return cats;
		}

		if (aNode instanceof RTLTreeCat) {

			RTLTreeCat cat = (RTLTreeCat) aNode;

			ArrayList res = new ArrayList();

			switch (cat.getCat()) {
			case PORTS:
				RTLModule module = cat.getModule();

				int n = module.getNumPorts();
				for (int i = 0; i < n; i++) {
					res.add(module.getPort(i));
				}

				break;
			case SIGNALS:
				RTLModule graph = getGraph(cat);

				n = graph.getNumSignals();
				for (int i = 0; i < n; i++) {
					res.add(graph.getSignal(i));
				}
				break;

			case SUBS:
				graph = getGraph(cat);

				n = graph.getNumNodes();
				for (int i = 0; i < n; i++) {
					RTLNode m = graph.getNode(i);
					if (m instanceof RTLModule) {
						res.add(graph.getNode(i));
					}
				}
				break;

			case BUILTINS:
				graph = getGraph(cat);

				n = graph.getNumNodes();
				for (int i = 0; i < n; i++) {
					RTLNode m = graph.getNode(i);
					if (!(m instanceof RTLModule)) {
						res.add(graph.getNode(i));
					}
				}
				break;
			}

			Collections.sort(res, new Comparator() {

				public int compare(Object o1, Object o2) {

					if ((o1 instanceof RTLSignal) && (o2 instanceof RTLSignal)) {

						RTLSignal s1 = (RTLSignal) o1;
						RTLSignal s2 = (RTLSignal) o2;

						return s1.getId().compareTo(s2.getId());
					}

					if ((o1 instanceof RTLModule) && (o2 instanceof RTLModule)) {

						RTLModule m1 = (RTLModule) o1;
						RTLModule m2 = (RTLModule) o2;

						return m1.getInstanceName().compareTo(m2.getInstanceName());
					}

					if ((o1 instanceof RTLPort) && (o2 instanceof RTLPort)) {

						RTLPort p1 = (RTLPort) o1;
						RTLPort p2 = (RTLPort) o2;

						return p1.getId().compareTo(p2.getId());
					}

					return 0;
				}
			});

			return res.toArray();
		}

		return null;
	}

	private RTLModule getGraph(RTLTreeCat aCat) {

		RTLModule m = aCat.getModule();
		if (!(m instanceof RTLModule))
			return null;

		RTLModule graph = (RTLModule) m;

		//		Architecture arch = graph.getArch();
		//		try {
		//			arch.elaborateStatements(graph, true);
		//		} catch (ZamiaException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		return graph;
	}

	public Object getParent(Object aNode) {

		if (aNode instanceof RTLTreeCat) {
			RTLTreeCat cat = (RTLTreeCat) aNode;
			return cat.getModule();
		}

		return null;
	}

	public boolean hasChildren(Object aNode) {
		return aNode instanceof RTLModule || aNode instanceof RTLTreeCat;
	}

	public Object[] getElements(Object aNode) {
		return getChildren(aNode);
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}
}
