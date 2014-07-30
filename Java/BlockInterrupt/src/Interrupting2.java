import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BlockMutex{
	private Lock lock = new ReentrantLock();

	public BlockMutex() {
		super();
		lock.lock();;
	}
	
	public void f(){
		try{
			//
			// Check http://www.dewen.org/q/9077 to know about this function, and this allow interruption on Lock
			//
			lock.lockInterruptibly();
			System.out.println("lock acquired in f()");
		}catch(InterruptedException e){
			System.out.println("Interrupted from lock acquisition in f()");
		}
	}
}

class Blocked2 implements Runnable{
	BlockMutex blocked = new BlockMutex();
	
	@Override
	public void run() {
		System.out.println("Waiting for f() in BlockedMutex");
		blocked.f();
		System.out.println("Broken out of blocked call");
	}
}


public class Interrupting2 {
	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new Blocked2());
		t.start();
		TimeUnit.SECONDS.sleep(1);
		System.out.println("Issuing t.interrupt()");
		t.interrupt();
	}

}
