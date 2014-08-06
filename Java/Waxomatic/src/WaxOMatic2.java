import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Car2{
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private boolean waxon = false;
	
	public void waxed(){
		lock.lock();
		try{
			waxon = true;
			condition.signalAll();
		}finally{
			lock.unlock();
		}
	}
	
	public void buffed(){
		lock.lock();
		try{
			waxon = false;
			condition.signalAll();
		} finally{
			lock.unlock();
		}
	}
	
	public void waitForWaxing() throws InterruptedException{
		lock.lock();
		try{
			while(!waxon)
			condition.await();
		} finally{
			lock.unlock();
		}
	}
	
	public void waitForBuffing() throws InterruptedException{
		lock.lock();
		try{
			while(waxon)
				condition.await();
		} finally{
			lock.unlock();
		}
	}
}

class WaxOn2 implements Runnable{
	private Car car;
	public WaxOn2(Car c){ car = c;}
	
	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				System.out.println("Wax On! ");
				TimeUnit.MILLISECONDS.sleep(200);
				car.waxed();
				car.waitForBuffing();
			}
		}catch(InterruptedException e){
			System.out.println("Exiting via interrupt!");
		}
		System.out.println("Ending Wax On Task");
	}
}

class WaxOff2 implements Runnable{
	private Car car;
	
	public WaxOff2(Car car) {
		super();
		this.car = car;
	}

	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				car.waitforWaxing();
				System.out.println("Wax Off! ");
				TimeUnit.MILLISECONDS.sleep(200);
				car.buffed();
			}
		}catch(InterruptedException e){
			System.out.println("Exiting via interrupt!");
		}
		System.out.println("Ending Wax Off Task");
	}
}


public class WaxOMatic2 {

	public static void main(String[] args) throws InterruptedException {
		Car car = new Car();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new WaxOff2(car));
		exec.execute(new WaxOn2(car));
		TimeUnit.SECONDS.sleep(5);
		exec.shutdownNow();
	}

}
