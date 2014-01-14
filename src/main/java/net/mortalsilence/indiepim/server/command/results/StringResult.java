package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;

public class StringResult implements Result {
	
	private String result;

	public StringResult() {
	}

	public String getResult() {
		return result;
	}

	public StringResult(String result) {
		this.result = result;
	}

	public void setResult(String result) {
		this.result = result;
	} 

}
