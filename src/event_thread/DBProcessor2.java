package event_thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import queue.Queue;
import event.Job;
import event.NIOEvent;

public class DBProcessor2 extends Thread {

	private Queue queue = null;
	Connection conn = null;
	Statement stmt = null;
		
	
	public DBProcessor2(Queue queue) {
		this.queue = queue;
		
	}

	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Job job = queue.pop(NIOEvent.DB_WRITE_FRIDGE);
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

	private void response(SocketChannel sc, Job job) {
		String response_Str = "";
		
		
		switch (job.getDetailType()) {
		case 4:
			break;
		case 5:
			break;
		}
	}

	private void closeChannel(SocketChannel sc) {
		try {
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
