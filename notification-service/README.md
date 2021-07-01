## [POC] 4PROCESS Notification service

- Java 11
- MongoDB

### SetUp

- First build ```gradlew clean build```
- The development environment can be easily setup by running ```docker-compose up --build```
    -   It will start two docker containers, one with mongoDB and another with this service 

### SetUp - db querys template

- Run command ```npm install```
- To run a query. Choose a query directory and run ```node .\<query_file_name>```