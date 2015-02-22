package org.zamia.plugin.editors;

import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public abstract class IdentifierAwareConfiguration extends SourceViewerConfiguration {
	abstract public IWordDetector getWordDetector();
	abstract public boolean ignoreCase();
}
