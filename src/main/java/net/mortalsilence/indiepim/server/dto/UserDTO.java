package net.mortalsilence.indiepim.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 25.10.13
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
public class UserDTO {

    @JsonProperty("id") private Long id;
    @JsonProperty("userName") private String userName;
    @JsonProperty("password") private String password;
    @JsonProperty("isAdmin") private Boolean admin;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
