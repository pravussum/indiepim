package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.MessageDTO;

import java.util.List;

public class MessageStatsResult implements Result {

    List<Long> lastTenDaysCount;

	public MessageStatsResult() {
	}

	public MessageStatsResult(List<Long> lastTenDaysCount) {
		this.lastTenDaysCount = lastTenDaysCount;
	}

    public List<Long> getLastTenDaysCount() {
        return lastTenDaysCount;
    }
}
