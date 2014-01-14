package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "calendar")
public class CalendarPO {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id", referencedColumnName="id")
    private UserPO user;

    @Column(name = "name")
    private String name;

    @Column(name = "ctag")
    private String ctag;

    @Column(name = "defaultCalendar")
    private Boolean defaultCalendar = false; // default value

    @Column(name = "color")
    private String color;

    @OneToMany(mappedBy = "calendar")
    private List<EventPO> events = new LinkedList<EventPO>();

    public Long getId() {
        return id;
    }

    public UserPO getUser() {
        return user;
    }

    public void setUser(UserPO user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCtag() {
        return ctag;
    }

    public void setCtag(String ctag) {
        this.ctag = ctag;
    }

    public Boolean getDefaultCalendar() {
        return defaultCalendar;
    }

    public void setDefaultCalendar(Boolean defaultCalendar) {
        this.defaultCalendar = defaultCalendar;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<EventPO> getEvents() {
        return events;
    }

    public void setEvents(List<EventPO> events) {
        this.events = events;
    }
}
