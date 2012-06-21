/* 
 * Copyright 2008-2009 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jun 22, 2008
 */
package org.zamia.plugin.search;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.Match;
import org.zamia.ASTNode;
import org.zamia.ExceptionLogger;
import org.zamia.SourceLocation;
import org.zamia.ToplevelPath;
import org.zamia.Utils;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.analysis.ReferenceSearchResult;
import org.zamia.analysis.ReferenceSite;
import org.zamia.analysis.ReferenceSite.RefType;
import org.zamia.analysis.SourceLocation2AST;
import org.zamia.analysis.SourceLocation2IG;
import org.zamia.analysis.ast.ASTDeclarationSearch;
import org.zamia.analysis.ast.ASTReferencesSearch;
import org.zamia.analysis.ig.IGReferencesSearch;
import org.zamia.instgraph.IGItem;
import org.zamia.instgraph.IGObject;
import org.zamia.plugin.ZamiaPlugin;
import org.zamia.plugin.editors.StaticAnalysisAction;
import org.zamia.util.Pair;
import org.zamia.vhdl.ast.DeclarativeItem;


/**
 * 
 * @author Guenter Bartsch
 * 
 */

public class ReferencesSearchQuery implements ISearchQuery {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	public static final ExceptionLogger el = ExceptionLogger.getInstance();

	private ZamiaSearchResult fSearchResult;

	protected boolean fSearchUpward, fSearchDownward;

	protected boolean fDeclOnly, fWritersOnly, fReadersOnly;

	private boolean fUsePath;

	protected ZamiaProject fZPrj;

	private ToplevelPath fTLP;

	private SourceLocation fLocation;

	public ReferencesSearchQuery(ZamiaProject aZPrj, ToplevelPath aTLP, SourceLocation aLocation, boolean aSearchUpward, boolean aSearchDownward, boolean aDeclOnly, boolean aUsePath, boolean aWritersOnly, boolean aReadersOnly) {

		fZPrj = aZPrj;
		fTLP = aTLP;
		fMessage = fLocation = aLocation;
		fSearchUpward = aSearchUpward;
		fSearchDownward = aSearchDownward;
		fDeclOnly = aDeclOnly;
		fUsePath = aUsePath;
		fWritersOnly = aWritersOnly;
		fReadersOnly = aReadersOnly;
	}

	public ReferencesSearchQuery(StaticAnalysisAction aSAA, boolean aSearchUpward, boolean aSearchDownward, boolean aDeclOnly, boolean aUsePath, boolean aWritersOnly, boolean aReadersOnly) {
		this(aSAA.getZamiaProject(), aSAA.getPath(), aSAA.getLocation(), aSearchUpward, aSearchDownward, aDeclOnly, aUsePath, aWritersOnly, aReadersOnly);
	}

	public boolean isSearchUpward() {
		return fSearchUpward;
	}

