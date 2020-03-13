
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.time.Duration;
import java.util.*;

public class JavaCode {


	public void base() {
		Flux
				.just(1, 2, 3)
				.map(element -> element*2)
				.filter(element -> true)
				.doOnNext(System.out::println)
				.doOnComplete(System.out::println)
				.doOnError(System.out::println)
				.subscribe();

	}































	//обработка ошибок
	public Mono<String> handleException() {
		return Mono.just("some string")
				.map(str -> {
					//какой-то код
					return str;
				})
				.onErrorReturn("Hello Stranger");
	}

	public Mono<String> handleException2() {
		return Mono.just("some string")
				.map(str -> {
					//какой-то код
					return str;
				})
				.onErrorResume(ex -> ex instanceof Exception, ex -> Mono.just("str"))
				.onErrorResume(ex -> Mono.error(new Exception("иксепшин")) )
				.onErrorResume(ex -> Mono.just("Error " + ex.getMessage()))
				.doOnError(Throwable::printStackTrace);

	}

















	//npe exception
	public void demoThreadLocal() {

		ThreadLocal<Map<Object, Object>> threadLocal = new ThreadLocal<>();
		threadLocal.set(new HashMap<>());

		 Flux
				 .range(0, 10)
                 .doOnNext(k ->
						 threadLocal
								 .get()
								 .put(k, new Random(k).nextGaussian())
				 )
				 .publishOn(Schedulers.parallel())
				 .map(k -> threadLocal.get().get(k))
				 .blockLast();
	}

	public void demoContext() {

		Flux
				.range(0, 10)
				.flatMap(k ->
						Mono.subscriberContext()
								.doOnNext(context -> {
									Map<Object, Object> map = context.get("randoms");
									map.put(k, new Random(k).nextGaussian());
								})
								.thenReturn(k)
				)
				.publishOn(Schedulers.parallel())
				.flatMap(k ->
						Mono.subscriberContext()
								.map(context -> {
									Map<Object, Object> map = context.get("randoms");
									return map.get(k);
								})
				)
				.subscriberContext(context ->
						context.put("randoms", new HashMap())
				)
				.blockLast();
	}









	//отмена подписки
	public void webfluxCancelling() throws InterruptedException {

		List<String> lists = Arrays.asList("abc", "def", "ghi");
		Disposable disposable = Flux.fromIterable(lists)
				.delayElements(Duration.ofSeconds(3))
				.map(String::toLowerCase)
				.subscribe(System.out::println);

		Thread.sleep(5000); //Sleeping so that some elements in the flux gets printed
		disposable.dispose();

		Thread.sleep(10000); // Sleeping so that we can prove even waiting for some time nothing gets printed after cancelling the flux
	}











//
//
//	val url = buildUrl()
//
//	// long synchronous function
//	download(url)
//
//	toast("File is downloaded")
//
//
////-----------------------------------------
//
//
//	val url = buildUrl()
//
//	// long asynchronous function
//	download(url) {
//		toast("File is downloaded")
//	}
//
//
////-----------------------------------------
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	launch {
//		val url = buildUrl()
//
//		download(url) //suspend function
//
//		toast("File is downloaded")
//	}
//
//
////-----------------------------------------
//
//	class GeneratedContinuationClass extends SuspendLambda {
//		String url;
//
//		@Override
//		void invokeSuspend() {
//			url = buildUrl();
//
//			download(url); // suspend function
//
//			toast("File is downloaded: " + url);
//		}
//	}
//
//
//
////-----------------------------------------
//
//	int label;
//
//	void invokeSuspend() {
//		switch (label) {
//			case 0: {
//				url = buildUrl();
//
//				download(url); // suspend function
//				return;
//			}
//			case 1: {
//				toast("File is downloaded: " + url);
//				return;
//			}
//		}
//	}
//
//
//
////-----------------------------------------
//
//	int label;
//
//	void invokeSuspend() {
//		switch (label) {
//			case 0: {
//				url = buildUrl();
//				label = 1;
//
//				download(url); // suspend function
//				return;
//			}
//			case 1: {
//				toast("File is downloaded: " + url);
//				return;
//			}
//		}
//	}
//
//
//	//-----------------------------------------
//
//
//
//	int label;
//
//	void invokeSuspend() {
//		switch (label) {
//			case 0: {
//				url = buildUrl();
//				label = 1;
//
//				download(url, this); // suspend function
//				return;
//			}
//			case 1: {
//				toast("File is downloaded: " + url);
//				return;
//			}
//		}
//	}
//
//
//	//-----------------------------------------
//
//
//
//	File file;
//
//	void invokeSuspend(Object result) {
//		switch (label) {
//			case 0: {
//				url = buildUrl();
//				label = 1;
//
//				download(url, this); // suspend function
//				return;
//			}
//			case 1: {
//				file = (File) result;
//				toast("File is downloaded: " + url);
//				label = 2;
//
//				unzip(file, this); // suspend function
//				return;
//			}
//			case 2: {
//				toast("File is unzipped");
//				return;
//			}
//		}
//	}


}