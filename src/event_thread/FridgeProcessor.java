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

public class FridgeProcessor extends Thread {

	private Queue queue = null;
	private ByteBuffer buf = null;
	
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	public FridgeProcessor(Queue queue) {
		this.queue = queue;
		this.buf = buf.allocateDirect(1024);
		
	}

	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Job job = queue.pop(NIOEvent.FRIDGE_WRITE);
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
			e.printStackTrace();
		}
	}

	private void response(SocketChannel sc, Job job) throws IOException {
		
		String response_Str = "";
		
		switch (job.getDetailType()) {
		case 1:
			response_Str = ",재료이름1,재료이름2,재료이름3";
			break;
		case 2:
			response_Str = "&재료이름1,2014,08,31,Freezer&재료이름2,2014,09,21,Fridge&재료이름1,2014,09,14,Freezer";
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
			e.printStackTrace();
		}
	}

	private void clearBuffer(ByteBuffer buf) {
		buf.clear();
	}
	
}
