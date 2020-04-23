package event_thread;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import queue.Queue;
import event.Job;
import event.NIOEvent;

public class RecipiProcessor extends Thread {

	private Queue queue = null;
	private ByteBuffer buf = null;
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	BufferedReader in ;
	
	String[] str;	//레시피 재료 저장
	String[] str2;	//냉장고 재료 배열로 저장 2차 저장
	String ingredients = "";	//냉장고 재료 1차저장
	
	
	public RecipiProcessor(Queue queue) {
		this.queue = queue;
		this.buf = buf.allocateDirect(1024);
	}

	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Job job = queue.pop(NIOEvent.RECIPI_WRITE);
				SelectionKey key = (SelectionKey) job.getSession().get(
						"SelectionKey");
				SocketChannel sc = (SocketChannel) key.channel();

				try {
					response(sc, job);
				} catch (Exception e) {
					closeChannel(sc);
				}
			}
		} catch (Exception e) {
			try {
				in.close();
			} catch (IOException e1) {		e1.printStackTrace();	}
			e.printStackTrace();
		}
	}

	private void response(SocketChannel sc, Job job) throws IOException {
		
		String response_Str = "";
		
		switch (job.getDetailType()) {
		case 1:
			response_Str += ",레시피이름1,레시피이름2,레시피이름3";
			break;
		case 2:
			response_Str += "레시피이름,효과,필요재료,요리법,부족재료&";
			break;
		}

		System.out.println("보내는거 : " + response_Str);

		buf.put(response_Str.getBytes());
		buf.flip();

		if (sc != null && sc.isConnected()) {
			while (buf.hasRemaining()) {
				sc.write(buf);
			}  
		}

		clearBuffer(buf);
	}
	
	

	private void closeChannel(SocketChannel sc) {
		try {
			sc.close();
		} catch (Exception e) {
			try {
				in.close();
			} catch (IOException e1) {		e1.printStackTrace();	}
			e.printStackTrace();
		}
	}

	private void clearBuffer(ByteBuffer buf) {
		buf.clear();
	}
	
}
