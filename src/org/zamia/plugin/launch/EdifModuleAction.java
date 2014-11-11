/*
 * Copyright 2007, 2009 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
*/

package org.zamia.plugin.launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.zamia.ZamiaException;
import org.zamia.ZamiaProject;
import org.zamia.instgraph.IGConcurrentStatement;
import org.zamia.instgraph.IGInstantiation;
import org.zamia.instgraph.IGItem;
import org.zamia.instgraph.IGManager;
import org.zamia.instgraph.IGMapping;
import org.zamia.instgraph.IGModule;
import org.zamia.instgraph.IGObject;
import org.zamia.instgraph.IGOperation;
import org.zamia.instgraph.IGOperationIndex;
import org.zamia.instgraph.IGOperationObject;
import org.zamia.instgraph.IGProcess;
import org.zamia.instgraph.IGStructure;
import org.zamia.instgraph.IGStructureVisitor;
import org.zamia.util.PathName;
import org.zamia.vhdl.ast.DMUID;

/**
 * Netlist is a structural description. This means that it must not contain any processes 
 * (behavioral description) except may be external gates. But latter must contain no
 * structure then. Such gates are assumed external and their processes are abridged. 
 * The functin of the gates must be provided by external library.
 * 
 *  TODO: move to Zamiacad project.
 * */
public class EdifModuleAction extends LaunchIGModuleAction {

	public void run(IAction aAction) {

		if (fWrapper == null)
			return;

		logger.debug("LaunchIGModuleAction: Launching: " + fWrapper);

		final IGModule module;
		ZamiaProject zp = fWrapper.getZPrj();
		final IGManager igm = zp.getIGM();
		
		switch (fWrapper.getOp()) {
			case TOPRED: module = igm.findModule(fWrapper.getPath().getToplevel()); break;
			case INSTANTIATION: module = fWrapper.getInstantiation().findModule(); break;
			case BLUEIG: module = igm.findModule(fWrapper.getDMUID()); break;
			default: logger.warn("unsupported target " + fWrapper.getOp()); module = null;
		}
		Shell shell= PlatformUI.getWorkbench().getModalDialogShellProvider().getShell();
		FileDialog dialog = new FileDialog (shell, SWT.SAVE);
		dialog.setFilterExtensions (new String [] {"*.edif;*.edf, *"});
		dialog.setFilterPath(zp.fBasePath.toString());
		dialog.setFileName (module.getDUUID().getId());
		String file = dialog.open();
		
		if (file != null)
		try {
			new EdifSaver(module, new File(file));
		} catch (Exception e) {
			el.logException(e);
		}

		
	}

	class EdifSaver {
		
