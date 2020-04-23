package requst_Thread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import queue.Queue;
import event.Job;
import event_thread.MyInforProcessor;

public class RequstProcessor extends Thread {

	private Selector selector = null; // 연결요청한 클라이언트를 read로 변경해서 등록
	private Selector selector_write = null; // 하드웨어 소켓을 등록
	private Queue queue = null;
	private Vector newClients = new Vector(); // 연결요청이 온 클라이언트

	private ByteBuffer header = ByteBuffer.allocateDirect(1);
	private ByteBuffer detail_header = ByteBuffer.allocateDirect(1);
	private ByteBuffer body = ByteBuffer.allocateDirect(1024);
	private ByteBuffer buffers[] = { header, detail_header, body };

	MyInforProcessor mp;

	public RequstProcessor(Queue queue) {
		this.queue = queue;
		// 셀렉터 열기
		try {
			selector = Selector.open();
			selector_write = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				processNewConnection();
				int keysReady = selector.select(1000);
				if (keysReady > 0) {
					processRequest();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void processNewConnection()
			throws ClosedChannelException {
		Iterator iter = newClients.iterator();
		try {
			while (iter.hasNext()) {
				SocketChannel sc = (SocketChannel) iter.next();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		newClients.clear();
	}

	private void processRequest() {
		Iterator iter = selector.selectedKeys().iterator();
		while (iter.hasNext()) {
			SelectionKey key = (SelectionKey) iter.next();
			iter.remove();
			categorize(key);
		}
	}

	private void categorize(SelectionKey key) {
		try {
			long read = 0;
			SocketChannel sc = (SocketChannel) key.channel();

			for (int i = 0; i < 2; i++) {
				read = sc.read(buffers);
				if (read == -1) {
					bufferClear(buffers);
					key.cancel();
					return;
				}
			}

			header.flip();
			detail_header.flip();
			body.flip();

			byte event_Type = header.get();
			event_Type -= 48;

			byte[] bytearr = new byte[body.remaining()];
			body.get(bytearr);
			String str_body = new String(bytearr);
			int detail_type = (detail_header.get() - 48);
			System.out.println("받은데이터 :  Event_Type (" + event_Type
					+ detail_type + "). " + str_body);

			switch (event_Type) {
			case 1:
				SocketChannel sc_hard = (SocketChannel) ((SocketChannel) key.channel());
				sc_hard.configureBlocking(false);
				sc_hard.register(selector_write, SelectionKey.OP_WRITE);
				// selector에서 key를 제거 하는 코드 추가해야됨(read로 등록되었으므로)
				break;
			case 3:
				pushMyJob(key, 3, detail_type, str_body);
				break;
			case 4:
				pushMyJob(key, 4, 4, str_body);
				break; // 질병검색은 종류가 한개이므로 걍 4 넘겨줌
			case 5:
				pushMyJob(key, 5, detail_type, str_body);
				break;
			case 6:
				pushMyJob(key, 6, detail_type, str_body);
				break;
			case 7:
				pushMyJob(key, 7, detail_type, str_body);
				break;
			case 8:
				selector_write.select(500);
				Iterator iter = selector_write.selectedKeys().iterator();
				if(iter.hasNext())
				{
					pushMyJob((SelectionKey) iter.next(), 8, detail_type, "");
					iter.remove();
				}
				break;
			case 9:
				pushMyJob(key, 9, detail_type, str_body);
				break;
			default:
				throw new IllegalArgumentException("Illegal EventType..");
			}
		} catch (IOException e) {
			key.cancel();
			e.printStackTrace();
		}

		bufferClear(buffers);
	}

	private void pushMyJob(SelectionKey key, int event_Type, int detail_type,
			String body) {
		Map session = new HashMap();
		session.put("SelectionKey", key);
		Job job = new Job(event_Type, detail_type, session, body);

		queue.push(job);
	}

	public void addClient(SocketChannel sc) {
		newClients.add(sc);
	}

	public void bufferClear(ByteBuffer buffers[]) {
		for (int index = 0; index < 3; index++) {
			if (buffers[index] != null) {
				buffers[index].clear();
			}
		}
	}

}
