package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 12.11.12
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "image")
@SuppressWarnings("serial")
public class ImagePO {

    @Id
    @GeneratedValue
   	@Column(name = "id")
   	private Long id;

   	@ManyToOne(optional=false)
   	@JoinColumn(name="user_id", referencedColumnName="id")
   	private UserPO user;

    @OneToOne(mappedBy = "photo", targetEntity = ContactPO.class)
    private ContactPO contact;

    @Column(name = "image")
    private byte[] image;

    public UserPO getUser() {
        return user;
    }

    public void setUser(UserPO user) {
        this.user = user;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Long getId() {
        return id;
    }
}
