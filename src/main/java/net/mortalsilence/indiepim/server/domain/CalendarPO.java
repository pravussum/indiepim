package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "calendar")
public class CalendarPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "syncUrl")
    private String syncUrl;

    @Column(name = "syncUserName")
    private String syncUserName;

    @Column(name = "syncPassword")
    private String syncPassword;

    @Column(name = "syncCalendarName")
    private String syncCalendarId;

    @Column(name = "syncPrincipalPath")
    private String syncPrincipalPath;

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

    public String getSyncUrl() {
        return syncUrl;
    }

    public void setSyncUrl(String syncUrl) {
        this.syncUrl = syncUrl;
    }

    public String getSyncUserName() {
        return syncUserName;
    }

    public void setSyncUserName(String syncUserName) {
        this.syncUserName = syncUserName;
    }

    public String getSyncPassword() {
        return syncPassword;
    }

    public void setSyncPassword(String syncPassword) {
        this.syncPassword = syncPassword;
    }

    public String getSyncCalendarId() {
        return syncCalendarId;
    }

    public void setSyncCalendarId(String syncCalendarName) {
        this.syncCalendarId = syncCalendarName;
    }

    public String getSyncPrincipalPath() {
        return syncPrincipalPath;
    }

    public void setSyncPrincipalPath(String syncPrincipalPath) {
        this.syncPrincipalPath = syncPrincipalPath;
    }
}
