package net.mortalsilence.indiepim.server.controller;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 25.10.13
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
public class FileUpload {
    MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
