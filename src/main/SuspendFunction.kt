package main

import jdk.nashorn.internal.codegen.CompilerConstants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.coroutines.*

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
    suspend fun contexChanging() {

        val job: Job = GlobalScope.launch(Dispatchers.IO) {
            println("1. Running in ${Thread.currentThread().id}")

            val data = withContext(Dispatchers.Default) {
                println("2. Running in ${Thread.currentThread().id}")
                customSuspendedFun2()
            }

            println("4. Running in ${Thread.currentThread().id}")
        }
    }

    suspend fun customSuspendedFun2() {
        delay(1000)
        println("3. Running in ${Thread.currentThread().id}")
    }















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














    //работа с сетевыми запросами с использованием библиотеки Retrofit (cont: Continuation / cont: CancellableContinuation)
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









}