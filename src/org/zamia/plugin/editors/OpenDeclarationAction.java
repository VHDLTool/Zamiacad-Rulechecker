/*
 * Copyright 2007-2008 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 */

package org.zamia.plugin.editors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.ide.IDE;
import org.zamia.ASTNode;
import org.zamia.FSCache;
import org.zamia.SourceFile;
import org.zamia.SourceLocation;
import org.zamia.ToplevelPath;
import org.zamia.ZamiaException;
import org.zamia.analysis.SourceLocation2AST;
import org.zamia.analysis.SourceLocation2IG;
import org.zamia.analysis.ast.ASTDeclarationSearch;
import org.zamia.instgraph.IGInstantiation;
import org.zamia.instgraph.IGItem;
import org.zamia.instgraph.IGManager;
import org.zamia.instgraph.IGModule;
import org.zamia.instgraph.IGOperationInvokeSubprogram;
import org.zamia.instgraph.IGOperationObject;
import org.zamia.instgraph.IGSequentialProcedureCall;
import org.zamia.instgraph.IGType;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.DeclarativeItem;


/**
 * 
 * @author Guenter Bartsch
 * 
 * ValTih: OpenDeclaration code will be shared with ShowDeclaration. Showing only needs computing the location, it will not actually jump.
 */
public class OpenDeclarationAction extends StaticAnalysisAction {

	public void run(IAction ignored) {
		try {
			
			processSelection();
			LocatedDeclaration jloc = findDeclaration();
			if (jloc != null)
				jloc.jumpTo();
			
		} catch (BadLocationException e) {
			el.logException(e);
		}
	}
	
	/**
	 * Locates declaration for a reference specified by a call to processSelection([optional caretPos]);
	 * @throws ZamiaException 
	 * @throws IOException 
	 */
	public LocatedDeclaration findDeclaration() {

		try {
			ToplevelPath tlp = getPath();

			if (tlp != null) {

				Pair<IGItem, ToplevelPath> res = SourceLocation2IG.findNearestItem(fLocation, tlp, fZPrj);

				if (res != null) {
					IGItem item = res.getFirst();
					tlp = res.getSecond();

					logger.info("OpenDeclaration: nearest item: %s, path: %s", item, tlp);

					if (item != null) {

						//IGSequentialProcedureCall seems to duplicate IGSequentialProcedureCall functionality. Should we keep both?
						if (item instanceof IGSequentialProcedureCall) {
							IGSequentialProcedureCall inv = (IGSequentialProcedureCall) item;
							item = (IGOperationInvokeSubprogram) inv.getChild(0);
						}

						if (item instanceof IGInstantiation) {
							logger.info("OpenDeclaration: this is an instantiation.");

							IGInstantiation inst = (IGInstantiation) item;

							IGManager igm = fZPrj.getIGM();
							IGModule module = igm.findModule(inst.getSignature());
							if (module != null) {
								return igTarget(module, tlp.append(inst.getLabel()));
							}
						} else if (item instanceof IGOperationObject) {
							item = ((IGOperationObject) item).getObject();
							return igTarget(item, tlp);
						} else if (item instanceof IGType) {
							return igTarget(item, tlp);
						} else if (item instanceof IGOperationInvokeSubprogram) {
							IGOperationInvokeSubprogram inv = (IGOperationInvokeSubprogram) item;
							return igTarget(inv.getSub(), tlp);
						}
					} else {

						logger.error("OpenDeclarationAction: IGItem not found => trying the AST way");

					}
				} else {
					logger.info("OpenDeclarationAction: findNearestIGItem() failed => trying the AST way.");
				}
			} 
			
			logger.info("Open Declaration Action");
			logger.info("=======================");
			logger.info("SourceLocation: %s", fLocation);

			ASTNode nearest = SourceLocation2AST.findNearestASTNode(fLocation, true, fZPrj);

			if (nearest != null) {
				DeclarativeItem declaration = ASTDeclarationSearch.search(nearest, fZPrj);

				if (declaration != null) {
					return astTarget(declaration);
				}
				
			} else {
				logger.error("Failed to map location %s to an AST object.", fLocation);
			}

		} catch (IOException e) {
			el.logException(e);
		} catch (ZamiaException e) {
			el.logException(e);
		}

		return null;
	}

	public class LocatedDeclaration {
		public final Object fItem; // the declaration object, used by show
		public final SourceLocation fLocation; // used by open 
		int fLen; 
		LocatedDeclaration(Object anItem, SourceLocation aLocation, int aLen) {
			this.fItem = anItem;
			this.fLocation = aLocation;
			this.fLen = aLen;
		}
		void jumpTo() {
			IWorkbenchPage page = fEditor.getEditorSite().getPage();
			ZamiaPlugin.showSource(page, fPrj, fLocation, fLen);
		}
		
		public String getComment(IDocument document) throws BadLocationException, IOException {
			int line = fLocation.fLine -1;
			// signal S1, S2: type; creates two declarations on the same line and 
			// diff column. Both share the same comment
			
			//Do we need to cache this (possibly we shouldn't open and load the file every while hovering the mouse)?
			SourceFile sf = fLocation.fSF;
			String uri = sf.getURI();
			BufferedReader br = new BufferedReader(FSCache.getInstance().openFile(sf, false));
			try {
				String s; int l = 0;
				while ((s = br.readLine()) != null) {
					if (l++ == line) {
//						IRegion reg = document.getLineInformation(line);
//						String s = document.get(reg.getOffset(), reg.getLength());
						int commentStart = s.indexOf("--");
						return commentStart != -1 ? s.substring(commentStart + 2).trim(): null; 
					}
					
				}
			} finally {
				br.close();
			}
		
			return null;
		}
	}
	
	private LocatedDeclaration astTarget(DeclarativeItem declaration) {
		
		String id = declaration.getId(); // can be null on unresolved package import, for instance
		return new LocatedDeclaration(declaration, declaration.getLocation(), (id == null ? 0 : id.length()));
		
		// The idea to select id.length is weird because declaration offset starts earlier.   
		// For instance, nearest is INTEGER and we jump to declaration 'TYPE INTEGER IS "-"2147483648 to 2147483647'
		// Thereby, INTEGER.length = 7 first characters are selected, covering 'TYPE IN'. To resolve it, we should 
		// keep id start location in DeclarativeItem or have ast.Name instead of id. Might be guenter did this 
		// apparently stupid thing to keep this "TODO" in mind?

	}
	
	private LocatedDeclaration igTarget(IGItem igItem, final ToplevelPath tlp) {
		SourceLocation location = igItem.computeSourceLocation();
		return (location == null) ? null : new LocatedDeclaration(igItem, location, 0) { 
			void jumpTo() {
				super.jumpTo();
				fEditor.setPath(tlp);
			}
		};

	}

}
