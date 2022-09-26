package com.todo.smoke

import com.todo.pojo.ToDo
import com.todo.util.HTTPCode
import com.todo.util.ToDoTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.Random

@DisplayName("Smoke Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoRestSmokeTests : ToDoTestHelper(){

    @Test
    @DisplayName("Smoke tests for POST/GET/PUT/DELETE")
    fun runSmoke() {

        // POST
        val id = Random().nextInt(Random().nextInt(1_000))
        val todo = ToDo(id, "TEST", "false".toBoolean())
        var values = mapOf("id" to id, "text" to todo.text, "completed" to todo.completed)
        var response = sendPostRequest(values)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.CREATED.value)
        assertThat(response.body()).isEmpty()

        //GET
        var toDoList = getAllToDos()
        assertThat(toDoList.contains(todo))

        //PUT - amend fields
        values = mapOf("id" to id, "text" to "UPDATED", "completed" to true)
        response = sendPutRequest(id, values)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.OK.value)
        assertThat(response.body()).isEmpty()
        toDoList = getAllToDos()
        assertThat(toDoList.contains(ToDo(values["id"] as Number, values["text"] as Any, values["completed"] as Boolean)))

        //DELETE
        response = deleteToDoByID(id)
        assertThat(response!!.statusCode()).isEqualTo(HTTPCode.NO_CONTENT.value)
    }
}