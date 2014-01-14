package net.mortalsilence.indiepim.server.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 24.10.13
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */
public class EventDTO {

    @JsonProperty("id") private String id;
    @JsonProperty("title") private String title;
    @JsonProperty("start") private Long start;
    @JsonProperty("end") private Long end;
    @JsonProperty("allDay") private Boolean allDay;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }
}
