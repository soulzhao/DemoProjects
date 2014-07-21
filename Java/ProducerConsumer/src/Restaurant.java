import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Restaurant {
	public Meal meal;
	public Chief chief = new Chief(this);
	public ExecutorService exec = Executors.newCachedThreadPool();
	public WaitPerson waitperson = new WaitPerson(this);

	public Restaurant() {
		super();

		exec.execute(chief);
		exec.execute(waitperson);
	}
	
	public static void main(String [] args){
		new Restaurant();
	}
}
