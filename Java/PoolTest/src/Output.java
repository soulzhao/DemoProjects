
public class Output implements Runnable{
	Pool pool;
	
	public Output(Pool pool) {
		super();
		this.pool = pool;
	}

	@Override
	public void run() {
		try {
			while(true){
				pool.output(3);
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
