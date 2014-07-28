import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class AttemptLocking {
	private ReentrantLock lock = new ReentrantLock();
	
	public void untimed(){
		boolean captured = lock.tryLock();
		try{
			System.out.println("tryLock() : " + captured);
		}finally{
			if(captured){
				lock.unlock();
			}
		}
	}
	
	public void timed(){
		boolean captured = false;
		try{
			// use trylock to define what to do when you could not obtain the lock
			// instead of just waiting
			captured = lock.tryLock(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		try{
			System.out.println("tryLock(2, TimeUnit.SECONDS): " + 
								captured);
		}finally{
			if(captured){
				lock.unlock();
			}
		}
	}
	
	public static void main(String [] args){
		final AttemptLocking al = new AttemptLocking();
		al.untimed();
		al.timed();
		
		new Thread(){
			{setDaemon(true);}
			public void run(){
				al.lock.lock();
				System.out.println("acquired!");
			}
		}.start();

//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Thread.yield();
		
		al.untimed();
		al.timed();
	}
}
