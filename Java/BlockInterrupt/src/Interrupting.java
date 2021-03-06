import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.management.RuntimeErrorException;


class SleepBlocked implements Runnable{
	@Override
	public void run() {
		try{
			TimeUnit.SECONDS.sleep(100);
		}catch(InterruptedException e){
			System.out.println("Exiting SleepBlocked run()");
		}
	}
}

class IOBlocked implements Runnable{
	private InputStream in;
	public IOBlocked(InputStream is){
		this.in = is;
	}
	
	@Override
	public void run() {
		try{
			System.out.println("Waiting for read():");
			in.read();
		}catch(IOException e){
			if(Thread.currentThread().isInterrupted())
				System.out.println("Interrupted from blocked I/O");
			else
				throw new RuntimeException(e);
		}
		System.out.println("Exiting IOBlocked run()");
	}
}

class SynchronizedBlocked implements Runnable{
	public synchronized void f(){
		while(true){
			Thread.yield();
		}
	}
	
	public SynchronizedBlocked(){
		new Thread() {
			public void run(){
				f();
			}
		}.start();
	}
	
	@Override
	public void run() {
		System.out.println("Trying to call f()");
		f();
		System.out.println("Exiting synchronizedBlocked run()");
	}
}

public class Interrupting{
	private static ExecutorService exec = Executors.newCachedThreadPool();
	
	static void test(Runnable r) throws InterruptedException {
		Future<?> f = exec.submit(r);
		TimeUnit.MILLISECONDS.sleep(100);
		System.out.println("Interrupting " + r.getClass().getName());
		f.cancel(true); // use Future.cancel to interrupt the thread
		System.out.println("Interrupt sent to " + r.getClass().getName());
	}
	
	public static void main(String [] args) throws InterruptedException{
		test(new SleepBlocked());
		
		//
		// Use below to indicate that we can interrupt on the I/O and synchronized area
		//
		test(new IOBlocked(System.in)); // Not exit as what we want
		test(new SynchronizedBlocked()); // Not exit as what we want
		
		TimeUnit.SECONDS.sleep(4);
		System.out.println("Aborting with System.exit(0)");
		System.exit(0); // System exit 
	}
}

