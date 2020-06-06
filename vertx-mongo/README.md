# VERTx gradle POC

- To run use `gradle vertxRun`

- Simple Api to insert, delete and fetch employees

- Endpoints exposed: 
    - GET "/api/employees"
    - GET "/api/employees/:id"
    - DELETE "/api/employees/:id"
    - POST "/api/employees"

## Research

### INFO

- Vert.x provides a non-blocking, event-driven runtime.
- Vert.x is based on the Netty project, a high-performance asynchronous networking library for the JVM. 
- Vert.x applications are written using an ACTOR-LIKE (take a look) concurrency model. An application consists of several components, the so-called Verticles, which run independently. A Verticle runs single-threaded and communicates with other Verticles by exchanging messages on the global event-bus. Because they do not share state, Verticles can run in parallel. The result is an easy to use approach for writing multi-threaded applications.You can create several Verticles which are responsible for the same task and the runtime will distribute the workload among them, which means you can take full advantage of all CPU cores without much effort.
- Verticles can also be distributed between several machines. This will be transparent to the application code. The Verticles use the same mechanisms to communicate as if they would run on the same machine. This makes it extremely easy to scale your application.

LINK -> https://vertx.io/docs/guide-for-java-devs/

### PRO

- Simple to use concurrency
- it can deal with more concurrent network connections with less threads than synchronous APIs such as Java servlets
- polyglot as it supports a wide range of popular JVM languages: Java, Groovy, Scala, Kotlin, JavaScript, Ruby and Ceylon.


### CONS

- difficult to read, to write and to debug.




