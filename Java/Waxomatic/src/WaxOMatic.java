import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Use Notify/NotifyAll/wait, sometimes will encounter a signal missing issue,
 * the thing is, the notify in one thread is call before wait call in another one.
 * The wait missed the notify signal so it will always wait.
 * 
 * the solution is to expand the synchronized area, include the condition in the critical area.
 * pls refer Think in Java Page 706, chapter 21.
 */

class  Car{
	private boolean waxOn = false;
	public synchronized void waxed(){
		waxOn = true; // ready to buff
		notifyAll();
	}
	
	public synchronized void buffed(){
		waxOn = false;
		notifyAll();
	}
	
	public synchronized void waitforWaxing() throws InterruptedException{
		while(waxOn == false){
			wait();
		}
	}
	
	public synchronized void waitForBuffing() throws InterruptedException{
		while(waxOn == true){
			wait();
		}
	}
}

class WaxOn implements Runnable{
	private Car car;
	public WaxOn(Car c){car = c;}
	
	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				System.out.println("Wax On!");
				TimeUnit.MILLISECONDS.sleep(200);
				car.waxed();
				car.waitForBuffing();
			}
		}catch(InterruptedException e){
			System.out.println("Exiting via interrupt!");
		}
		System.out.println("Ending Wax On task");
	}
}

class WaxOff implements Runnable{
	private Car car;
	public WaxOff(Car c){car = c;}
	
	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				car.waitforWaxing();
				System.out.println("Wax Off!");
				TimeUnit.MILLISECONDS.sleep(200);
				car.buffed();
			}
		}catch(InterruptedException e){
			System.out.println("Exiting via interrupt!");
		}
		System.out.println("Ending Wax Off task");
	}
}

public class WaxOMatic {
	public static void main(String [] args) throws InterruptedException{
		Car car = new Car();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new WaxOff(car));
		exec.execute(new WaxOn(car));
		TimeUnit.SECONDS.sleep(5);
		exec.shutdownNow();
	}
}
