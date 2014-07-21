import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TestDriver {

	public static void main(String[] args) {
		Pool pool = new Pool(10);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(new Input(pool));
		executor.submit(new Output(pool));
	}

}
