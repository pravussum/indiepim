package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "msg_account_stats")
public class MessageAccountStatsPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name = "lastSyncRun")
    private Date lastSyncRun;

    @Column (name = "msg_sync_error_cnt")
    private Integer syncErrorCount;

    @OneToOne (mappedBy = "messageAccountStats")
    private MessageAccountPO messageAccount;

    public Long getId() {
        return id;
    }

    public Date getLastSyncRun() {
        return lastSyncRun;
    }

    public void setLastSyncRun(Date lastSyncRun) {
        this.lastSyncRun = lastSyncRun;
    }

    public Integer getSyncErrorCount() {
        return syncErrorCount;
    }

    public void setSyncErrorCount(Integer syncErrorCount) {
        this.syncErrorCount = syncErrorCount;
    }

    public MessageAccountPO getMessageAccount() {
        return messageAccount;
    }

    public void setMessageAccount(MessageAccountPO messageAccount) {
        this.messageAccount = messageAccount;
    }
}
