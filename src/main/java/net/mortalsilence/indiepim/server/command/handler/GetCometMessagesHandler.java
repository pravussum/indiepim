package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.comet.CometMessage;
import net.mortalsilence.indiepim.server.comet.CometService;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetCometMessages;
import net.mortalsilence.indiepim.server.command.results.CometMessagesResult;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Named
public class GetCometMessagesHandler implements Command<GetCometMessages, CometMessagesResult> {

    @Inject UserDAO userDAO;
    @Inject CometService cometService;

	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
	final public static Integer HEARTBEAT_TIMEOUT = 50;
	
	@Override
	public CometMessagesResult execute(GetCometMessages action) {
		if(logger.isDebugEnabled())
			logger.debug("GetCometMessagesHandler.execute"); 
		final UserPO user = userDAO.getUser(ActionUtils.getUserId());
		if(logger.isDebugEnabled())
			logger.debug("Waiting for comet messages in session with id " + action.getSessionId());
		final BlockingQueue<CometMessage> cometQueue = cometService.getCometMessageQueue(user.getId(), action.getSessionId());
        if(cometQueue == null)
            throw new RuntimeException("No comet queue for current session!");
		final Collection<CometMessage> messages = new LinkedList<CometMessage>();
		CometMessage curMsg;
		/* receive waiting messages from the queue (blocking!). max for the heartbeat time */
		if(cometQueue.peek() != null) {
			/* messages available -- send them to the client */
			while((curMsg = cometQueue.poll()) != null) {
				messages.add(curMsg);
			}
			if(logger.isDebugEnabled())
				logger.debug("Returning comet messages");
			return new CometMessagesResult(messages);
		} else {
			/* no available messages -- wait for the next one (blocking) */
			try {
				curMsg = cometQueue.poll(HEARTBEAT_TIMEOUT, TimeUnit.SECONDS);
				if(curMsg == null) {
					/* timeout occured? send heartbeat to the client */
					final CometMessagesResult result = new CometMessagesResult();
					result.setHeartbeat(true);
					if(logger.isDebugEnabled())
						logger.debug("Returning empty comet message (heartbeat).");
					return result;					
				} else {
					// TODO handle multiple messages?
					messages.add(curMsg);
					if(logger.isDebugEnabled())
						logger.debug("Returning single comet message");
					return new CometMessagesResult(messages);					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				final CometMessagesResult result = new CometMessagesResult();
				result.setError(true);
				result.setErrorMessage(e.getMessage());
				return result;
			}
		}
	}

	@Override
	public void rollback(GetCometMessages arg0, CometMessagesResult arg1) {
		// N/A
	}

}
