User Device Service
========================

This project is (supposed to be) a microservice in a home automation system that manages user/device relationship.

## Why

To show how I write basic REST APIs and try out some libraries.

## Tech Stack

1. [Spring Boot](https://projects.spring.io/spring-boot/): web framework
2. [Hibernate](http://hibernate.org): for ORM and validation
3. [MySQL](https://www.mysql.com): as the primary database
4. [Memcached](http://memcached.org) for caching and [Nginx](https://www.nginx.com) as reverse proxy
4. [Swagger/OpenAPI](https://swagger.io): for API documentation
5. [SLF4J](https://www.slf4j.org): logging
6. [Docker Compose](https://docs.docker.com/compose/) and [Kubernetes](https://kubernetes.io): for deployment

## Entity

There are four entities managed by this service, `User`, `Device`, `DeviceType` and `Binding`. `User` and `Device` represents people and their appliances in the system. `DeviceType` is a class or category of devices, e.g., `Hue Light` or `thermometer`, that take the same commands or report status in the same format. `Binding` represents ownership of devices, a user can only control or read status of a device if there is a `Binding` record between them.

The schema is in `/db/schema.sql` file. Some brief notes is listed below.

1. `name` column in `User` should be named `username` and is unique in the table.
2. `password` field should already be hashed before reaching this service (perhaps in API gateway or some previous layer). Therefore, no hashing/salt being performed here (security is delegated to the previous layer).
3. For the same reason, `Binding` creation does not check if `pinCode` matches the device's code.
4. Probably could have add a unique constraint on (manufacturer, modelname) pair in `DeviceType`.

## Code

The project is organized in features, e.g., `User`, `Device`, etc. In each feature area, there are three simple layer. `controller` as the API's entry point, `repository` for abstracting access to databases and `service` for encapsulating external services or aggregating calls to `repository`.

## API

Basic CRUD API sets for each entity except for `Binding`. Due to the limited fields in `Binding`, there is no point to have update API.

`PUT` are used for full update (replace the entire state/object), which means that the request body must contain the entire object. `PATCH` is used for partial update but the request body must contain at least one updatable field. Plus, if any updatable field is present, it must be valid.

`Binding`'s query API support embedded entity, setting query parameter `entities` to `device` will query `Binding` with their associated `Device`.

Swagger source is in `/doc/api.yaml` and the API document can be found [here](https://app.swaggerhub.com/api/sevenlol1007/user-device_service_api/1.0.0).

## Config

Place a property file or yaml file in the locations `Spring` check. Some important items are listed below. An example file can be found in `/config/application.properties`.

1. `spring.datasource.url`: MySQL url
2. `spring.datasource.username` and `spring.datasource.password`: username and password of MySQL
3. `spring.datasource.driver-class-name`: MySQL driver
4. `memcached.cache.servers`: Memcached url

## Build

`./gradlew build`: building a fat jar
`./gradlew bootRun`: for running it locally
`./gradlew build docker`: for building a fat jar **and** creating a docker image

The server expose port `8080`

## Deploy

**1. Docker Compose**

Run `docker-compose up -d` to spin up nginx, memcached, api server and MySQL.

Nginx config file is in `/config/nginx.conf`. MySQL schema (`/db/schema.sql`) will be imported with `--init-file` command. MySQL password will be set to environment variable `MYSQL_PASSWORD`. `/scripts/cleanup.sh` can be used to cleanup dangling volumn and such.

**2. Kubernetes**

Deployed using [minikube](https://github.com/kubernetes/minikube).

All yaml files are stored in `k8s/` directory:
1. `pv001.yaml`: Persistent volume with `hostpath` for MySQL.
2. `mysql-pw.yaml`, `mysql-schema.yaml`: `Secret` for MySQL password and `ConfigMap` for database schema.
3. `mysql.yaml`: Persistent volume claim for storage, deployment for a single MySQL instance and headless service for discovery. Schema will be mounted at path `/docker-entrypoint-initdb.d` and tables will be created on startup.
4. `memcached.yaml`: A headless service and a single instance deployment for memcached.
5. `api-config.yaml`: `ConfigMap`s containing nginx and api server's config file.
6. `api.yaml`: A deployment that manages pods containing one nginx server as reverse proxy and one api server. A service for exposing the api server using `NodePort`.

Readiness and liviness probe with path `/ready` and `/healthy` is configured for the api server container.

**Build**

Link minikube with the local docker daemon

`eval $(minikube docker-env)`

Build

`./gradlew build docker`

**Deploy**

Deploy the objects in the following order.
1. `pv001.yaml`
2. `mysql-pw.yaml`, `mysql-schema.yaml`
3. `mysql.yaml`
4. `memcached.yaml`
5. `api-config.yaml`
6. `api.yaml`

Retrieve the service url with `minikube service user-device --url`

## Examples

Scripts in directory `examples/` show basic API calls of `UserDeviceService`.

These scripts assume the server listening on `localhost:8080`. If not, modify the `HOST` & `PORT` variables.

If the service is deployed using `kubernetes`, use `kubectl get pods` to get one of the `pod` ID of the API server, e.g., `user-device-api-7c4456f6b5-57qmf`. After that, use `kubectl port-forward user-device-api-7c4456f6b5-57qmf 8080:8080` to consume the service using `localhost:8080`.

The description of each script is shown below:
1. `01_device_type_basic.sh`, `02_device_basic.sh`, `03_user_basic.sh`: CRUD examples for `DeviceType`, `Device` and `User`.
2. `04_binding_basic.sh`: Create `Binding` between a `User` & `Device`.
3. `05_binding_query.sh`: Different ways to query `Binding`.
