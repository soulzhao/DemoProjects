
public class ThreadsTest {
	static Thread thread1;
	static Thread thread2;
	static Thread thread3;
	
	public static void counter(String threadname){
		int count = 0;
		while(count < 5){
			System.out.println(String.format("thread %s running, %d times...", threadname, count));
			count++;
		}
	}
	
	public static void main(String [] args) throws InterruptedException{
		thread1 = new Thread(new Runnable(){
			@Override
			public void run() {
				ThreadsTest.counter("1");
			}
		});
		
		thread2 = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					thread1.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ThreadsTest.counter("2");
			}
		});
		
		thread3 = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					thread2.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ThreadsTest.counter("3");
			}
		});
		
		thread1.start();
		thread2.start();
		thread3.start();
	}
}
