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
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {

	        try {
		        IDocument document = textViewer.getDocument();
		        Hyperlink left = scan(document, region.getOffset(), true);
		        Hyperlink right = scan(document, region.getOffset()+1, false);
	            return new IHyperlink[] {new Hyperlink(left, right)};
            } catch (BadLocationException ex) {
                return null;
            }

	}
	
	private static Hyperlink scan(IDocument document, int offset, boolean scanLeft) throws BadLocationException {
        StringBuilder sb = new StringBuilder();
        int length = 0;
        while (true) {
        	char ch = document.getChar(offset);
        	if (Character.isIdentifierIgnorable(ch))
        		continue;
        	if (!Character.isUnicodeIdentifierPart(ch))
        		break;
        	if (scanLeft) {
        		offset -= 1;        		
            	sb.insert(0, ch);
        	} else {
        		offset += 1;        		
            	sb.append(ch);
        	}
        	length++;
        }
		return new Hyperlink(new Region(offset+1, length), sb.toString());
	}

	public static class Hyperlink implements IHyperlink {

	    private String text;
	    private IRegion region;

	    public Hyperlink(IRegion region, String text) {
	        this.region= region;
	        this.text = text;
	    }
	    
	    public String toString() {
	    	return "(" + region + "): " + text;
	    }

	    public Hyperlink(Hyperlink left, Hyperlink right) {
	        this.region = new Region(left.region.getOffset(), left.region.getLength() + right.region.getLength());
	        this.text = left.text + right.text;
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