	public boolean isSearchDownward() {
		return fSearchDownward;
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	Object fMessage = null;
	boolean fDone = false;
	
	protected String getLabelOptions() {
		List options = new ArrayList();
		Object[] pairs = new Object[] {
				fSearchDownward, "Down",
				fSearchUpward, "Up",
				fDeclOnly, "Decl",
				fReadersOnly, "Readers",
				fWritersOnly, "Writers",
				fUsePath, "Path=" + fTLP,
			};
		for (int i = 0 ; i != pairs.length ; i++) {
			if ((Boolean) pairs[i++]) 
				options.add(pairs[i]);
		}

		return Utils.concatenate(options, "+");
	}
	public String getLabel() {
		return (fDone ? " Done " : " ") + "Searching " + fMessage + " ("+ getLabelOptions() +") for references..." + refCounter + " found" ;
	}

	public ZamiaSearchResult getSearchResult() {
		if (fSearchResult == null)
			fSearchResult = new ZamiaSearchResult(this);
		return fSearchResult;
	}

	public IStatus run(IProgressMonitor aMonitor) throws OperationCanceledException {

		refCounter = 0;
		fSearchResult = getSearchResult();
		fSearchResult.removeAll();

		try {

			ZamiaProject zprj = fZPrj;
			ToplevelPath tlp = fTLP;

			if (fUsePath && tlp != null) {

				Pair<IGItem, ToplevelPath> nearest = SourceLocation2IG.findNearestItem(fLocation, tlp, zprj);

				if (nearest != null) {

					IGItem item = nearest.getFirst();
					ToplevelPath path = nearest.getSecond();
					fMessage = path + " : " + item;
					
					logger.info("ReferencesSearchQuery: nearest item: %s, path: %s", item, path);

					if (item != null) {

						IGObject object = IGReferencesSearch.asObject(item);
						
						if (object != null) {
							fMessage = object.getId();
							igSearch(object, path);
						} else {
							ZamiaPlugin.showError(null, "IG-based reference search failed", "Failed to map cursor location to IG Object", "Mapped to non-object " + item);
						}
					} else {
						ZamiaPlugin.showError(null, "IG-based reference search failed", "Failed to map cursor location to IG Object", "Mapped to no IG item at all");
					}
				} else {
					ZamiaPlugin.showError(null, "IG-based reference search failed", "Failed to map cursor location to IG Object", "Mapped to no IG item at all");
				}

			} else {

				/*
				 * AST based reference search in case we do not have path
				 * information or the user requested ir
				 */

				ASTNode nearest = SourceLocation2AST.findNearestASTNode(fLocation, true, zprj);
				fMessage = nearest;
				if (nearest != null) {
					DeclarativeItem declaration = ASTDeclarationSearch.search(nearest, zprj);
					fMessage = declaration;
					if (declaration != null) {

						ReferenceSearchResult results = ASTReferencesSearch.search(declaration, fSearchUpward, fSearchDownward, zprj);

						if (fDeclOnly) {

							ReferenceSearchResult filteredResults = new ReferenceSearchResult("Initial Signal Declarations of " + declaration, declaration.getLocation(), declaration.toString().length());

							int n = results.getNumChildren();

							for (int i = 0; i < n; i++) {

								ReferenceSearchResult res = results.getChild(i);

								if (res instanceof ReferenceSite 
										&& ((ReferenceSite) res).getRefType() == RefType.Declaration)
									filteredResults.add(res);
								
							}

							addMatch(filteredResults);

						} else {
							addMatch(results);

						}
					} else {
						ZamiaPlugin.showError(null, "AST-based reference search failed", "Reference search failed.", "Failed to find declaration of " + nearest);
					}
				} else {
					ZamiaPlugin.showError(null, "AST-based reference search failed", "Failed to map cursor location " + fLocation + " to and AST object", "");
				}
			}
		} catch (Throwable e) {
			el.logException(e);
			ZamiaPlugin.showError(null, "Exception caught while executing reference search", "Caught an unexpected exception during reference search", "" + e);
		}
		fDone = true;
		aMonitor.done();
		return Status.OK_STATUS;
	}

	//Extended search may have more than one result to merge
	protected void igSearch(IGObject object, ToplevelPath path) {
		IGReferencesSearch rs = new IGReferencesSearch(fZPrj);
		ReferenceSearchResult rsr = rs.search(object, path, fSearchUpward, fSearchDownward, fWritersOnly, fReadersOnly);
		mergeResults(object, rsr);
	}

	protected void mergeResults(Object aObject, ReferenceSearchResult root) {
		if (root != null) {
			//aRSR.dump(1, System.err);
			addMatch(root);
		} else {
			ZamiaPlugin.showError(null, "IG-based reference search (" + aObject + ") failed", "Search returned no result.", "");
			System.err.println(aObject + " search returns null");
			
		}
	}
	
	int refCounter = 0;
	protected void addMatch(ReferenceSearchResult aRSR) {
		refCounter += aRSR.countRefs();
		fSearchResult.addMatch(new Match(aRSR, 0, 1));
	}
}