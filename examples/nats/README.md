# NATS Example

This example assumes you are either running the NATs Server locally or you have the location in an environment variable `NATS_URLS` in expected nats format (e.g. `NATS_URLS=nats://localhost:4222`).

You can either specify the NATS `subject` in the environment variable `NATS_SUBJECT` or it will provide a default.

To run up a [NATS server](https://hub.docker.com/_/nats) without installing it, simply use:

```shell
docker run -d --name nats-main -p 4222:4222 -p 6222:6222 -p 8222:8222 nats
```
