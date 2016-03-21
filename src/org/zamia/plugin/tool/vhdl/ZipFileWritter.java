package org.zamia.plugin.tool.vhdl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
/**
 * Compresser des fichiers dans un ZIP avec ZipOutputStream
 * @author cniesner
 */
public class ZipFileWritter {

    /**
     * Flux de l'archive zip
     */
    private ZipOutputStream zos;
 
    /**
     * Constructor: creation d'une nouvelle archive
     * @param zipFile
     * @throws FileNotFoundException
     */
    public ZipFileWritter(String zipFile) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(zipFile);
//ajout du checksum
        CheckedOutputStream checksum = new CheckedOutputStream(fos, new Adler32());
        this.zos = new ZipOutputStream(new BufferedOutputStream(checksum));
    }
 
    /**
     * Ajouter un fichier au zip
     * @param fileName
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void addFile(String fileName) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(fileName);
        int size = 0;
        byte[] buffer = new byte[1024];
 
//Ajouter une entree à l'archive zip
        File file = new File(fileName);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        this.zos.putNextEntry(zipEntry);
 
//copier et compresser les données
        while ((size = fis.read(buffer, 0, buffer.length)) > 0) {
            this.zos.write(buffer, 0, size);
        }
 
        this.zos.closeEntry();
        fis.close();
    }
 
    /**
     * Fermer le fichier zip
     * @throws IOException
     */
    public void close() throws IOException {
        this.zos.close();
    }
}
