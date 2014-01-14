package net.mortalsilence.indiepim.server.command.results;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 24.10.13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class IdVersionResult extends IdResult {

    @JsonProperty("version") private Timestamp version;

    public IdVersionResult(Long id, Timestamp version) {
   		this.id = id;
        this.version = version;
   	}

    public IdVersionResult() {
    }

    public Timestamp getVersion() {
        return version;
    }
}