		PrintWriter out;
		void save(String format, Object... args) {
			//logger.info(format, args);
			out.format(format + "\n", args);
		}
		EdifSaver(final IGModule module, File file) throws FileNotFoundException, ZamiaException {
			out= new PrintWriter(file);
			try {
	
				save("(edif ZamiaCAD_edif (edifVersion 2 0 0) (edifLevel 0) (keywordMap (keywordLevel 0))");
				
				class Visitor implements IGStructureVisitor {
					
					Collection<DMUID> deps = new LinkedHashSet<>();
					
					public void visit(IGStructure struct, PathName path) throws ZamiaException {
						IGItem item = module.findItem(path);
						if (item instanceof IGModule) {
							deps.add(((IGModule) item).getDUUID());
						} else if (item instanceof IGInstantiation) {
							DMUID child = ((IGInstantiation) item).getChildDUUID();
							deps.remove(child); deps.add(child);
							
							/*List order = new ArrayList();
							for (DMUID dep: deps) order.add(dep.getLibId() + "."+dep.getId());
							logger.info("visiting path=%s, duuid= %s, deps =%s ", path, child, Utils.concatenate(order));*/
						}
		
					}
					
				}
	
				final IGManager igm = module.getIGM();
				
					// ORDER MODULES TO SAVE
					Visitor v = new Visitor();
					module.accept(v);
					
					// REVERT THE ORDER -- first to be saved are in front actually
					LinkedList<DMUID> reverted = new LinkedList<>(v.deps);
					LinkedList<DMUID> dep2 = new LinkedList<>();
					while (!reverted.isEmpty()) {
						DMUID dep = reverted.removeLast();
						dep2.add(dep);
					}
					
					// START SAVING
					while (!dep2.isEmpty()) {
						
						LinkedList<DMUID> deps = new LinkedList<>();
						
						// DETERMINE THE LIBRARY AND ALL GATES OF FIRST GATE TO SAVE
						String libID = dep2.peek().getLibId();
						//logger.info("starting %s", libID);
						IGConcurrentStatement proc = null, structural = null; // external must contain only processes
						reverted.clear();
						for (DMUID dep : dep2)
							if (!dep.getLibId().equals(libID))
								deps.add(dep);
							else {
								//logger.info(" analyzing %s", dep.getId());
								IGStructure struct = igm.findModule(dep).getStructure();
								for ( IGConcurrentStatement s: struct.getStatements()) {
									if (s instanceof IGInstantiation) structural = s;
									else if (s instanceof IGProcess) 
										proc = s;
									else logger.warn(" unknown smt %s", s);
									if (proc != null && structural != null)
										throw new RuntimeException(dep + " contains both functional description, e.g. " + proc + ", which implies that it is (non-structural, external) gate, as well as structural information, e.g. " + structural.getLabel() + "(" + ((IGInstantiation)structural).getChildDUUID().getId() + ")");
	
								}
								reverted.add(dep);
							}
						
						boolean extern = proc != null; // library is external if its gates are purely functional
						dep2 = deps;
						
						save("(%s %s (edifLevel 0) (technology (numberDefinition))", extern ? "external" : "library", libID);
						for (DMUID dep : reverted) {
							save(" (cell " + dep.getId() + " (cellType GENERIC) (view Netlist_representation (viewType NETLIST)");
							IGStructure struct = igm.findModule(dep).getStructure();
							save("  (interface");
							for (IGObject o: struct.getContainer().interfaces())
								save("   port " + o.getId() + " (direction " + (o.getDirection() == IGObject.OIDir.OUT || o.getDirection() == IGObject.OIDir.BUFFER ? "OUTPUT" : "INPUT") + ")");
							save("  )");
							
							// we expect that all statements are instantiations
							if (!extern) {
								save("  (contents");
								Map<String, StringBuilder> nets = new HashMap<>(); // actual is a signal that connects (a set of) formals 
								for (IGConcurrentStatement smt: struct.getStatements()) {
									IGInstantiation inst = (IGInstantiation) smt;
									int n = inst.getNumMappings();
									//String mappings = "";
									for (int i = 0 ; i != n ; i++) {
										IGMapping m = inst.getMapping(i);
										IGObject formal = ((IGOperationObject) m.getFormal()).getObject();
										IGOperation actOp = m.getActual();
										final String actualId, actualFull;
										if (actOp instanceof IGOperationObject) {
											IGObject obj = ((IGOperationObject) actOp).getObject();
											actualFull = actualId = obj.getId(); 
										} else {
											IGOperationIndex index = ((IGOperationIndex) actOp); 
											actualId = ((IGOperationObject)index.getOperand()).getObject().getId();
											actualFull = "(member "+actualId+" " + index.getIndex() + ")";
										} 
										StringBuilder formals = nets.get(actualId);
										if (formals == null) nets.put(actualId, formals = new StringBuilder(
												"   (net " + actualId +" (joined (portRef " + actualFull + ") "));
										formals.append("(portRef ").append(formal.getId()).append(" (instanceRef ").append(smt.getLabel()).append(")) ");
										//mappings += formal.getId() + " => " + actualId + ", ";
									}
									DMUID ref = inst.getChildDUUID();
									save("   (instance %s (viewRef Netlist_representation (cellRef %s"+(ref.getLibId().equals(libID) ? "": " (libraryRef %s)")+")))"// + ", mappings: " + mappings
											, smt.getLabel(), ref.getId(), ref.getLibId());
								}
								
								for (String actual: nets.keySet()) {
									//logger.info("   (" + actual.getId() + ": " + Utils.concatenate(nets.get(actual), " ") + ")");
									save(nets.get(actual).append("))").toString());
								}
								
								save("  )");
							}
							save(" ))");
						}
						save(")");
					}
	
					
					//printf("signals are: %s", ','.join([str(l.getId()) for l in module.getContainer().localItems() if isinstance(l, org.zamia.instgraph.IGObject) and l.getCat() == IGObject.IGObjectCat.SIGNAL]))
					DMUID modID = module.getDUUID();
					save("(design %s (cellRef %s (libraryRef %s))))", modID.getId(), modID.getId(), modID.getLibId());
			} finally {
				out.close();
			}
			
			logger.info("File %s was written", file);
		}
		
		
	}
}
