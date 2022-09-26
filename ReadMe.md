
FUNCTIONAL TESTS:
1) ToDo fields
 - "Completed" accepts only BOOLEAN data i.e. HTTP code - 200, otherwise - 400
   Apr 14 10:24:08.803 DEBUG create_todo: Todo { id: 93, text: "Test", completed: true }
   Apr 14 10:24:08.803  INFO 172.17.0.1:46472 "POST /todos HTTP/1.1" 201 "-" "Java-http-client/11.0.14.1" 2.249227ms
   Apr 14 10:24:08.803  INFO 172.17.0.1:46472 "POST /todos HTTP/1.1" 201 "-" "Java-http-client/11.0.14.1" 2.249227ms 
 - "Text" - accept String 
 - "ID" - accept 64bit values
2) POST
Issue#1 (Minor) ("Pass response body - id already exists: #ID): When trying to post the same ToDo (with duplicate IDS) , server return 400 (Bad Request) but without response body / exception

PERFORMANCE TESTS:
1 Test Class: /src/test/kotlin/com/todo/performance/TodoPostLoadTests.kt
2 ENV: VM 4 Cores && 8GB RAM
3 Results: 
3.1 100_000/4 threads:
[main] INFO com.todo.performance.TodoPostLoadTests - Percentile 99% =  14.0ms
[main] INFO com.todo.performance.TodoPostLoadTests - Percentile 95% =  7.0ms
[main] INFO com.todo.performance.TodoPostLoadTests - Percentile 75% =  3.0ms
Elapsed time (total time of running tests) - 1.4min

3.2 300_000/4 threads:
[main] INFO com.todo.performance.TodoPostLoadTests - Percentile 99% =  15.0ms
[main] INFO com.todo.performance.TodoPostLoadTests - Percentile 95% =  9.0ms
[main] INFO com.todo.performance.TodoPostLoadTests - Percentile 75% =  4.0ms
Elapsed time (total time of running tests) - 7min

3.3 1000_000/4 threads:
[main] INFO com.todo.performance.TodoPostLoadTests - Percentile 99% =  19.0ms
[main] INFO com.todo.performance.TodoPostLoadTests - Percentile 95% =  11.0ms
[main] INFO com.todo.performance.TodoPostLoadTests - Percentile 75% =  7.0ms
Elapsed time (total time of running tests) - 44min

Total time of tests increased significantly with increasing tests load but resp time performance look like linear
P.S. Total time values seems for me quite questionable, it is likely instrumentation issue (VM capacity bottleneck) contributed into such irrelevant data (eg 300k vs 1000_000k) 
