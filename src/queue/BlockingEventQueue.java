package queue;

import java.util.ArrayList;
import java.util.List;

import event.Job;
import event.NIOEvent;

public class BlockingEventQueue implements Queue {

	private final Object myinfor_Monitor = new Object();
	private final Object disease_Monitor = new Object();
	private final Object fridge_Monitor = new Object();
	private final Object recipi_Monitor = new Object();
	private final Object db_Monitor_fridge = new Object();
	private final Object db_Monitor_state_fridge = new Object();
	private final Object db_Monitor_my = new Object();
	private final Object db_Monitor_state_my = new Object();	
	private final Object hard_Monitor = new Object();

	private final List<Job> myinfor_Queue = new ArrayList<Job>();
	private final List<Job> disease_Queue = new ArrayList<Job>();
	private final List<Job> fridge_Queue = new ArrayList<Job>();
	private final List<Job> recipi_Queue = new ArrayList<Job>();
	private final List<Job> db_fridge_Queue = new ArrayList<Job>();
	private final List<Job> db_my_Queue = new ArrayList<Job>();
	private final List<Job> hard_Queue = new ArrayList<Job>();

	// BlockingEventQueue 하나만 생성할 수 있도록 싱글톤 패턴을 사용
	private static BlockingEventQueue instance = new BlockingEventQueue();

	public static BlockingEventQueue getInstance() {
		return instance;
	}

	private BlockingEventQueue() {
	}

	public Job pop(int eventType) {
		switch (eventType) {
		case NIOEvent.MYINFOR_WRITE:
			return getMyinforJob();
		case NIOEvent.DISEASE_WRITE:
			return getDiseaseJob();
		case NIOEvent.FRIDGE_WRITE:
			return getFridgeJob();
		case NIOEvent.RECIPI_WRITE:
			return getRecipiJob();
		case NIOEvent.DB_WRITE_FRIDGE:
			return getDB_fridge_Job();
		case NIOEvent.HARD_CONTROL:
			return gethardControl();
		case NIOEvent.DB_WRITE_MY:
			return getDB_my_Job();
		default:
			throw new IllegalArgumentException("Illegal EventType");
		}
	}

	public void push(Job job) {
		if (job != null) {
			int eventType = job.getEventType();
			switch (eventType) {
			case NIOEvent.MYINFOR_WRITE:
				putMyinforJob(job);
				break;
			case NIOEvent.DISEASE_WRITE:
				putDiseaseJob(job);
				break;
			case NIOEvent.FRIDGE_WRITE:
				putFridgeJob(job);
				break;
			case NIOEvent.RECIPI_WRITE:
				putRecipiJob(job);
				break;
			case NIOEvent.DB_WRITE_FRIDGE:
				putDB_fridge_Job(job);
				break;
			case NIOEvent.HARD_CONTROL:
				puthardControl(job);
				break;
			case NIOEvent.DB_WRITE_MY:
				putDB_my_Job(job);
				break;
			default:
				throw new IllegalArgumentException("Illegal EventType..");
			}
		}
	}

	// get///////////////////////////////////////////////////////////////////
	private Job gethardControl() {
		
		synchronized (hard_Monitor) {
			if (hard_Queue.isEmpty()) {
				try {
					hard_Monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return (Job) hard_Queue.remove(0);
	}


	private Job getDiseaseJob() {
		synchronized (disease_Monitor) {
			if (disease_Queue.isEmpty()) {
				try {
					disease_Monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return (Job) disease_Queue.remove(0);

	}

	private Job getFridgeJob() {
		synchronized (fridge_Monitor) {
			if (fridge_Queue.isEmpty()) {
				try {
					fridge_Monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if (!db_fridge_Queue.isEmpty()) {
			synchronized (db_Monitor_state_fridge) {
				try {
					db_Monitor_state_fridge.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return (Job) fridge_Queue.remove(0);

	}

	private Job getRecipiJob() {
		synchronized (recipi_Monitor) {
			if (recipi_Queue.isEmpty()) {
				try {
					recipi_Monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return (Job) recipi_Queue.remove(0);

	}

	private Job getDB_fridge_Job() {
		synchronized (db_Monitor_fridge) {
			if (db_fridge_Queue.isEmpty()) {
				synchronized (db_Monitor_state_fridge) {
					db_Monitor_state_fridge.notifyAll();
				}
				try {
					db_Monitor_fridge.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return (Job) db_fridge_Queue.remove(0);
		}
	}
	
	
	// put//////////////////////////////////////////////////////////////////
	private void puthardControl(Job job) {
		synchronized (hard_Monitor) {
			hard_Queue.add(job);
			hard_Monitor.notify();
		}
	}


	private Job getMyinforJob() {
		synchronized (myinfor_Monitor) {
			if (myinfor_Queue.isEmpty()) {
				try {
					myinfor_Monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (!db_my_Queue.isEmpty()) {
			synchronized (db_Monitor_state_my) {

				try {
					db_Monitor_state_my.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return (Job) myinfor_Queue.remove(0);
	}
	private void putMyinforJob(Job job) {
		synchronized (myinfor_Monitor) {
			myinfor_Queue.add(job);
			myinfor_Monitor.notify();
		}
	}
	private Job getDB_my_Job() {
		synchronized (db_Monitor_my) {
			if (db_my_Queue.isEmpty()) {
				synchronized (db_Monitor_state_my) {
					db_Monitor_state_my.notifyAll();
				}
				try {
					db_Monitor_my.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return (Job) db_my_Queue.remove(0);
		}
	}
	private void putDB_my_Job(Job job) {
		synchronized (db_Monitor_my) {
			db_my_Queue.add(job);
			db_Monitor_my.notify();
		}
	}

	private void putDiseaseJob(Job job) {
		synchronized (disease_Monitor) {
			disease_Queue.add(job);
			disease_Monitor.notify();
		}
	}

	private void putFridgeJob(Job job) {
		synchronized (fridge_Monitor) {
			fridge_Queue.add(job);
			fridge_Monitor.notify();
		}
	}

	private void putRecipiJob(Job job) {
		synchronized (recipi_Monitor) {
			recipi_Queue.add(job);
			recipi_Monitor.notify();
		}
	}

	private void putDB_fridge_Job(Job job) {
		synchronized (db_Monitor_fridge) {
			db_fridge_Queue.add(job);
			db_Monitor_fridge.notify();
		}
	}
	

}
