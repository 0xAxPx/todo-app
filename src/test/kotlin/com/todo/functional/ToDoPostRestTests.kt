package com.todo.functional

import com.todo.util.ToDoTestHelper
import com.todo.pojo.ToDo
import com.todo.util.HTTPCode
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

/**
 * POST tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToDoPostRestTests : ToDoTestHelper() {
    companion object {
        private val LOG = LoggerFactory.getLogger(ToDoPostRestTests::class.java)
        private val ids = mutableListOf<Any>()
    }

    @Nested
    class PositiveCases {

        @AfterEach
        fun tearDown() {
            deleteAllToDo()
        }

        @ParameterizedTest(name = "{index} => post with {0} : {1} : {2}")
        @CsvFileSource(resources = ["/post_data.csv"],)
        fun testPostWithRandomSignedIds(testId: String?, testText: Any, testCompleted: Any) {
            val id = Random().nextInt(Random().nextInt(1_000_000))
            ids.add(id)
            val completed = (((testCompleted to Boolean) as Pair<*,*>).first as String).toBoolean()
            val todo = ToDo(id, testText, testCompleted)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to id, "text" to todo.text, "completed" to completed)

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(HTTPCode.CREATED.value)
            Assertions.assertThat(response.body()).isEmpty()

            //GET
            val toDoList = getAllToDos()
            Assertions.assertThat(toDoList.contains(todo))
        }

        //ToDo Ids
        @ParameterizedTest
        @ValueSource(longs = [Long.MAX_VALUE, Long.MAX_VALUE - 1, 0])
        fun testIdsValues(testId: Long) {
            val todo = ToDo(testId, "Test", false)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(HTTPCode.CREATED.value)
        }

        //ToDo Text
        @ParameterizedTest
        @ValueSource(strings = [
            "Test",
            "qwertyuiopp[]\';lkjhgfdsazxcvbnm,./=-0987654321`/.,mnbvcxz';lkjhgfdsa][\\poiuytrewq1-0987654321",
            "//\\%)$"
        ])
        fun testTextValues(text: String) {
            val todo = ToDo(Random().nextInt(Int.MAX_VALUE), text, false)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(HTTPCode.CREATED.value)
        }

        //ToDo Text
        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun testCompletedValues(completed: Boolean) {
            val todo = ToDo(Random().nextInt(Int.MAX_VALUE), "Completed: $completed", completed)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(HTTPCode.CREATED.value)
        }

        //ToDo request with duplicates
        @Test
        fun testRequestWithDuplicateIDS() {
            val todo = ToDo(1234567890, "First", false)
            LOG.info("POST request with $todo")
            var values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed)

            var requestBody = mapper.writeValueAsString(values)
            var postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            var response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(HTTPCode.CREATED.value)

            val todoDuplicated = todo.copy(text = "Duplicated")

            LOG.info("POST request with $todo")
            values = mapOf("id" to todoDuplicated.id, "text" to todoDuplicated.text, "completed" to todoDuplicated.completed)

            requestBody = mapper.writeValueAsString(values)
            postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(HTTPCode.BAD_REQUEST.value)

        }
    }

    @Nested
    class NegativeCases {
        @ParameterizedTest(name = "{index} => post with {0} : {1} : {2}")
        @ValueSource(longs = [Long.MIN_VALUE, -1])
        fun testPostWithEdgeIds(testId: Long) {
            val todo = ToDo(testId, "Test $testId", false)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed )

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(HTTPCode.BAD_REQUEST.value)
        }

        @ParameterizedTest(name = "{index} => post with {0} : {1} : {2}")
        @ValueSource(ints = [Int.MAX_VALUE, 0])
        fun testPostWithOffRangeTextValue(testText: Int) {
            val todo = ToDo(Random().nextInt(Random().nextInt(1_000_000)), testText, false)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to todo.id, "text" to todo.text, "completed" to todo.completed )

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(HTTPCode.BAD_REQUEST.value)
        }

        @ParameterizedTest(name = "{index} => post with {0} : {1} : {2}")
        @CsvSource(
            ",Text,3445",
            ",Text,''"
        )
        fun testPostWithOfFRangeCompletedValue(testId: String?, testText: String, testCompleted: Any) {
            val id = Random().nextInt(Random().nextInt(1_000_000))
            val todo = ToDo(id, testText, testCompleted)
            LOG.info("POST request with $todo")
            val values = mapOf("id" to id, "text" to todo.text, "completed" to testCompleted)

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(HTTPCode.BAD_REQUEST.value)
        }

        @Test
        fun testIdIsNull() {
            val id = null
            val text = "Text"
            val completed = true
            val values = mapOf("id" to id, "text" to text, "completed" to completed)

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(400)

        }

        @Test
        fun testTextIsNull() {
            val id = Random().nextInt(Random().nextInt(1_000_000))
            val text = null
            val completed = true
            val values = mapOf("id" to id, "text" to text, "completed" to completed)

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(400)
        }

        @Test
        fun testCompletedIsNull() {
            val id = Random().nextInt(Random().nextInt(1_000_000))
            val text = "Completed is Null"
            val completed = null
            val values = mapOf("id" to id, "text" to text, "completed" to completed)

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(400)
        }

        @Test
        fun testAllFieldsNull() {
            val id = null
            val text = null
            val completed = null
            val values = mapOf("id" to id, "text" to text, "completed" to completed)

            val requestBody = mapper.writeValueAsString(values)
            val postRequest = HttpRequest.newBuilder()
                .header(content_type_json.first, content_type_json.second)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(BASE_URL))
                .build()

            val response = client.send(postRequest, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(400)
        }
    }
}