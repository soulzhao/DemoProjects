
public class Input implements Runnable{
	Pool pool;
	
	public Input(Pool pool) {
		super();
		this.pool = pool;
	}

	@Override
	public void run() {
		try {
			while(true){
				pool.input(4);
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
