import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//
// This is a unsafe implementation, and will cause a lot of issues.
// The First thing is the canceled flag
// Due to the unstability of currentEvenValue, the value is not predictable.
//
public class EvenGenerator extends IntGenerator {
	private int currentEvenValue = 0;
	
	private Lock lock = new ReentrantLock();
	
	@Override
	public int next() {
		lock.lock();
		try{
			++currentEvenValue;// Danger point here !!!
		//Thread.yield();
			++currentEvenValue;// Danger point here !!!
		} finally{
			lock.unlock();
		}
		return currentEvenValue;
	}

	public static void main(String[] args) {
		EvenChecker.test(new EvenGenerator());
	}

}
