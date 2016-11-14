/*   @(#)  TaskManager.java  2002-02-04
*
*  Copyright(C) 2002, All Rights Reserved.
*  Ahlquist.com
*  516 Suisse Drive
*  San Jose, California 95123
*  U.S.A.
*
*  This document contains information proprietary and confidential to
*  Ahlquist.com, which is either copyrighted or which a
*  patent has been applied and/or protected by trade secret laws.
*
*  This document, or any parts thereof, may not be used, disclosed,
*  or reproduced in any form, by any method, or for any purpose without
*  the express written permission of Ahlquist.com.
*
*
*/

package com.ahlquist.common.util;

import java.util.Date;
import org.apache.log4j.Logger;

/**
 * The TaskManager oversees asynchronous activities. Each task represents work
 * to do.
 *
 * Concurrency:
 * <li>the TaskManager lock is used for waiting until all tasks are done. also
 * for starting tasks -- necessary?
 */
public class TaskManager extends MyObject {
	
	final static Logger logger = Logger.getLogger(TaskManager.class);

	private TaskLists taskLists = new TaskLists();

	/** the name of the task manager. Initialized in constructor. */
	private String name;

	/**
	 * true iff completed tasks should be saved and returned from
	 * waitAllComplete; by default, completed tasks are discarded.
	 */
	private boolean fSaveCompletedTasks = false;

	/** the priority to set for all threads we create. */
	private int threadPriority = Thread.NORM_PRIORITY;

	/**
	 * how long each worker thread will stick around when no work is available
	 * for it. Threads will idle for this long, then exit. Provides simple
	 * thread reuse.
	 */
	private static final long msThreadIdleBeforeDie = 300 * 1000; // CONFIG

	/** how many threads we will run concurrently. */
	private static final int threadsMax = 20; // CONFIG

	public TaskManager(String name) {
		this.name = name;
	}

	public boolean getSaveCompletedTasks() {
		return (fSaveCompletedTasks);
	}

	public void setSaveCompletedTasks(boolean fSaveCompletedTasks) {
		this.fSaveCompletedTasks = fSaveCompletedTasks;
	}

	public int getThreadPriority() {
		return (threadPriority);
	}

	public void setThreadPriority(int threadPriority) {
		this.threadPriority = threadPriority;
	}

	public String toString() {
		return (name);
	}

	public synchronized void startTask(Task task) {
		taskLists.startTask(task);
	}

	/*
	 * Caller must enable this feature via setSaveCompletedTasks.
	 * 
	 * @return the next task to complete (may wait until one completes); null if
	 * no tasks remain. public synchronized Task fetchNextCompletedTask() { //
	 * REVIEW: This is a good performance enhancement. It will require //
	 * changing waitAllComplete/isAllDone. }
	 */

	/**
	 * Waits here until all tasks are complete.
	 *
	 * @return VArray of Task: all tasks completed since the last call to this
	 *         method (empty unless fSaveCompletedTasks has been enabled). This
	 *         array will be empty unless the caller enabled
	 *         setSaveCompletedTasks.
	 */
	public synchronized VArray waitAllComplete() {
		while (!taskLists.isAllDone()) {
			try {
				wait();
			} catch (InterruptedException ex) {
				logger.error("Unexpected interrupt", ex);
			}
		}

		return (taskLists.fetchClearAllDone());
	}

	private synchronized void notifyAllComplete() {
		notify();
	}

	/**
	 * This class manages the tasks. Threads wait on this lock for work.
	 *
	 * Concurrency: This lock is below the parent TaskManager lock; we must not
	 * acquire our parent's lock after our own.
	 */
	class TaskLists {
		private VArray tasksTodo = new VArray();
		private VArray tasksDoing = new VArray();
		private VArray tasksDone = new VArray();

		private int threadsIdle = 0;
		private int threadsBusy = 0;

		synchronized void startTask(Task task) {
			// Log.println("Starting task: " + task);
			tasksTodo.addElement(task);

			// REVIEW: If startTask is called too many times in succession, we
			// will not create enough threads (since the threadsIdle count will
			// not be adjusted to take into account
			if (tasksTodo.size() <= threadsIdle) {
				notify(); // Awaken one thread.
			} else if (threadsIdle + threadsBusy < threadsMax) {
				new TaskThread().start(); // Create one thread.
				threadsIdle++;
			}
		}

		synchronized boolean isAllDone() {
			return (tasksTodo.size() + tasksDoing.size() == 0);
		}

