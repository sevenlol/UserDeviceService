#!/bin/bash

# CRUD for Device
# jq must be installed

HOST=localhost
PORT=8080

# create a DeviceType (light)
DEVICE_TYPE=$(curl -sX POST \
  http://$HOST:$PORT/types/devices \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "light",
	"modelname" : "Hue",
	"manufacturer" : "Philips"
}' | jq -r '.device_type')

# create a Device with specified DeviceType (living room light)
# if succeeded, HTTP status code will be 201
# response body will contain the created Device's ID
# E.g., {"device_id":"1"}
DEVICE_ID=$(curl -sX POST \
  http://$HOST:$PORT/devices \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"type": '$DEVICE_TYPE',
    "name": "living room light",
    "mac": "aabbabc00012",
    "pin_code": 1235
}' | jq -r '.device_id')

URL=http://$HOST:$PORT/devices/$DEVICE_ID

# retrieve the newly created Device
echo "Newly created Device($DEVICE_ID), Type($DEVICE_TYPE):"
curl -s $URL | jq
echo "======================="

# full update
# (if not all required fields are present in body, this will fail)
# if specified DeviceType does not exist => 404
# if specified MAC address already used => 409
curl -sX PUT \
  $URL \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"type": '$DEVICE_TYPE',
    "name": "bedroom light",
    "mac": "aabbabc00012",
    "pin_code": 9876
}' -o /dev/null

# retrieve the updated Device
# notice that name & mac field changed
echo "Updated Device($DEVICE_ID):"
curl -s $URL | jq
echo "======================="

# partial update (name field)
# only one field needs to be present
# integrity constraint same as full update
curl -sX PATCH \
  $URL \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "living room light"
}' -o /dev/null

# retrieve the updated Device
echo "Partially updated Device($DEVICE_ID):"
curl -s $URL | jq
echo "======================="

# delete
echo "Delete Device($DEVICE_ID)"
curl -sX DELETE $URL -o /dev/null

# try to retrieve the deleted Device
# and fail (404)
echo "Retrieve deleted Device($DEVICE_ID):"
curl -s $URL
echo

# delete DeviceType
curl -sX DELETE http://$HOST:$PORT/types/devices/$DEVICE_TYPE -o /dev/null
