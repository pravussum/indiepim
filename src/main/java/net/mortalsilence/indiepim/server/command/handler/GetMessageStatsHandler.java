package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetMessageStats;
import net.mortalsilence.indiepim.server.command.results.MessageStatsResult;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.exception.NotImplementedException;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class GetMessageStatsHandler implements Command<GetMessageStats, MessageStatsResult> {

    @Inject
    private MessageDAO messageDAO;

    @Transactional(readOnly = true)
	@Override
    public MessageStatsResult execute(GetMessageStats action) {
		
		if(GetMessageStats.STATS_TYPE.COUNT_LAST_TEN_DAYS == action.getStatsType()) {
            List<Long> result = messageDAO.getMsgCountForLastTenDays(ActionUtils.getUserId());
            return new MessageStatsResult(result);
        }
        throw new NotImplementedException("MessageStatsResult");
	}

	@Override
	public void rollback(GetMessageStats arg0, MessageStatsResult arg1) {
		// get method, no use to roll back
	}
	

}