		synchronized VArray fetchClearAllDone() {
			Debug._assert(isAllDone());
			VArray tasks = tasksDone;
			tasksDone = new VArray();
			return (tasks);
			//try { return(tasksDone); } finally { tasksDone = new VArray(); }
		}

		/** Called by TaskThread. */
		void taskComplete(Task task) {
			synchronized (this) {
				boolean removed = tasksDoing.removeElement(task);
				Debug._assert(removed);
				if (fSaveCompletedTasks)
					tasksDone.addElement(task);
				threadsBusy--;
				threadsIdle++;
			}

			// We must not hold the TaskList lock when we invoke our parent.
			if (isAllDone())
				notifyAllComplete();
		}

		/**
		 * Called from TaskThread context. Thread waits here for next task.
		 *
		 * @return task to perform; null if thread should exit.
		 */
		synchronized Task getNextTask() {
			Debug._assert(threadsIdle > 0); // Calling thread must be idle.

			// Wait for a task to do, or for too much time to elapse
			long msIdling = 0;
			while (tasksTodo.size() == 0 && msIdling < msThreadIdleBeforeDie) {
				long msBeginWait = MyTime.getCurrentMillis();
				try {
					wait(msThreadIdleBeforeDie);
				} catch (InterruptedException ex) {
					logger.error("TaskManagerThread interrupted",ex);
				}
				msIdling += (MyTime.getCurrentMillis() - msBeginWait);
			}

			// Advise calling thread to die if there is no work (we timed out).
			if (tasksTodo.size() == 0) {
				threadsIdle--;
				return (null); // thread should die
			}

			// Return next task to thread.
			Task task = (Task) tasksTodo.removeFirstElement();
			tasksDoing.addElement(task);
			threadsBusy++;
			threadsIdle--;
			return (task);
		}
	}

	class TaskThread extends Thread {
		TaskThread() {
			setDaemon(true);
			setThreadPriority(getThreadPriority());
		}

		public void run() {
			long msStart = MyTime.getCurrentMillis();
			logger.debug("TaskManager thread starting @ " + new Date(msStart));
			int tasksPerformed = 0;
			Task task;
			while ((task = taskLists.getNextTask()) != null) {
				logger.debug("Task starting: " + task);
				task.doTask();
				logger.debug("Task done: " + task);
				taskLists.taskComplete(task);
				tasksPerformed++;
			}

			long msElapsed = MyTime.getCurrentMillis() - msStart;
			int secElapsed = (int) (msElapsed / 1000);
			logger.debug("TaskManager thread exiting @ " + new Date() +
			 " (" + secElapsed + " seconds elapsed) " +
			 " after performing " + tasksPerformed + " tasks.");
		}
	}

	/** Implements a simple test of the TaskManager. */
	public static void main(String[] args) {
		// Parse arguments to this program.
		if (args.length != 1)
			failMain("Exactly one argument required");

		int cThreads = Numbers.getInt(args[0], -1);
		if (cThreads < 0)
			failMain("Invalid #threads: " + args[0]);

		runTest(cThreads);
		System.out.println("--- waiting a bit before next test ---");
		Util.sleepMs(5000);
		runTest(cThreads);
	}

	private static void runTest(int cThreads) {
		System.out.println("Starting test with " + cThreads + " tasks.");
		TaskManager tm = new TaskManager("Test TaskManager");
		for (int id = 0; id < cThreads; id++)
			tm.startTask(new TestTask(id + 1));

		System.out.println("Waiting for tasks to finish");
		tm.waitAllComplete();
		System.out.println("All tasks have finished.");
	}

	private static void failMain(String message) {
		System.err.println("ERROR: " + message);
		System.err.println("Args: <#threads>");
		System.exit(1);
	}
}

/** Used for testing TaskManager. */
class TestTask implements Task {
	private int id;

	TestTask(int id) {
		this.id = id;
	}

	public void doTask() {
		Util.sleepMs((long) (1000 * Math.random()));
		System.out.println("    Task " + id + " complete.");
	}

	public String toString() {
		return ("TestTask-" + id);
	}
}

/*
 * FUTURE??? public interface ObjectProcessor { public Object
 * processObject(Object oRequest) }
 * 
 * public class ObjectProcessorTask implements Task { Object oRequest; Object
 * oResult;
 * 
 * public ObjectProcessorTask(Object oRequest) { this.oRequest = oRequest; }
 * 
 * public final void doTask(Task task) {
 * 
 * } }
 */
