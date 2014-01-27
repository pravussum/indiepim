package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DeleteMessagesResult implements Result, Serializable{

	private Map<Long, DeleteResultInfo> result = new HashMap<Long, DeleteResultInfo>();

    public DeleteMessagesResult() {
	}

	public Map<Long, DeleteResultInfo> getResult() {
		return result;
	}

	public DeleteMessagesResult(Map<Long, DeleteResultInfo> result) {
		this.result = result;
	} 
	
	public void setResult(Map<Long, DeleteResultInfo> result) {
		this.result = result;
	}
	

}
