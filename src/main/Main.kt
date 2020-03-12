package main

import kotlinx.coroutines.*

fun main() = runBlocking {

    val suspendFunction = SuspendFunction()



    suspendFunction.base()




//    //не наследуется контекст, нужно передавать параметром
//    val context = CoroutineName("test")
//
//    val job : Job = GlobalScope.launch(context) {
//        println("1. Running in ${coroutineContext[CoroutineName]}")
//
//        suspendFunction.demoContext()
//
//    }


//        suspendFunction.contexChanging()


//    println("1. Running in ${Thread.currentThread().id}")
//    suspendFunction.showWork()

//    println("1. Running in ${Thread.currentThread().id}")
//    suspendFunction.showAsyncWork()


//    отменить работу корутины и всех дочерних корутин
//        suspendFunction.cancelCoroutine()




    //отмена выполнения корутины бросает исключение CancellationException
    //оно обрабатывается по умолчанию, но блок try catch его перехватит
//    suspendFunction.cancelCoroutineWithException()




    //обработка исключений с помощью CoroutineExceptionHandler
//    suspendFunction.handleExceptionWithExceptionHandle()





//    suspendFunction.getName()


    Thread.sleep(5000)
}
