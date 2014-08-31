import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The entity used to be put into the delayQueue.
 * Delayed interface inherited from Comparable interface 
 * @author Soul
 */
class DelayedTask implements Runnable, Delayed{
	private static int counter = 0;
	private final int id = counter++;
	private final int delta;
	private final long trigger;
	
	protected static List<DelayedTask> sequence = 
			new ArrayList<DelayedTask>();
	
	public DelayedTask(int delayInMilliseconds) {
		super();
		this.delta = delayInMilliseconds;
		this.trigger = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delta, TimeUnit.MILLISECONDS);
		sequence.add(this);
	}
	
	@Override
	public int compareTo(Delayed arg) {
		DelayedTask that = (DelayedTask) arg;
		if(trigger < that.trigger) return -1;
		if(trigger > that.trigger) return 1;
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(trigger - System.nanoTime(), TimeUnit.NANOSECONDS);
	}

	@Override
	public void run() {
		System.out.println(this + " ");
	}
	
	public String toString(){
		return String.format("[%1$-4d]", delta) + " Task " + id;
	}
	
	public String summary(){
		return "(" + id + ":" + delta + ")";
	}
	
	/**
	 * a public static inner class used to end the execution service
	 * @author Soul
	 */
	public static class EndSentinel extends DelayedTask{
		private ExecutorService exec;
		
		public EndSentinel(int delayInMilliseconds, ExecutorService e) {
			super(delayInMilliseconds);
			exec = e;
		}
		
		public void run(){
			for(DelayedTask pt : sequence){
				System.out.println(pt.summary() + " ");
			}
			System.out.println();
			System.out.println(this + " Calling shutdownNow()");
			exec.shutdownNow();
		}
	}
}

/**
 * The Consumer taken entities from the queue
 * @author Soul
 */
class DelayedTaskConsumer implements Runnable{
	private DelayQueue<DelayedTask> q;
	
	public DelayedTaskConsumer(DelayQueue<DelayedTask> q) {
		super();
		this.q = q;
	}

	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				q.take().run(); // always take the task with the least delay value
			}
		}catch(InterruptedException e){
			//
		}
		System.out.println("Finished DelayedTaskConsumer!");
	}
}

public class DelayQueueDemo {
	public static void main(String[] args) {
		Random rand = new Random(47);
		ExecutorService exec = Executors.newCachedThreadPool();
		// Only objects implement delayed interface can be put into DelayQueue, it is a kind of priority queue
		DelayQueue<DelayedTask> queue = 
				new DelayQueue<DelayedTask>(); 
		for(int i = 0; i < 20; i++){
			queue.put(new DelayedTask(rand.nextInt(5000)));
		}
		queue.add(new DelayedTask.EndSentinel(5000, exec));
		exec.execute(new DelayedTaskConsumer(queue));
	}

}
