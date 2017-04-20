package net.mortalsilence.indiepim.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 24.10.13
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */
public class EventDTO {

    @JsonProperty("id") public Long id;
    @JsonProperty("title") public String title;
    @JsonProperty("start") public Long start;
    @JsonProperty("end") public Long end;
    @JsonProperty("allDay") public Boolean allDay;
    @JsonProperty("color") public String color;
    @JsonProperty("url") public String url;
    @JsonProperty("description") public String description;
    @JsonProperty("location") public String location;
    @JsonProperty("calendarId") public Long calendarId;
}
