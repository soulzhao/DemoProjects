import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class CloseResource {

	public static void main(String[] args) throws Exception {
		ExecutorService exec = Executors.newCachedThreadPool();
		ServerSocket server = new ServerSocket(8080);
		InputStream socketInput = 
				new Socket("localhost", 8080).getInputStream();
		exec.execute(new IOBlocked(socketInput));
		exec.execute(new IOBlocked(System.in));
		
		TimeUnit.MILLISECONDS.sleep(100);
		System.out.println("Shutting down all threads");
		exec.shutdownNow();
		TimeUnit.SECONDS.sleep(1); // 只有当这些底层的IO资源释放掉之后，调用它们的线程才会被释放掉！！！！
		System.out.println("Closing " + socketInput.getClass().getName());
		socketInput.close();
		TimeUnit.SECONDS.sleep(1); // 只有当这些底层的IO资源释放掉之后，调用它们的线程才会被释放掉！！！！
		System.out.println("Closing " + System.in.getClass().getName());
		System.in.close();
	}

}
