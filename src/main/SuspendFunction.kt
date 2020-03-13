package main

import jdk.nashorn.internal.codegen.CompilerConstants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.*
import kotlin.system.measureTimeMillis

class SuspendFunction {

    val service: Service = Service()











    suspend fun demoContext() {
        println("2. Running in ${coroutineContext[CoroutineName]}")

        val job : Job = GlobalScope.launch() {
            println("3. Running in ${coroutineContext[CoroutineName]}")

        }
    }
























    fun base() = GlobalScope.launch { //(1)
        val job: Job = launch() { //(2)
            val result = getMessage() //(3)
            println("$result")
        }
        println("The result: ")
        job.join() //(4)
    }




    suspend fun getMessage() : String {
        delay(1000);
        return "Hello world!"
    }

















//смена thread pool
    suspend fun contextChanging() {

        val job: Job = GlobalScope.launch() {
            println("1. Running in ${Thread.currentThread().name}")

            val data = withContext(Dispatchers.Unconfined) {
                println("2. Running in ${Thread.currentThread().name}")
                customSuspendedFun2()
            }

            println("4. Running in ${Thread.currentThread().name}")
        }
    }

    suspend fun customSuspendedFun2() {
        delay(1000)
        println("3. Running in ${Thread.currentThread().name}")
    }






    fun printThreadName(coroutineContext: CoroutineContext) = CoroutineScope(coroutineContext).launch {
//sampleStart
        launch { // context of the parent, main runBlocking coroutine
            println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
            println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Default) { // will get dispatched to DefaultDispatcher
            println("Default               : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
            println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
        }
    }
//sampleEnd















    //асинхронное выполнение
    suspend fun showWork() {

        println("2. Running in ${Thread.currentThread().id}")

        val job: Job = GlobalScope.launch(coroutineContext) {

            val strA = getStringA()
            val strB = getStringB()

            printResult(strA, strB)
        }

    }

    suspend fun getStringA() : String {

        println("3. Running in ${Thread.currentThread().id}")

        delay(1000)
        return "String A"
    }

    suspend fun getStringB() : String {

        println("4. Running in ${Thread.currentThread().id}")

        delay(1000)
        return "String B"
    }

    suspend fun printResult(a: String, b: String) {
        delay(1000)
        println("$a and $b")
    }




















    //асинхронное выполнение c async
    suspend fun showAsyncWork() {

        val job: Job = CoroutineScope(Dispatchers.Default).launch() {

            println("2. Running + ${Thread.currentThread().id}")

            val dataA = getAsyncStringA()
            val dataB = getAsyncStringB()

            printResult(dataA.await(), dataB.await())
        }

    }

    suspend fun getAsyncStringA(): Deferred<String> {
        return CoroutineScope(Dispatchers.Default).async {

            println("3. Running + ${Thread.currentThread().id}")

            delay(1000)
            "String A"
        }
    }

    suspend fun getAsyncStringB(): Deferred<String> {
        return CoroutineScope(Dispatchers.Default).async {

            println("4. Running + ${Thread.currentThread().id}")

            delay(1000)
            "String B"
        }
    }
























    //отмена выполнения корутины приведет к отмене дочерних корутин
    fun cancelCoroutine() {
        val job: Job = GlobalScope.launch() {
            delay(1000)
            println("2. Running")
            customSuspendedFun()
//            childCoroutine(coroutineContext)
        }
        println("1. Running")
        Thread.sleep(1100)
        job.cancel()
    }

    suspend fun customSuspendedFun() {
        delay(1000)
        println("3. Running")
    }

    suspend fun childCoroutine(coroutineContext: CoroutineContext) {
        val job: Job = GlobalScope.launch(coroutineContext) {
            delay(1000)
            println("3. Running")
        }
    }



















    //обработка исключений
    suspend fun showExceptionHandling() {

        val job: Job = CoroutineScope(Dispatchers.Default).launch() {

            println("2. Running + ${Thread.currentThread().id}")

            val dataA = getAsyncStringA()
            val dataB = getAsyncStringB()

            printResult(dataA.await(), dataB.await())
        }

    }




    //отмена выполнения корутины бросает исключение CancellationException
    //оно обрабатывается по умолчанию, но блок try catch его перехватит
    fun cancelCoroutineWithException() {
        val job: Job = GlobalScope.launch(CustomExceptionHandler()) {
            try {
                delay(1000)
                println("2. Running")
            } catch (ex: Throwable) {
                if (ex !is CancellationException)
                    ex.printStackTrace()
            }
            throw Exception()
        }
        println("1. Running")
        job.cancel()
    }





    class CustomExceptionHandler : CoroutineExceptionHandler {
        override fun handleException(context: CoroutineContext, exception: Throwable) {
            println("Hello from exception handler!")
            exception.printStackTrace()
        }

        override val key: CoroutineContext.Key<*>
            get() = CoroutineExceptionHandler.Key
    }







    //обработка исключений с помощью CoroutineExceptionHandler

    fun handleExceptionWithExceptionHandle() {
        val job: Job = GlobalScope.launch(Dispatchers.Default + CustomExceptionHandler()) {

            throw Exception("Hello, I am exception!")
        }

    }














    //работа с сетевыми запросами с использованием библиотеки Retrofit2 (cont: Continuation / cont: CancellableContinuation)
    suspend fun <T> Call<T>.await(): T = suspendCoroutine { cont ->
        this.enqueue(object: Callback<T> {

            override fun onResponse(call: Call<T>, response: Response<T>) {
                cont.resume(response.body()!!)
            }

            override fun onFailure(call: Call<T>, ex: Throwable) {
                cont.resumeWithException(ex)
            }

        })


    }

    suspend fun getName(): String? {
        return service.getName()?.await()
    }





















    //sampleStart
    fun foo(): Flow<Int> = flow { // flow builder
        for (i in 1..5) {
            delay(100) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }

    fun showFlow(cont: CoroutineContext) = CoroutineScope(cont).launch {
        // Launch a concurrent coroutine to check if the main thread is blocked
        launch {
            for (k in 1..3) {
                println("I'm not blocked $k")
                delay(100)
            }
        }
        // Collect the flow
        foo().collect { any -> println(any) }
    }
//sampleEnd













//    a thread-safe (aka synchronized, linearizable, or atomic) data structure that provides all the necessarily synchronization
//    var counter = AtomicInteger()


    //sampleStart
    suspend fun massiveRun(action: suspend () -> Unit) {
        val n = 100  // number of coroutines to launch
        val k = 1000 // times an action is repeated by each coroutine
        val time = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) { action() }
                    }
                }
            }
        }
        println("Completed ${n * k} actions in $time ms")
    }

    //sampleStart
    @Volatile // in Kotlin `volatile` is an annotation
    var counter = 0

    fun showVolatile(cont: CoroutineContext) = CoroutineScope(cont).launch {
        withContext(Dispatchers.Default) {
            massiveRun {
                counter++
            }
        }
        println("Counter = $counter")
    }
//sampleEnd



}