package org.zamia.plugin.tool.vhdl.help;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.zamia.ZamiaLogger;

public class ThreadCreateFeature extends Thread {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();
	
	@Override
	public void run() {
		InputStream pdfInJar = getClass().getClassLoader().getResourceAsStream("/help/ZamiaCad_Rule_Checker_Features_Description.pdf");
		try {
		    File pdf = new File("ZamiaCad_Rule_Checker_Features_Description.pdf");
		    FileOutputStream fos = new java.io.FileOutputStream(pdf);
		    while (pdfInJar.available() > 0) {
		          fos.write(pdfInJar.read());
		    }
		    fos.close();
		    System.out.println("ZamiaCad_Rule_Checker_Features_Description.pdf  OK");
		} catch (IOException e) {
			logger.error("some exception message", e);
		}
		
		
	}
	
	
}