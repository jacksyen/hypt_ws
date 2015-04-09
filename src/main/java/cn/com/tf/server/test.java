package cn.com.tf.server;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class test {
	
	private ConcurrentHashMap<String, FutureTask<Integer>> concurrentHashMap = new ConcurrentHashMap<String, FutureTask<Integer>>();
	
	private test(){
		FutureTask<Integer> f = new FutureTask<Integer>(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				Thread.sleep(2000);
				System.out.println(123);
				return 1;
			}
			
		});
		concurrentHashMap.put("1", f);
		f.run();
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException{
		final test t = new test();
		Runnable r = new Runnable() {
			public void run() {
				try {
					System.out.println(t.concurrentHashMap.get("1").isDone());
					System.out.println(t.concurrentHashMap.get("1").get());
					System.out.println("thread");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread th =new Thread(r);
		th.start();
		System.out.println(t.concurrentHashMap.get("1").get());
		System.out.println("main");
	}

}
