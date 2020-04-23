package event;

import java.util.Map;

//클라이언트로 부터 받은 요청을 객체화할때 사용할 클래스
public class Job {

	private int eventType; // 요청 타입
	private Map session = null;
	private String body; // 요청을 하는데 필요한 정보로 없을수도잇고 잇을수도잇다.
	private int detail_type;

	public Job() {
	}

	public Job(int head, int detail_type, Map session, String body) {
		this.eventType = head;
		this.detail_type = detail_type;
		this.session = session;
		if (body != null)
			this.body = body;
	}

	public void setSession(Map session) {
		this.session = session;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public Map getSession() {
		return session;
	}

	public int getEventType() {
		return eventType;
	}

	public int getDetailType() {
		return detail_type;
	}

	public String getBody() {
		return body;
	}

}
