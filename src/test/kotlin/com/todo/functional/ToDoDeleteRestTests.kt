package com.todo.functional

import com.todo.util.ToDoTestHelper
import com.todo.pojo.ToDo
import com.todo.util.HTTPCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.slf4j.LoggerFactory

/**
 * DELETE tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToDoDeleteRestTests : ToDoTestHelper() {
    companion object {
        private val LOG = LoggerFactory.getLogger(ToDoDeleteRestTests::class.java)
    }

        @BeforeEach
        fun tearDown() {
            deleteAllToDo()
        }

        //Try to delete ToDO twice
        @Test
        fun testDeleteToDoTwice() {
            val id = 1234567
            val todo = ToDo(id, "Test", false)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

            //POST
            var response = sendPostRequest(values)
            assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)

            //DELETE first time
            response = deleteToDoByID(todo.id)
            assertThat(response!!.statusCode()).isEqualTo(HTTPCode.NO_CONTENT.value)

            //DELETE second time
            response = deleteToDoByID(todo.id)
            assertThat(response!!.statusCode()).isEqualTo(HTTPCode.NOT_FOUND.value)
        }

    //Try to delete ToDO which does not exist
    @Test
    fun testDeleteNotExistingToDo() {

        assertThat(getAllToDos().isEmpty()).isTrue

        val response = deleteToDoByID(123456789)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.NOT_FOUND.value)
    }
}