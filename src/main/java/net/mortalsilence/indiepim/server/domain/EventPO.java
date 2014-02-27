package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "event")
public class EventPO {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id", referencedColumnName="id")
    private UserPO user;

    @ManyToOne(optional=false)
    @JoinColumn(name = "calendar_id", referencedColumnName = "id")
    private CalendarPO calendar;

    @Column(name = "uid")
    private String uid;

    @Column(name = "name")
    private String name;

    @Column(name = "start")
    private Timestamp startTs;

    @Column(name = "end")
    private Timestamp endTs;


    @Column(name = "location")
    private String location;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "etag")
    private String etag;

    @OneToOne
    @JoinColumn(name = "recurrence_id", referencedColumnName = "id")
    private RecurrencePO recurrence;

    @OneToOne
    @JoinColumn(name = "orig_event_id", referencedColumnName = "id")
    private EventPO origEvent;

    public Long getId() {
        return id;
    }

    public UserPO getUser() {
        return user;
    }

    public void setUser(UserPO user) {
        this.user = user;
    }

    public CalendarPO getCalendar() {
        return calendar;
    }

    public void setCalendar(CalendarPO calendar) {
        this.calendar = calendar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStart() {
        return startTs.getTime();
    }

    public void setStart(Long startTs) {
        this.startTs = new Timestamp(startTs);
    }

    public Timestamp getStartTs() {
        return startTs;
    }

    public void setStartTs(Timestamp startTs) {
        this.startTs = startTs;
    }

    public Timestamp getEndTs() {
        return endTs;
    }

    public void setEndTs(Timestamp endTs) {
        this.endTs = endTs;
    }

    public Long getEnd() {
        if(endTs == null)
            return null;
        return endTs.getTime();
    }

    public void setEnd(Long endTs) {
        if(endTs == null) {
            this.endTs = null;
            return;
        }

        this.endTs = new Timestamp(endTs);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public RecurrencePO getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(RecurrencePO recurrence) {
        this.recurrence = recurrence;
    }

    public EventPO getOrigEvent() {
        return origEvent;
    }

    public void setOrigEvent(EventPO origEvent) {
        this.origEvent = origEvent;
    }
}
