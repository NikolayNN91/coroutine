package main

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.NonCancellable.children
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.*

class ContextDemo {
//    suspend fun sum(): Int {
//        val jobs = mutableListOf<Deferred<Int>>()
//        for(child in children){
//            jobs += async {  // we lose our context here!
//                child.evaluate()
//            }
//        }
//        return jobs.awaitAll().sum()
//    }

}