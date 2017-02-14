package eu.watchme.sm.datatypes;

public class TextToken {
    private String length;
    private String offset;
    private String para;
    private String sent;
    private String value;
    private String wid;

    public String getLength() {
	return length;
    }

    public String getOffset() {
	return offset;
    }

    public String getPara() {
	return para;
    }

    public String getSent() {
	return sent;
    }

    public String getValue() {
	return value;
    }

    public String getWid() {
	return wid;
    }

    public void setLength(String length) {
	this.length = length;
    }

    public void setOffset(String offset) {
	this.offset = offset;
    }

    public void setPara(String para) {
	this.para = para;
    }

    public void setSent(String sent) {
	this.sent = sent;
    }

    public void setValue(String token) {
	this.value = token;
    }

    public void setWid(String wid) {
	this.wid = wid;
    }

    @Override
    public String toString() {
	return "{\"value\":\"" + value + "\", \"wid\":\"" + wid + "\", \"sent\":\"" + sent
		+ "\", \"para\":\"" + para + "\", \"offset\":\"" + offset + "\", \"length\":\""
		+ length + "\"}";
    }

}
