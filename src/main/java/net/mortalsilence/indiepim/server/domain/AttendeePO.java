package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;

@Entity
@Table(name = "attendee")
public class AttendeePO {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional=true)
    @JoinColumn(name="user_id", referencedColumnName="id")
    private UserPO user;

    @ManyToOne(optional = true)
    @JoinColumn(name = "email_address_id", referencedColumnName = "id")
    private EmailAddressPO emailAddressPO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private EventPO event;
}
