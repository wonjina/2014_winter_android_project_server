package event;

public interface NIOEvent {
	final static int MYINFOR_WRITE = 3; // 내정보 요청 (클라이언트로 정보 다시 보내줘야함)
	final static int DISEASE_WRITE = 4; // 질병 요청 (클라이언트로 정보 다시 보내줘야함)
	final static int FRIDGE_WRITE = 5; // 냉장고요청 (클라이언트로 정보 다시 보내줘야함)
	final static int RECIPI_WRITE = 6; // 레시피 요청 (클라이언트로 정보 다시 보내줘야함)
	final static int DB_WRITE_FRIDGE = 7; // DB수정 요청
	final static int HARD_CONTROL = 8; // 하드웨어제어 이벤트
	final static int DB_WRITE_MY = 9; 
}
