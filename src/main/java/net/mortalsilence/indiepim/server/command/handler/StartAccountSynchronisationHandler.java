package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.StartAccountSynchronisation;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.message.MessageConstants;
import net.mortalsilence.indiepim.server.schedule.AccountIncSyncroJob;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

@Service
public class StartAccountSynchronisationHandler implements Command<StartAccountSynchronisation, BooleanResult>, MessageConstants {

    @Inject private UserDAO userDAO;
    @Inject private MessageDAO messageDAO;
    @Inject private Scheduler scheduler;

    @Transactional
	@Override
    public BooleanResult execute(StartAccountSynchronisation action) {
		
		final UserPO user = userDAO.getUser(ActionUtils.getUserId());
		final MessageAccountPO account = messageDAO.getMessageAccount(user.getId(), action.getAccountId());
        final JobKey jobKey = jobKey("AccountIncSyncroJob", "user_" + account.getUser().getId() + "_account_" + account.getId());
            JobDetail syncJob;
            try {
                syncJob = scheduler.getJobDetail(jobKey);
                if(syncJob == null) {
                    syncJob = newJob(AccountIncSyncroJob.class)
                        .withIdentity("AccountIncSyncroJob", "user_" + account.getUser().getId() + "_account_" + account.getId())
                        .usingJobData("userId", account.getUser().getId())
                        .usingJobData("accountId", account.getId())
                        .storeDurably()
                        .build();
                    scheduler.addJob(syncJob, true);
                }
                scheduler.triggerJob(jobKey);
            } catch (SchedulerException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
		return new BooleanResult(true);
	}

	@Override
	public void rollback(StartAccountSynchronisation arg0, BooleanResult arg1) {
		// TODO how to roll back?
	}
}
