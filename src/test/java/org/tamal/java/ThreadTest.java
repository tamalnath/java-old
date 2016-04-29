package org.tamal.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

/**
 * @author Tamal Kanti Nath
 */
@SuppressWarnings({ "static-method", "synthetic-access" })
public class ThreadTest {

	private static final int ITERATIONS = 10_000;
	private Object syncLock = new Object();
	private boolean interrupted;
	private int x;

	/**
	 * This test proves that non-synchronized methods may return unexpected results due to thread cache.
	 * @throws InterruptedException if unexpected results are not thrown after 10 iterations
	 */
	@Test
	public void testCounter() throws InterruptedException {
		while (true) {
			Thread t1 = new Thread(() -> increment());
			Thread t2 = new Thread(() -> decrement());
			t1.start();
			t2.start();
			t1.join();
			t2.join();
			if (x != 0) {
				return;
			}
		}
	}

	private void increment() {
		for (int i = 0; i < ITERATIONS; i++) {
			x++;
		}
	}

	private void decrement() {
		for (int i = 0; i < ITERATIONS; i++) {
			x--;
		}
	}

	/**
	 * This test proves that synchronized method of block resolves the concurrency issues.
	 * @throws InterruptedException will never be thrown
	 */
	@Test
	public void testSynchronizedCounter() throws InterruptedException {
		Thread t1 = new Thread(() -> incrementSynchronized());
		Thread t2 = new Thread(() -> decrementSynchronized());
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		assertEquals(0, x);
	}

	private void incrementSynchronized() {
		for (int i = 0; i < ITERATIONS; i++) {
			synchronized(syncLock) {
				x++;
			}
		}
	}

	private void decrementSynchronized() {
		for (int i = 0; i < ITERATIONS; i++) {
			synchronized(syncLock) {
				x--;
			}
		}
	}

	/**
	 * This test demonstrated the use of {@link #wait()} and {@link #notify()}.
	 * @throws InterruptedException should never happen
	 */
	@Test
	public void testWaitNotify() throws InterruptedException {
		Thread t1 = new Thread(() -> incrementWait());
		Thread t2 = new Thread(() -> decrementWait());
		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private synchronized void incrementWait() {
		for (int i = 0; i < ITERATIONS; i++) {
			assertEquals(0, x);
			x++;
			notify();
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		notify();
	}

	private synchronized void decrementWait() {
		for (int i = 0; i < ITERATIONS; i++) {
			assertEquals(1, x);
			x--;
			notify();
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		notify();
	}

	/**
	 * This test demonstrates the use of {@link Thread#interrupt()}, {@link Thread#isInterrupted()} and {@link InterruptedException}.
	 */
	@Test
	public void testInterrupt() {
		Thread t = new Thread(() -> min2max());
		t.start();
		try {
			t.join(10);
			t.interrupt();
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(interrupted);
		t = new Thread(() -> sleep(1000));
		t.start();
		try {
			t.join(10);
			t.interrupt();
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(interrupted);
	}

	private void min2max() {
		for (int i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i++) {
			if (Thread.interrupted()) {
				interrupted = true;
				break;
			}
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			assertEquals("sleep interrupted", e.getMessage());
			interrupted = true;
		}
	}

	/**
	 * This test demonstrates deadlock due to synchronized methods.
	 */
	@Test
	public void testDeadLock() {
		Person p1 = new Person();
		Person p2 = new Person();
		Thread t1 = new Thread(() -> p1.addFriendSync(p2));
		Thread t2 = new Thread(() -> p2.addFriendSync(p1));
		t1.start();
		t2.start();
		try {
			t1.join(50);
			assertTrue(t1.isAlive());
			t2.join(50);
			assertTrue(t2.isAlive());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long[] tids = bean.findDeadlockedThreads();
		assertNotNull(tids);
		ThreadInfo[] threadInfos = bean.getThreadInfo(tids);
		assertEquals(2, threadInfos.length);
		assertEquals(threadInfos[0].getThreadId(), threadInfos[1].getLockOwnerId());
		assertEquals(threadInfos[1].getThreadId(), threadInfos[0].getLockOwnerId());
	}

	/**
	 * This test demonstrates deadlock due to synchronized methods.
	 */
	@Test
	public void testReentrantLock() {
		Person p1 = new Person();
		Person p2 = new Person();
		while (true) {
			Thread t1 = new Thread(() -> p1.addFriendReentrant(p2));
			Thread t2 = new Thread(() -> p2.addFriendReentrant(p1));
			t1.start();
			t2.start();
			try {
				t1.join();
				t2.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			assertFalse(p1.friends.contains(p2) ^ p2.friends.contains(p1));
			if (p1.friends.contains(p2) && p2.friends.contains(p1)) {
				break;
			}
			ThreadMXBean bean = ManagementFactory.getThreadMXBean();
			long[] tids = bean.findDeadlockedThreads();
			assertNull(tids);
		}
	}

	private static class Person {

		private ReentrantLock lock;
		private Set<Person> friends;

		public Person() {
			friends = new HashSet<>();
			lock = new ReentrantLock();
		}

		public void addFriendSync(Person friend) {
			assertFalse(Thread.holdsLock(this));
			synchronized(this) {
				assertTrue(Thread.holdsLock(this));
				try {
					Thread.sleep(ThreadLocalRandom.current().nextLong(10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				assertFalse(Thread.holdsLock(friend));
				synchronized(friend) {
					assertTrue(Thread.holdsLock(friend));

					this.friends.add(friend);
					friend.friends.add(this);
				}
				assertFalse(Thread.holdsLock(friend));
			}
			assertFalse(Thread.holdsLock(this));
		}

		public void addFriendReentrant(Person friend) {
			boolean meLocked = this.lock.tryLock();
			try {
				Thread.sleep(ThreadLocalRandom.current().nextLong(10));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean friendLocked = friend.lock.tryLock();
			if (meLocked && friendLocked) {
				this.friends.add(friend);
				friend.friends.add(this);
			}
			if (meLocked) {
				assertTrue(this.lock.isHeldByCurrentThread());
				assertTrue(this.lock.isLocked());
				assertEquals(1, this.lock.getHoldCount());
				assertEquals(0, this.lock.getQueueLength());
				this.lock.unlock();
			}
			if (friendLocked) {
				assertTrue(friend.lock.isHeldByCurrentThread());
				assertTrue(friend.lock.isLocked());
				assertEquals(1, friend.lock.getHoldCount());
				assertEquals(0, friend.lock.getQueueLength());
				friend.lock.unlock();
			}
		}

	}

}
