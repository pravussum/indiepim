package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.DeleteMessageAccount;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

import static org.quartz.JobKey.jobKey;

@Named
public class DeleteMessageAccountHandler implements Command<DeleteMessageAccount, BooleanResult> {

    @Inject
    private MessageDAO messageDAO;
    @Inject
    private GenericDAO genericDAO;

	@Transactional
    @Override
    public BooleanResult execute(DeleteMessageAccount action) {

		MessageAccountPO account = messageDAO.getMessageAccount(ActionUtils.getUserId(), action.getAccountId());
		// TODO: check if account exists and if it is owned by user

		/* Delete Sync Job */
		// Schedule the job with the trigger
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			Scheduler scheduler = sf.getScheduler();		
			scheduler.deleteJob(jobKey("AccountIncSyncroJob_" + account.getId(),  "user_" + account.getUser().getId()));		
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// TODO: warning to user
		genericDAO.remove(account);
		
		return new BooleanResult(true);
	}

	@Override
	public void rollback(DeleteMessageAccount arg0, BooleanResult arg1) {
		// TODO implement undo
	}

}
