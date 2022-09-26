package com.todo.util

import com.todo.pojo.ToDo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.*
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

open class ToDoTestHelper {

    companion object {
        init {}
        private val log = LoggerFactory.getLogger(ToDoTestHelper::class.java)
        const val BASE_URL = "http://localhost:8080/todos"

        val content_type_json = Pair("content-type","application/json")
        private val authorization = Pair("admin", "admin")
        val getLimitOffsetParams = Pair("limit=", "offset=")
        val client: HttpClient = HttpClient.newBuilder().build()
        val mapper = ObjectMapper().registerKotlinModule()

        fun basicAuth(username: String, password: String) : String{
            val encoded = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
            return "Basic $encoded"
        }

        //GET
        fun getAllToDos(): Array<ToDo> {
            val request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL))
                .header(content_type_json.first, content_type_json.second)
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            assertThat(response.statusCode()).isEqualTo(HTTPCode.OK.value)
            return  mapper.readValue(response.body(), Array<ToDo>::class.java)
        }

        //GET with limit
        fun sendGetRequestWithParams(params: String): Array<ToDo> {
            val request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("${BASE_URL}/?${params}"))
                .header(content_type_json.first, content_type_json.second)
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            assertThat(response.statusCode()).isEqualTo(HTTPCode.OK.value)
            return  mapper.readValue(response.body(), Array<ToDo>::class.java)
        }

        //DELETE by Id
        fun deleteToDoByID(id: Any): HttpResponse<String>? {
            val deleteRequest = HttpRequest.newBuilder()
                .header("Authorization", basicAuth(authorization.first, authorization.second))
                .header(content_type_json.first, content_type_json.second)
                .uri(URI.create("$BASE_URL/$id"))
                .DELETE()
                .build()
            return client.send(deleteRequest, HttpResponse.BodyHandlers.ofString())
        }

        //DELETE all
        fun deleteAllToDo(): Unit {
            val todos = getAllToDos()
            log.info("Delete ${todos.size} ToDos...")
            todos.forEach { todo ->
                run {
                    val response = deleteToDoByID(todo.id)
                    assertThat(response!!.statusCode()).isEqualTo(HTTPCode.NO_CONTENT.value)
                }
            }
            assertThat(getAllToDos().isEmpty()).isTrue
        }

        //POST
        fun sendPostRequest(values: Map<String, Any>): HttpResponse<String>? {
            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            return client.send(postRequest, HttpResponse.BodyHandlers.ofString())
        }

        //PUT
        fun sendPutRequest(id: Int, values: Map<String, Any>): HttpResponse<String>? {
            val requestBody = mapper.writeValueAsString(values)
            val putRequest = HttpRequest.newBuilder()
                .header("Authorization", basicAuth(authorization.first, authorization.second))
                .header(content_type_json.first, content_type_json.second)
                .uri(URI.create("$BASE_URL/$id"))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build()
            return client.send(putRequest, HttpResponse.BodyHandlers.ofString())
        }
    }
}