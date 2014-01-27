package net.mortalsilence.indiepim.server.dto;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 27.01.14
 * Time: 23:27
 */
public class AttachmentDTO {
    public AttachmentDTO(Long id, String filename, String mime_type) {
        this.id = id;
        this.filename = filename;
        this.mime_type = mime_type;
    }

    public AttachmentDTO() {
    }

    public Long id;
    public String filename;
    public String mime_type;
}
