#!/bin/bash

# CRUD for DeviceType
# jq must be installed

# create a DeviceType
# if succeeded, HTTP status code will be 201
# response body will contain the type value of the created DeviceType
# E.g., {"device_type":"1"}
DEVICE_TYPE=$(curl -sX POST \
  http://localhost:8080/types/devices \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "light",
	"modelname" : "Hue",
	"manufacturer" : "Philips"
}' | jq -r '.device_type')

URL="http://localhost:8080/types/devices/$DEVICE_TYPE"

# retrieve the newly created DeviceType
echo "Newly created DeviceType($DEVICE_TYPE):"
curl -s $URL | jq

# full update
# (if not all required fields are present in body, this will fail)
curl -sX PUT \
  $URL \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "hue_light",
	"modelname" : "Hue Light",
	"manufacturer" : "Philips"
}' -o /dev/null

echo "======================="
# retrieve the updated DeviceType
echo "Updated DeviceType($DEVICE_TYPE):"
curl -s $URL | jq

# partial update (name field)
# only one field needs to be present
curl -sX PATCH \
  $URL \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "hue_light_updated_again"
}' -o /dev/null

echo "======================="
# retrieve the updated DeviceType
echo "Partially updated DeviceType($DEVICE_TYPE):"
curl -s $URL | jq

# delete
curl -sX DELETE $URL -o /dev/null

echo "======================="
# try to retrieve the deleted DeviceType
# and fail (404)
echo "Deleted DeviceType($DEVICE_TYPE):"
curl -s $URL
echo
