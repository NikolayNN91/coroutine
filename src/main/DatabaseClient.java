import reactor.core.publisher.Mono;

public class DatabaseClient {

    public Mono<Long> webfluxExecute(String str) {
        return Mono.just(1L);
    }

    public Long coroutineExecute(String str) {
        return 1L;
    }
}
