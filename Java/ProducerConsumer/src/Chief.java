import java.util.concurrent.TimeUnit;


public class Chief implements Runnable{
	Restaurant restaurant;
	int count = 0;
	public Chief(Restaurant restaurant) {
		super();
		this.restaurant = restaurant;
	}

	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				synchronized (this) {
					while(restaurant.meal != null){
						wait();
					}
				}
				if(++count == 10){
					System.out.println("Out of food! closing!");
					restaurant.exec.shutdownNow();
				}
				System.out.println("Order up!");
				synchronized (restaurant.waitperson) {
					restaurant.meal = new Meal(count);
					restaurant.waitperson.notifyAll();
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}
		}catch(InterruptedException e){
			System.out.println("Cheif interrupted!");
		}
	}
}
