package com.syddraf.dim.model;

import com.google.gson.Gson;

public class DIMMessage {

	// Mandatory Fields
	private String to = "";
	private String from = "";
	private long unixTime = 0;
	private MessageType type = MessageType.TEXT_MESSAGE;
	private String message = "";
	
	// Optional Fields
	private String fileName = "";
	private long dataSize = 0;
	public String serialize() { return new Gson().toJson(this);}
	
	// Getters
	public String headerTo() { return this.to; }
	public String headerFrom() { return this.from; }
	public long headerUnixTime() { return this.unixTime; }
	public MessageType headerType() { return this.type; }
	public String bodyMessage() { return this.message; }
	public String bodyFileName() { return this.fileName; }
	public long bodyDataSize() { return this.dataSize; }
	
	// Setters
	public void headerTo(String to) { this.to = to; }
	public void headerFrom(String from) { this.from = from; }
	public void headerUnixTime(long time) { this.unixTime = time; }
	public void headerType(MessageType type) { this.type = type; }
	public void bodyMessage(String message) { this.message = message; }
	public void bodyFileName(String fileName) { this.fileName = fileName; }
	public void bodyDataSize(long dataSize) { this.dataSize = dataSize; }
}
