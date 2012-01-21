/*
 * Copyright 2007-2008 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 */

package org.zamia.plugin.editors;

import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IWorkbenchPage;
import org.zamia.ASTNode;
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
import org.zamia.instgraph.IGType;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.DeclarativeItem;


/**
 * 
 * @author Guenter Bartsch
 * 
 */
public class OpenDeclarationAction extends StaticAnalysisAction {

	public void run(IAction ignored) {

		try {

			processSelection();

			ToplevelPath tlp = getPath();

			boolean success = false;
			
			if (tlp != null) {

				Pair<IGItem, ToplevelPath> res = SourceLocation2IG.findNearestItem(fLocation, tlp, fZPrj);

				if (res != null) {
					IGItem item = res.getFirst();
					tlp = res.getSecond();

					logger.info("OpenDeclaration: nearest item: %s, path: %s", item, tlp);

					if (item != null) {

						if (item instanceof IGInstantiation) {
							logger.info("OpenDeclaration: this is an instantiation.");

							IGInstantiation inst = (IGInstantiation) item;

							IGManager igm = fZPrj.getIGM();
							IGModule module = igm.findModule(inst.getSignature());
							if (module != null) {
								SourceLocation location = module.computeSourceLocation();
								success = jumpToIG(location, tlp.append(inst.getLabel()));
							}
						} else if (item instanceof IGOperationObject) {
							item = ((IGOperationObject) item).getObject();
							success = jumpToIG(item.computeSourceLocation(), tlp);
						} else if (item instanceof IGType) {
							success = jumpToIG(item.computeSourceLocation(), tlp);
						} else if (item instanceof IGOperationInvokeSubprogram) {
							
							IGOperationInvokeSubprogram inv = (IGOperationInvokeSubprogram) item;
							success = jumpToIG(inv.getSub().computeSourceLocation(), tlp);
						}
					} else {

						logger.error("OpenDeclarationAction: IGItem not found => trying the AST way");

					}
				} else {
					logger.info("OpenDeclarationAction: findNearestIGItem() failed => trying the AST way.");
				}
			} 
			
			if (!success) {

				logger.info("Open Declaration Action");
				logger.info("=======================");
				logger.info("SourceLocation: %s", fLocation);

				ASTNode nearest = SourceLocation2AST.findNearestASTNode(fLocation, true, fZPrj);

				if (nearest != null) {
					DeclarativeItem declaration = ASTDeclarationSearch.search(nearest, fZPrj);

					//for instance, nearest is INTEGER, we jump to declaration = TYPE INTEGER IS "-"2147483648 to 2147483647
					//Thereby, INTEGER.length = 7 first characters are selected, covering TYPE IN. Keep id start location in DeclarativeItem? 
					if (declaration != null) {
						jumpToAST(declaration.getLocation(), declaration.getId().length());
					}
					
				} else {
					logger.error("Failed to map location %s to an AST object.", fLocation);
				}
			}

		} catch (BadLocationException e) {
			el.logException(e);
		} catch (IOException e) {
			el.logException(e);
		} catch (ZamiaException e) {
			el.logException(e);
		}
	}

	private void jumpToAST(SourceLocation location, int len) {
		IWorkbenchPage page = fEditor.getEditorSite().getPage();
		ZamiaPlugin.showSource(page, fPrj, location, len);
	}
	
	private boolean jumpToIG(SourceLocation location, ToplevelPath tlp) {
		if (location == null)
			return false;
		jumpToAST(location, 0);
		fEditor.setPath(tlp);
		return true;
	}
}
