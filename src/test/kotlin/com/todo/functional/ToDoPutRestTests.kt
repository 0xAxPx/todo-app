package com.todo.functional

import com.todo.util.ToDoTestHelper
import com.todo.pojo.ToDo
import com.todo.util.HTTPCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.slf4j.LoggerFactory

/**
 * PUT tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToDoPutRestTests : ToDoTestHelper() {
    companion object {
        private val LOG = LoggerFactory.getLogger(this.javaClass)
    }

        @BeforeEach
        fun tearDown() {
            deleteAllToDo()
        }

        @Test
        fun testPutRequestWithDuplicateIds() {
            val id = 1234567
            val todo = ToDo(id, "Test", false)
            LOG.info("POST request with $todo")
            var values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

            //POST
            var response = sendPostRequest(values)
            assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)
            response = deleteToDoByID(todo.id)
            assertThat(response!!.statusCode()).isEqualTo(204)

            //PUT
            val updatedTodo = todo.copy(text = "UPDATED")
            values = mapOf("id" to updatedTodo.id, "text" to updatedTodo.text, "completed" to updatedTodo.completed)
            response = sendPutRequest(id, values)

            assertThat(response!!.statusCode()).isEqualTo(HTTPCode.NOT_FOUND.value)
        }

    @Test
    fun testPutRequestWithChangingIDS() {
        val id = 1234567
        val todo = ToDo(id, "Test", false)
        LOG.info("POST request with $todo")
        var values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

        //POST
        var response = sendPostRequest(values)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)

        //PUT
        val updatedTodo = todo.copy(id = 123456789 , text = "UPDATED with 123456789")
        values = mapOf("id" to updatedTodo.id, "text" to updatedTodo.text, "completed" to updatedTodo.completed)
        response = sendPutRequest(id, values)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.OK.value)

        //Get todos and check ToDo updated (id = 123456789) is in the list, old one - not (id = 1234567)
        val todos = getAllToDos()
        assertThat(todos.contains(updatedTodo)).isTrue
        assertThat(todos.contains(todo)).isFalse
    }

    @Test
    fun testPutRequestWithInvalidValues() {
        val id = 1234567
        val todo = ToDo(id, "Test", false)
        LOG.info("POST request with $todo")
        var values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

        //POST
        var response = sendPostRequest(values)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)

        //PUT
        var updatedTodo = todo.copy(id = 123456789.012 , text = "UPDATED with 123456789")
        values = mapOf("id" to updatedTodo.id, "text" to updatedTodo.text, "completed" to updatedTodo.completed)
        response = sendPutRequest(id, values)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.BAD_REQUEST.value)

        updatedTodo = todo.copy(id = "" , text = "UPDATED with 123456789")
        values = mapOf("id" to updatedTodo.id, "text" to updatedTodo.text, "completed" to updatedTodo.completed)
        response = sendPutRequest(id, values)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.BAD_REQUEST.value)

        updatedTodo = todo.copy(id = id, text = 345)
        values = mapOf("id" to updatedTodo.id, "text" to updatedTodo.text, "completed" to updatedTodo.completed)
        response = sendPutRequest(id, values)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.BAD_REQUEST.value)

        updatedTodo = todo.copy(id = id, completed = "")
        values = mapOf("id" to updatedTodo.id, "text" to updatedTodo.text, "completed" to updatedTodo.completed)
        response = sendPutRequest(id, values)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.BAD_REQUEST.value)
    }
}