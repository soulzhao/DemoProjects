import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;

class LiftOff implements Runnable{
	protected int countDown = 10;
	private static int taskCount = 0;
	private final int id = taskCount++;
	
	public LiftOff() {}
	public LiftOff(int countDown) {
		this.countDown = countDown;
	}
	
	private String status(){
		return "#" + id + "(" +
				(countDown > 0 ? countDown : "LiftOff !") + "), ";
	}
	
	@Override
	public void run() {
		while(countDown-- > 0){
			System.out.println(status());
			Thread.yield();
		}
	}
}

class LiftOffRunner implements Runnable{
	private BlockingQueue<LiftOff> rockets;
	
	public LiftOffRunner(BlockingQueue<LiftOff> rockets) {
		super();
		this.rockets = rockets;
	}

	public void add(LiftOff lo){
		try{
			rockets.put(lo);
		}catch(InterruptedException e){
			System.out.println("Interruped during put()");
		}
	}
	
	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				LiftOff roceket = rockets.take();
				roceket.run();
			}
		}catch(InterruptedException e){
			System.out.println("Waking from take()");
		}
		System.out.println("Exiting LiftOffRunner!");
	}
}

public class TestBlockingQueues {
	static void getkey(){
		try{
			new BufferedReader(
					new InputStreamReader(System.in)).readLine();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	static void getkey(String msg){
		System.out.println(msg);
		getkey();
	}
	
	static void test(String msg, BlockingQueue<LiftOff> queue){
		System.out.println(msg);
		LiftOffRunner runner = new LiftOffRunner(queue);
		Thread t = new Thread(runner);
		t.start();
		for(int i = 0; i < 5; i++){
			runner.add(new LiftOff(5));
		}
		
		getkey("Press 'Enter' (" + msg + ")");
		t.interrupt();
		System.out.println("Finished " + msg + "test");
	}
	
	public static void main(String[] args) {
		test("LinkedBlockingQueue", new LinkedBlockingDeque<LiftOff>());
		/**
		 * LinkedBlockingDeque
		An optionally-bounded blocking queue based on linked nodes. This queue orders elements FIFO (first-in-first-out). The head of the queue is that element that has been on the queue the longest time. The tail of the queue is that element that has been on the queue the shortest time. New elements are inserted at the tail of the queue, and the queue retrieval operations obtain elements at the head of the queue. Linked queues typically have higher throughput than array-based queues but less predictable performance in most concurrent applications.
		The optional capacity bound constructor argument serves as a way to prevent excessive queue expansion. The capacity, if unspecified, is equal to Integer.MAX_VALUE. Linked nodes are dynamically created upon each insertion unless this would bring the queue above capacity.

		This class and its iterator implement all of the optional methods of the Collection and Iterator interfaces.
		 */
		test("ArrayBlockingQueue", new ArrayBlockingQueue<LiftOff>(3));
		/**
		 * ArrayBlockingQueue:
		 
		 A bounded blocking queue backed by an array. This queue orders elements FIFO (first-in-first-out). The head of the queue is that element that has been on the queue the longest time. The tail of the queue is that element that has been on the queue the shortest time. New elements are inserted at the tail of the queue, and the queue retrieval operations obtain elements at the head of the queue.
		 This is a classic "bounded buffer", in which a fixed-sized array holds elements inserted by producers and extracted by consumers. Once created, the capacity cannot be changed. Attempts to put an element into a full queue will result in the operation blocking; attempts to take an element from an empty queue will similarly block.

		 This class supports an optional fairness policy for ordering waiting producer and consumer threads. By default, this ordering is not guaranteed. However, a queue constructed with fairness set to true grants threads access in FIFO order. Fairness generally decreases throughput but reduces variability and avoids starvation.A bounded blocking queue backed by an array. This queue orders elements FIFO (first-in-first-out). The head of the queue is that element that has been on the queue the longest time. The tail of the queue is that element that has been on the queue the shortest time. New elements are inserted at the tail of the queue, and the queue retrieval operations obtain elements at the head of the queue.
		 This is a classic "bounded buffer", in which a fixed-sized array holds elements inserted by producers and extracted by consumers. Once created, the capacity cannot be changed. Attempts to put an element into a full queue will result in the operation blocking; attempts to take an element from an empty queue will similarly block.

		 This class supports an optional fairness policy for ordering waiting producer and consumer threads. By default, this ordering is not guaranteed. However, a queue constructed with fairness set to true grants threads access in FIFO order. Fairness generally decreases throughput but reduces variability and avoids starvation.
		 */
		test("SynchronousQueue", new SynchronousQueue<LiftOff>());
		/**
		 * SynchronousQueue
		 A blocking queue in which each insert operation must wait for a corresponding remove operation by another thread, and vice versa. A synchronous queue does not have any internal capacity, not even a capacity of one. You cannot peek at a synchronous queue because an element is only present when you try to remove it; you cannot insert an element (using any method) unless another thread is trying to remove it; you cannot iterate as there is nothing to iterate. The head of the queue is the element that the first queued inserting thread is trying to add to the queue; if there is no such queued thread then no element is available for removal and poll() will return null. For purposes of other Collection methods (for example contains), a SynchronousQueue acts as an empty collection. This queue does not permit null elements.
		 Synchronous queues are similar to rendezvous channels used in CSP and Ada. They are well suited for handoff designs, in which an object running in one thread must sync up with an object running in another thread in order to hand it some information, event, or task.

		 This class supports an optional fairness policy for ordering waiting producer and consumer threads. By default, this ordering is not guaranteed. However, a queue constructed with fairness set to true grants threads access in FIFO order.
		 */
		
	}

}
