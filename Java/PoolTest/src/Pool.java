
public class Pool {
	//
	// when full, the level is 20,
	// we have a warning at 18 and 2.
	// when 18, we stop input,
	// when 2, we stop output.
	//
	private int level;
	
	
	
	public Pool(int level) {
		super();
		this.level = level;
	}

	public void input(int water) throws InterruptedException{
		synchronized (this) {
			if(level > 18 || level + water > 20){
				System.out.println("The input thread will wait!");
				wait();
				System.out.println("The input thread awake!");
			}
			level += water;
			System.out.println(String.format("Pool level +%d, Pool level is now %d", water, level));			
			notifyAll();
		}
	}
	
	public void output(int water) throws InterruptedException{
		synchronized (this) {
			if(level < 2 || level - water < 0){
				System.out.println("The output thread will wait!");
				wait();
				System.out.println("The output thread awake!");				
			}
			level -= water;
			System.out.println(String.format("Pool level -%d, Pool level is now %d", water, level));			
			notifyAll();
		}
	}
}
