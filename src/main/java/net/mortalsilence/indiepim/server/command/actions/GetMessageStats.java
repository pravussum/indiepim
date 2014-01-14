package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.MessageStatsResult;

public class GetMessageStats extends AbstractSessionAwareAction<MessageStatsResult> {

    public static enum STATS_TYPE {
        COUNT_LAST_TEN_DAYS
    }

	public GetMessageStats() {

	}
	private STATS_TYPE statsType;

	public GetMessageStats(STATS_TYPE stats_type) {
		this.statsType = stats_type;
	}

    public STATS_TYPE getStatsType() {
        return statsType;
    }
}
