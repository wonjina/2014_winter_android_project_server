package event;

import java.util.Map;

//Ŭ���̾�Ʈ�� ���� ���� ��û�� ��üȭ�Ҷ� ����� Ŭ����
public class Job {

	private int eventType; // ��û Ÿ��
	private Map session = null;
	private String body; // ��û�� �ϴµ� �ʿ��� ������ ���������հ� ���������մ�.
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
