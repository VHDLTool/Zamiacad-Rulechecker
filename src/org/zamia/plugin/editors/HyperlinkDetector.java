package org.zamia.plugin.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Valentin Tihhomirov
 * Used to jump to declarations */
public class HyperlinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		try {
			final IDocument doc = textViewer.getDocument();
//			Region r = VHDLInformationProvider.senseIdentifierRange(doc, region.getOffset());
			if (VHDLInformationProvider.getInformationStaticMethod(region.getOffset()) != null) {
				Region r = VHDLInformationProvider.senseIdentifierRange(doc, region.getOffset());
				return new IHyperlink[] {new Hyperlink(r, doc.get(r.getOffset(), r.getLength()))};
			}
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static class Hyperlink implements IHyperlink {

	    private String text;
	    private IRegion region;

	    public String toString() {
	    	return "(" + region + "): " + text;
	    }

	    public Hyperlink(IRegion r, String text) {
	        this.region = r;
	        this.text = text;
	    }

	    public IRegion getHyperlinkRegion() {
	        return region;
	    }

	    public void open() {
//	        if(text!=null)
	    	
	    		// TODO: It seems that pressing F3 can do without creating a new Action object every time 
	        	new OpenDeclarationAction().run(null);	        	
	    }

	    public String getTypeLabel() {
	        return null;
	    }


	    public String getHyperlinkText() {
	        return null;
	    }

	}


}
