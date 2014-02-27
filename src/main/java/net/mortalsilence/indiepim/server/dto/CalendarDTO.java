package net.mortalsilence.indiepim.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 16.02.14
 * Time: 21:02
 */
public class CalendarDTO {

    @JsonProperty("id") public Long id;
    @JsonProperty("name") public String name;
    @JsonProperty("defaultCalendar") public Boolean defaultCalendar;
    @JsonProperty("color") public String color;
    @JsonProperty("syncUrl") public String syncUrl;
    @JsonProperty("userName") public String userName;
    @JsonProperty("password") public String password;
    @JsonProperty("syncPrincipalPath") public String syncPrincipalPath;
}
