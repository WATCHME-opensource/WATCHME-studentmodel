package eu.watchme.sm.datatypes;

public class TargetToken {
	private TextToken token;
	private String wid;

	public TextToken getToken() {
		return token;
	}

	public String getWid() {
		return wid;
	}

	public void setToken(TextToken token) {
		this.token = token;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	@Override
	public String toString() {
		return "{\"wid\":\"" + wid + "\", \"token\":" + token + "}";
	}
}
