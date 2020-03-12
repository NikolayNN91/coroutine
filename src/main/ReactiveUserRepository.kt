package main

import DatabaseClient
import reactor.core.publisher.Mono


class ReactiveUserRepository {


    private var client: DatabaseClient? = null

    fun ReactiveUserRepository(client: DatabaseClient?) {
        this.client = client
    }

    fun webfluxSelect(): Mono<Long>? {
        return client?.webfluxExecute("select .....")
    }

//    suspend fun coroutineSelect(): Long? =
//        client?.coroutineExecute("select .....")
}