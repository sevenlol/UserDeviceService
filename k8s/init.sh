#!/bin/bash

# persistent volume for mysql
kubectl create -f pv001.yaml

# mysql
kubectl create -f mysql-schema.yaml
kubectl create -f mysql-pw.yaml
kubectl create -f mysql.yaml

# memcached
kubectl create -f memcached.yaml

# UserDevice api server
kubectl create -f api-config.yaml
kubectl create -f api.yaml
