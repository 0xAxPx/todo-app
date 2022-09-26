package com.todo.functional

import com.todo.util.ToDoTestHelper
import com.todo.pojo.ToDo
import com.todo.util.HTTPCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.slf4j.LoggerFactory

/**
 * Get tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToDoGetRestTests : ToDoTestHelper() {
    companion object {
        private val LOG = LoggerFactory.getLogger(ToDoGetRestTests::class.java)
    }

        @BeforeEach
        fun tearDown() {
            deleteAllToDo()
        }

        //Get ToDos with default limit (retrieve all existing ToDo)
        @Test
        fun testGetDefaultLimitToDoList() {

            assertThat(getAllToDos().isEmpty()).isTrue

            for (id in 1..1000) {
                val todo = ToDo(id, "Test", false)
                LOG.info("POST request with $todo")
                val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

                //POST
                val response = sendPostRequest(values)
                assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)
            }
            //Get list of ToDO
            val todos = getAllToDos()
            assertThat(todos.size).isEqualTo(1000)
        }

    @Test
    fun tesGetToDoListWithLimit() {

        assertThat(getAllToDos().isEmpty()).isTrue

        for (id in 1..200) {
            val todo = ToDo(id, "Test", false)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

            //POST
            val response = sendPostRequest(values)
            assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)
        }
        //Get list of 100 from 200
        var todos = sendGetRequestWithParams("${getLimitOffsetParams.first}100")
        assertThat(todos.size).isEqualTo(100)

        //Get list of 200 from 200
        todos = sendGetRequestWithParams("${getLimitOffsetParams.first}0")
        assertThat(todos.size).isEqualTo(0)

        //Get list of 2000 from 200
        todos = sendGetRequestWithParams("${getLimitOffsetParams.first}2000")
        assertThat(todos.size).isEqualTo(200)
    }

    @Test
    fun testGetToDoListWithOffsetAndLimit() {

        assertThat(getAllToDos().isEmpty()).isTrue

        for (id in 1..20) {
            val todo = ToDo(id, "Test", false)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

            //POST
            val response = sendPostRequest(values)
            assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)
        }
        //Get list of ToDo with id starting from 3 to 12
        val todos = sendGetRequestWithParams("${getLimitOffsetParams.first}10&${getLimitOffsetParams.second}2")
        assertThat(todos.size).isEqualTo(10)
        assertThat(todos[0].id).isEqualTo(3)
        assertThat(todos[todos.size - 1].id).isEqualTo(12)
    }

    @Test
    fun testGetToDoListWithOffset() {

        assertThat(getAllToDos().isEmpty()).isTrue

        for (id in 1..200) {
            val todo = ToDo(id, "Test", false)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

            //POST
            val response = sendPostRequest(values)
            assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)
        }
        //Get list of 100 from 200
        var todos = sendGetRequestWithParams("${getLimitOffsetParams.second}198")
        assertThat(todos.size).isEqualTo(2)

        //Get list of 200 from 200
        todos = sendGetRequestWithParams("${getLimitOffsetParams.second}0")
        assertThat(todos.size).isEqualTo(200)

        //Get list of 2000 from 200
        todos = sendGetRequestWithParams("${getLimitOffsetParams.second}2000")
        assertThat(todos.size).isEqualTo(0)
    }
}