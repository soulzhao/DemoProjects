import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


class NIOBlocked implements Runnable{
	private final SocketChannel sc;
	
	public NIOBlocked(SocketChannel sc) {
		super();
		this.sc = sc;
	}

	@Override
	public void run() {
		try{
			System.out.println("Waiting for read() in " + this);
			sc.read(ByteBuffer.allocate(1));
		}catch(ClosedByInterruptException e){
			System.out.println("ClosedByInterruptException");
		}catch(AsynchronousCloseException e){
			System.out.println("AsynchronousCloseException");
		}catch(IOException e){
			throw new RuntimeException();
		}
		System.out.println("Exiting NIOBlocked.run()" + this);
	}
	
}

public class NIOInterruption {
	public static void main(String[] args) throws Exception {
		ExecutorService exec = Executors.newCachedThreadPool();
		ServerSocket server = new ServerSocket(8080);
		InetSocketAddress isa = new InetSocketAddress("localhost", 8080); // NIO
		SocketChannel sc1 = SocketChannel.open(isa); // NIO
		SocketChannel sc2 = SocketChannel.open(isa); // NIO
		Future<?> f = exec.submit(new NIOBlocked(sc1));
		exec.execute(new NIOBlocked(sc2));
		/*
		 * The differences between shutdown and shutdownnow 
		 * shutdownnow put status to STOP
		 * shutdownnow put status to SHUTDOWN
		 * http://justsee.iteye.com/blog/999189
		 * http://zzhonghe.iteye.com/blog/826947
		 * 
		 * 
		 *  RUNNING    = -1 << COUNT_BITS;
    		SHUTDOWN   =  0 << COUNT_BITS;
    		STOP       =  1 << COUNT_BITS;
    		TIDYING    =  2 << COUNT_BITS;
    		TERMINATED =  3 << COUNT_BITS;
		 * 
		 */
		exec.shutdownNow();
//		exec.shutdown();
		// Below is not necessary!!
//		TimeUnit.SECONDS.sleep(1);
//		f.cancel(true);
//		TimeUnit.SECONDS.sleep(1);
//		sc2.close();
	}

}
