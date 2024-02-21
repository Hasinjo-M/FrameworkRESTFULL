/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etu1789.framework.upload;

/**
 *
 * @author Hasinjo
 */

public class FileUpload {
    String filePath;
    String fileName;
    byte[] file;

    public FileUpload() {

    }
    public FileUpload(String path, String name, byte[] fileByte) {
        this.filePath = path;
        this.fileName = name;
        this.file = fileByte;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    
}
