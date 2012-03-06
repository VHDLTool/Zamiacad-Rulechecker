package org.zamia.plugin;

import org.eclipse.core.runtime.FileLocator;
import org.zamia.ResourceLocator;

import java.io.IOException;
import java.net.URL;

/**
 * @author Anton Chepurov
 */
public class EclipseLocator implements ResourceLocator {
	private static EclipseLocator fInstance = new EclipseLocator();

	public static EclipseLocator getInstance() {
		return fInstance;
	}

	@Override
	public URL resolve(URL aUrl) throws IOException {
		return FileLocator.resolve(aUrl);
	}
}
