
public class WaitPerson implements Runnable{
	Restaurant restaurant;
	
	public WaitPerson(Restaurant restaurant) {
		super();
		this.restaurant = restaurant;
	}

	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				synchronized(this){
					while(restaurant.meal == null){
						wait();
					}
				}
				System.out.println("Wait Person got meal !" + restaurant.meal);
				synchronized(restaurant.chief){ 
					// here should be notice!!! whoever will call the wait or notice, who should be synchronized!!
					restaurant.meal = null;
					restaurant.chief.notifyAll();
				}
			}
		}catch(InterruptedException e){
			System.out.println("Wait Person interrupted!");
		}
	}
	
}
