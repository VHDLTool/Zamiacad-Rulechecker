package org.zamia.plugin.editors;

import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.zamia.SourceLocation;
import org.zamia.SourceRanges;
import org.zamia.analysis.ReferenceSite;
import org.zamia.plugin.views.sim.SimulatorView;

/**
 * @author Anton Chepurov
 */
public class ReferencesSearchQueryListener implements IQueryListener {

	private final ReferencesSearchQuery fQuery;

	private final SimulatorView fSimulatorView;

	private final SourceRanges fSources = SourceRanges.createRanges();

	public ReferencesSearchQueryListener(ReferencesSearchQuery aQuery, SimulatorView aSimulatorView) {
		fQuery = aQuery;
		fSimulatorView = aSimulatorView;
	}

	@Override
	public void queryAdded(ISearchQuery query) {
	}

	@Override
	public void queryRemoved(ISearchQuery query) {
	}

	@Override
	public void queryStarting(ISearchQuery query) {
	}

	@Override
	public void queryFinished(ISearchQuery query) {
		if (fQuery != query) {
			return;
		}

		ReferencesSearchQuery rsquery = (ReferencesSearchQuery) query;

		ZamiaSearchResult result = (ZamiaSearchResult) rsquery.getSearchResult();

		for (Object element : result.getElements()) {

			if (element instanceof ReferenceSite) {

				ReferenceSite site = (ReferenceSite) element;

				SourceLocation location = site.getLocation();

				fSources.add(location, 0);
			}

		}

		fSimulatorView.setStaticSources(fSources);

	}

}
