package org.zamia.plugin.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.zamia.plugin.editors.OpenDeclarationAction.LocatedDeclaration;

/**
 * Valentin Tihhomirov
 * Used to jump to declarations */
public class HyperlinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		try {
			final IDocument doc = textViewer.getDocument();
			LocatedDeclaration ld = VHDLInformationProvider.findDeclaration(region.getOffset());
			if (ld != null) {
				Region r = VHDLInformationProvider.senseIdentifierRange(doc, region.getOffset());
				return new IHyperlink[] {new Hyperlink(r, ld)};
			}
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static class Hyperlink implements IHyperlink {

	    private IRegion region;
	    LocatedDeclaration locatedDeclaration;

//	    public String toString() {
//	    	return "(" + region + "): " + text;
//	    }

	    public Hyperlink(IRegion r, LocatedDeclaration locatedDeclaration) {
	        this.region = r;
	        this.locatedDeclaration = locatedDeclaration;
	    }

	    public IRegion getHyperlinkRegion() {
	        return region;
	    }

	    public void open() {
    		// TODO: It seems that pressing F3 can do without creating a new Action object every time 
        	//new OpenDeclarationAction().run(null);
	    	locatedDeclaration.jumpTo();
	    }

	    public String getTypeLabel() {
	        return null;
	    }


	    public String getHyperlinkText() {
	        return null;
	    }

	}


}
