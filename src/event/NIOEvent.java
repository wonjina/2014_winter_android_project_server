package event;

public interface NIOEvent {
	final static int MYINFOR_WRITE = 3; // ������ ��û (Ŭ���̾�Ʈ�� ���� �ٽ� ���������)
	final static int DISEASE_WRITE = 4; // ���� ��û (Ŭ���̾�Ʈ�� ���� �ٽ� ���������)
	final static int FRIDGE_WRITE = 5; // ������û (Ŭ���̾�Ʈ�� ���� �ٽ� ���������)
	final static int RECIPI_WRITE = 6; // ������ ��û (Ŭ���̾�Ʈ�� ���� �ٽ� ���������)
	final static int DB_WRITE_FRIDGE = 7; // DB���� ��û
	final static int HARD_CONTROL = 8; // �ϵ�������� �̺�Ʈ
	final static int DB_WRITE_MY = 9; 
}
