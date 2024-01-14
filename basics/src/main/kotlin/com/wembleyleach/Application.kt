package com.wembleyleach

import kotlinx.coroutines.*

fun main() = runBlocking {
    jobHandle()
}

suspend fun jobHandle() = coroutineScope {
    val job = launch {
        delay(1000L)
        println("World!")
    }
    println("Hello")
    job.join()
    println("Done")
}

suspend fun doHello() {
    doWorld()
    println("Done")
}

suspend fun doWorld() = coroutineScope {
    launch {
        delay(2000L)
        println("World 2")
    }
    launch {
        delay(1000L)
        println("World 1")
    }
    println("Hello,")
}
