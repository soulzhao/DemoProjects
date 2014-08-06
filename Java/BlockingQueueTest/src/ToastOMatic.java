import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class Toast{
	public enum Status {DRY, BUTTERED, JAMMED}
	private Status status = Status.DRY;
	private final int id;
	public Toast(int idn) {id = idn;}
	public void butter() {status = Status.BUTTERED;}
	public void jam() {status = Status.JAMMED;}
	public Status getStatus() {return status;}
	public int getId() {return id;};
	public String toString(){
		return "Toast " + id + ": " + status;
	}
}

class ToastQueue extends LinkedBlockingQueue<Toast>{}

class Toaster implements Runnable{
	private ToastQueue toastQueue;
	private int count = 0;
	private Random rand = new Random(47);
	public Toaster(ToastQueue tq) { toastQueue = tq;}
	
	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				TimeUnit.MILLISECONDS.sleep(100 + rand.nextInt(500));
				Toast t = new Toast(count++);
				System.out.println(t);
				toastQueue.put(t);
			}
		}catch(InterruptedException e){
			System.out.println("Toaster Interrupted!");
		}
		System.out.println("Toaster Off!");
	}
}

class Butterer implements Runnable{
	private ToastQueue dryQueue, butteredQueue;
	
	public Butterer(ToastQueue dryQueue, ToastQueue butteredQueue) {
		super();
		this.dryQueue = dryQueue;
		this.butteredQueue = butteredQueue;
	}

	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				Toast t = dryQueue.take();
				t.butter();
				System.out.println(t);
				butteredQueue.put(t);
			}
		}catch(InterruptedException e){
			System.out.println("Bufferer Interrupted!");
		}
		System.out.println("Bufferer Off!");
	}
}

class Jammer implements Runnable{
	private ToastQueue butterQueue, finishedQueue;
	
	public Jammer(ToastQueue butterQueue, ToastQueue finishedQueue) {
		super();
		this.butterQueue = butterQueue;
		this.finishedQueue = finishedQueue;
	}

	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				Toast t = butterQueue.take();
				t.jam();
				System.out.println(t);
				finishedQueue.put(t);
			}
		}catch(InterruptedException e){
			System.out.println("Jammer Interrupted!");
		}
		System.out.println("Jammer Off!");		
	}
}

class Eater implements Runnable{
	private ToastQueue finishedQueue;
	private int counter = 0;
	
	public Eater(ToastQueue finishedQueue) {
		super();
		this.finishedQueue = finishedQueue;
	}

	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				Toast t = finishedQueue.take();
				if(t.getId() != counter++ ||
				   t.getStatus() != Toast.Status.JAMMED){
				  System.out.println(">>> Error: " + t);
				  System.exit(1);
				}else{
					System.out.println("Chomp! " + t);
				}
			}
		}catch(InterruptedException e){
			System.out.println("Eater interrupted!");
		}
		System.out.println("Eater off");
	}
}

public class ToastOMatic {

	public static void main(String[] args) throws InterruptedException {
		ToastQueue dryQueue = new ToastQueue(),
				butterQueue = new ToastQueue(),
				finishedQueue = new ToastQueue();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new Toaster(dryQueue));
		exec.execute(new Butterer(dryQueue, butterQueue));
		exec.execute(new Jammer(butterQueue, finishedQueue));
		exec.execute(new Eater(finishedQueue));
		TimeUnit.SECONDS.sleep(5);
		exec.shutdownNow();
	}

}
