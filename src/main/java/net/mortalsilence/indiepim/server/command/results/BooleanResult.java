package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BooleanResult implements Result, Serializable{
	
	private Boolean result;

	public BooleanResult() {
	}

	public Boolean getResult() {
		return result;
	}

	public BooleanResult(Boolean result) {
		this.result = result;
	} 
	
	public void setResult(Boolean result) {
		this.result = result;
	}
	

}
