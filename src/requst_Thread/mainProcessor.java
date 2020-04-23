package requst_Thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import event_thread.DBProcessor;
import event_thread.DBProcessor2;
import event_thread.DiseaseProcessor;
import event_thread.FridgeProcessor;
import event_thread.HardwareProcessor;
import event_thread.MyInforProcessor;
import event_thread.RecipiProcessor;

import queue.BlockingEventQueue;
import queue.Queue;

public class mainProcessor {

	private static final String HOST = "220.149.119.118";
	private static final int PORT = 9730;

	private static ServerSocketChannel ssc = null;
	private static ServerSocket ssk = null;
	private static Selector selector = null;
	private Queue queue = null;
	RequstProcessor rp;
	MyInforProcessor mp;
	RecipiProcessor recipi;
	FridgeProcessor fp;
	DiseaseProcessor dp;
	DBProcessor dbp;
	HardwareProcessor hp;
	DBProcessor2 dp2;

	public void initSever() {
		try {

			queue = BlockingEventQueue.getInstance();

			rp = new RequstProcessor(queue);
			mp = new MyInforProcessor(queue);
			recipi = new RecipiProcessor(queue);
			fp = new FridgeProcessor(queue);
			dp = new DiseaseProcessor(queue);
			dbp = new DBProcessor(queue);
			hp = new HardwareProcessor(queue);
			dp2 = new DBProcessor2(queue);
			
			selector = Selector.open();
			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ssk = ssc.socket();
			InetSocketAddress isa = new InetSocketAddress(HOST, PORT);
			ssk.bind(isa);

			ssc.register(this.selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void stopServer() {
		try {
			ssk.close();
			ssc.close();
			selector.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startServer() {
		rp.start();
		mp.start();
		recipi.start();
		fp.start();
		dp.start();
		dbp.start();
		hp.start();
		dp2.start();

		try {
			while (!Thread.currentThread().isInterrupted()) {
				selector.select();
				acceptPendingConnections();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 연결요청온 클라이언트의 소켓채널을 구함.
	private void acceptPendingConnections() throws Exception {
		Iterator iter = selector.selectedKeys().iterator();

		while (iter.hasNext()) {
			SelectionKey key = (SelectionKey) iter.next();
			iter.remove();

			ServerSocketChannel readyChannel = (ServerSocketChannel) key
					.channel();
			SocketChannel sc = readyChannel.accept();

			System.out.println(" Accept (" + sc.socket().getInetAddress()
					+ "). ");

			pushMyJob(sc);
		}
	}

	// 구한 소캣체널을 RequstProcessor의 vector에 저장
	private void pushMyJob(SocketChannel sc) {
		rp.addClient(sc);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		mainProcessor mp = new mainProcessor();

		mp.initSever();
		mp.startServer();
		stopServer();

	}

}
