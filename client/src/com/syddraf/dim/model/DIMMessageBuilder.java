package com.syddraf.dim.model;

public class DIMMessageBuilder {

	private DIMMessage msg = new DIMMessage();
	
	public DIMMessageBuilder setTo(String to) {
		this.msg.headerTo(to);
		return this;
	}
	
	public DIMMessageBuilder setFrom(String from) {
		this.msg.headerFrom(from);
		return this;
	}
	
	public DIMMessageBuilder setType(MessageType type) {
		this.msg.headerType(type);
		return this;
	}
	
	public DIMMessageBuilder setMessage(String msg) {
		this.msg.bodyMessage(msg);
		return this;
	}
	
	public DIMMessageBuilder setFileName(String fileName) {
		this.msg.bodyFileName(fileName);
		return this;
	}
	
	public DIMMessageBuilder setDataSize(long dataSize) {
		this.msg.bodyDataSize(dataSize);
		return this;
	}
	
	public DIMMessage generate() {
		return this.msg;
	}
	
}
