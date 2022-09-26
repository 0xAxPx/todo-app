package com.todo.performance

import com.todo.util.HTTPCode
import com.todo.util.ToDoTestHelper.Companion.sendPostRequest
import org.assertj.core.api.Assertions.assertThat
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Callable


class PostRequestTask(private val values: Map<String, Any>) : Callable<Double> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun call(): Double {
        log.info("Sending POST request with $values")
        val start = Instant.now()
        val response  = sendPostRequest(values)
        val elapsed = Duration.between(start, Instant.now()).toMillis()
        log.info("Request elapsed time: $elapsed ms")
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)
        return elapsed.toDouble()
    }
}
