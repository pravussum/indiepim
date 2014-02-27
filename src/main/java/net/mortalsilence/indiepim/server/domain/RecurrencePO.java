package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;

@Entity
@Table(name = "recurrence")
public class RecurrencePO {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @OneToOne (mappedBy = "recurrence")
    private EventPO event;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id", referencedColumnName="id")
    private UserPO user;

    @Column(name = "rec_interval")
    private Integer interval;

    @Column(name = "freq", nullable = false)
    private String frequency;

    @Column(name = "until")
    private Long until;

    @Column(name = "count")
    private Integer count;

    @Column(name = "bysecond")
    private String bySecond;

    @Column(name = "byminute")
    private String byMinute;

    @Column(name = "byhour")
    private String byHour;

    @Column(name = "byday")
    private String byDay;

    @Column(name = "bymonthday")
    private String byMonthDay;

    @Column(name = "byyearday")
    private String byYearDay;

    @Column(name = "byweekno")
    private String byWeekNo;

    @Column(name = "bymonth")
    private String byMonth;

    @Column(name = "bysetpos")
    private Integer bySetPos;

    @Column(name = "week_start_day")
    private String weekStartDay;

    public Long getId() {
        return id;
    }

    public EventPO getEvent() {
        return event;
    }

    public void setEvent(EventPO event) {
        this.event = event;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Long getUntil() {
        return until;
    }

    public void setUntil(Long until) {
        this.until = until;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getBySecond() {
        return bySecond;
    }

    public void setBySecond(String bySecond) {
        this.bySecond = bySecond;
    }

    public String getByMinute() {
        return byMinute;
    }

    public void setByMinute(String byMinute) {
        this.byMinute = byMinute;
    }

    public String getByHour() {
        return byHour;
    }

    public void setByHour(String byHour) {
        this.byHour = byHour;
    }

    public String getByDay() {
        return byDay;
    }

    public void setByDay(String byDay) {
        this.byDay = byDay;
    }

    public String getByMonthDay() {
        return byMonthDay;
    }

    public void setByMonthDay(String byMonthDay) {
        this.byMonthDay = byMonthDay;
    }

    public String getByYearDay() {
        return byYearDay;
    }

    public void setByYearDay(String byYearDay) {
        this.byYearDay = byYearDay;
    }

    public String getByWeekNo() {
        return byWeekNo;
    }

    public void setByWeekNo(String byWeekNo) {
        this.byWeekNo = byWeekNo;
    }

    public String getByMonth() {
        return byMonth;
    }

    public void setByMonth(String byMonth) {
        this.byMonth = byMonth;
    }

    public Integer getBySetPos() {
        return bySetPos;
    }

    public void setBySetPos(Integer bySetPos) {
        this.bySetPos = bySetPos;
    }

    public String getWeekStartDay() {
        return weekStartDay;
    }

    public void setWeekStartDay(String weekStartDay) {
        this.weekStartDay = weekStartDay;
    }

    public UserPO getUser() {
        return user;
    }

    public void setUser(UserPO user) {
        this.user = user;
    }
}
