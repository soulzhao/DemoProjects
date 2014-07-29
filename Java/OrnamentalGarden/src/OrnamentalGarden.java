import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Count{
	private int count = 0;
	private Random rand = new Random(47);
	public synchronized int increment(){
		int temp = count;
		if(rand.nextBoolean()) Thread.yield(); // 产生一半时间在让步的效果，增加失败的可能性
		return (count = ++temp);
	}
	public synchronized int value() {return count;};
}

class Entrance implements Runnable{
	private static Count count = new Count();
	private static List<Entrance> entrances = 
			new ArrayList<Entrance>();
	private int number = 0; //每一个入口的本地技术副本
	
	private final int id;
	private static volatile boolean canceled = false;
	public static void cancel() {canceled = true;}
	public Entrance(int id) {
		super();
		this.id = id;
		entrances.add(this);
	}
	
	@Override
	public void run() {
		while(!canceled){ // 此处cancel
			synchronized(this){
				++number;
			}
			System.out.println(this + "Total: " + count.increment());
			try{
				TimeUnit.MILLISECONDS.sleep(100);
			}catch(InterruptedException e){
				System.out.println("Sleep Interrupted!!");
			}
		}
		System.out.println("Stopping " + this);
	}
	
	public synchronized int getValue() {return number;}
	
	public String toString(){
		return "Entrance  " + id + ": " + getValue();
	}
	
	public static int getTotalCount(){
		return count.value();
	}
	
	public static int sumEntrances(){
		int sum = 0;
		for(Entrance entracne : entrances)
			sum += entracne.getValue();
		return sum;
	}
}

public class OrnamentalGarden {

	public static void main(String[] args) throws InterruptedException {
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0; i < 5; i++){
			exec.execute(new Entrance(i));
		}
		TimeUnit.SECONDS.sleep(3);
		Entrance.cancel();
		exec.shutdown();
		/**
			boolean awaitTermination(long timeout,
			                         TimeUnit unit)
			                         throws InterruptedException
			Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, 
			or the current thread is interrupted, whichever happens first.
			Parameters:
				timeout - the maximum time to wait
				unit - the time unit of the timeout argument
			Returns:
				true if this executor terminated and false if the timeout elapsed before termination
			Throws:
				InterruptedException - if interrupted while waiting
		 */
		if(!exec.awaitTermination(250, TimeUnit.MILLISECONDS))
			System.out.println("Some tasks were not terminated!");
		// Here we do a validation, and see if the below 2 values are the same!!
		System.out.println("Total: " + Entrance.getTotalCount());
		System.out.println("Sum of Entrances:" + Entrance.sumEntrances());
		
	}

}
