## CloudEvents Restful WS + Spring Boot + Jersey example

This sample shows how to create a Spring Boot application that accepts CloudEvents and replies with CloudEvents.

To run it:

```shell
mvn spring-boot:run
```

You can try sending a request using `curl`:

```shell
curl -X POST -v -d '{"username": "slinkydeveloper", "firstName": "Francesco", "lastName": "Guardiani", "age": 23}' \                                                                                                                       ~
    -H'Content-type: application/json' \
    -H'Ce-id: 1' \
    -H'Ce-source: cloud-event-example' \
    -H'Ce-type: happybirthday.myapplication' \
    -H'Ce-specversion: 1.0' \
    http://localhost:8080/happy_birthday
```
