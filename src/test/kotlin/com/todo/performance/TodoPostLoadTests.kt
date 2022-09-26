package com.todo.performance

import com.todo.pojo.ToDo
import com.todo.util.HTTPCode
import com.todo.util.ToDoTestHelper
import org.apache.commons.math3.stat.StatUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

@DisplayName("Performance Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoPostLoadTests : ToDoTestHelper(){

    private val log = LoggerFactory.getLogger(this.javaClass)

    @BeforeEach
    fun tearDown() {
        // delete all test ToDos
        deleteAllToDo()
    }

    @ParameterizedTest
    @ValueSource(ints = [100_000, 300_000, 1000_000])
    @DisplayName("Load tests for POST")
    fun runParallel(size: Int) {
        val collections = CopyOnWriteArrayList<Double>()

        //warm up
        log.info("======== WARM UP ==========")
        runOneThreadTests(2_000)
        deleteAllToDo()
        Thread.sleep(2_000L)

        val numCPU = Runtime.getRuntime().availableProcessors()
        val executorService = Executors.newFixedThreadPool(numCPU)
        log.info("======== RUNNING with $numCPU CPU available ==========")
        val futureTasks = mutableListOf<Callable<Double>>()

        val todoTasksToExecute = mutableListOf<Map<String, Any>>()
        for (id in 1..size) {
            todoTasksToExecute.add(mapOf("id" to id, "text" to "Post #$id", "completed" to true))
        }

        log.info("Execute task of POST request sending....")
        for (task in todoTasksToExecute) {
            futureTasks.add(PostRequestTask(task))
        }

        try {
            val start = System.currentTimeMillis()
            val results = executorService.invokeAll(futureTasks)
            var sum = 0.0
            for (f in results) {
                collections.add(f.get())
                sum += f.get()
            }
            val elapsed = System.currentTimeMillis() - start
            log.info("Elapsed time $elapsed ms")
        } finally {
          executorService.shutdown()
        }

        assertThat(collections.size).isEqualTo(size)

        log.info("Percentile 99% =  ${StatUtils.percentile(convertToArray(collections, size), 99.0)}ms")
        log.info("Percentile 95% =  ${StatUtils.percentile(convertToArray(collections, size), 95.0)}ms")
        log.info("Percentile 75% =  ${StatUtils.percentile(convertToArray(collections, size), 75.0)}ms")
    }

    private fun convertToArray(collection: CopyOnWriteArrayList<Double>, arraySize: Int): DoubleArray {
        val doubleArr = DoubleArray(arraySize)
        for ((i, value) in collection.withIndex()) {
            doubleArr[i] = value
        }
        return doubleArr
    }

    private fun runOneThreadTests(requests: Int) {
        for (n in 1..requests) {
            val todo = ToDo(n, "TEST", "false".toBoolean())
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)
            assertThat(sendPostRequest(values)!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)
        }
    }
}