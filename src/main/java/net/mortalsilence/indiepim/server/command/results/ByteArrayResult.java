package net.mortalsilence.indiepim.server.command.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.mortalsilence.indiepim.server.command.Result;

public class ByteArrayResult implements Result {

	@JsonIgnore protected byte[] bytes;

    public ByteArrayResult(byte[] bytes) {
        this.bytes = bytes;
    }

    public ByteArrayResult() {
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
