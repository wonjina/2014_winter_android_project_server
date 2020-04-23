package event_thread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import event.Job;
import event.NIOEvent;

import queue.Queue;

public class HardwareProcessor extends Thread {

	private Queue queue = null;
	private ByteBuffer buf = null;

	public HardwareProcessor(Queue queue) {
		this.queue = queue;
		this.buf = buf.allocateDirect(1);
	}

	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Job job = queue.pop(NIOEvent.HARD_CONTROL);
				SelectionKey key = (SelectionKey) job.getSession().get("SelectionKey");
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

		buf.put(Integer.toString(job.getDetailType()).getBytes());
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
