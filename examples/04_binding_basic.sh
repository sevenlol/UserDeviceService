#!/bin/bash

# Basic API for Binding (create, get, delete)
# jq must be installed

HOST=localhost
PORT=8080

# create device type
DEVICE_TYPE=$(curl -sX POST \
  http://$HOST:$PORT/types/devices \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "Machine",
	"modelname" : "Northen Light",
	"manufacturer" : "IFT, Inc."
}' | jq -r '.device_type')

# create device
DEVICE_ID=$(curl -sX POST \
  http://$HOST:$PORT/devices \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"type": '$DEVICE_TYPE',
    "name": "Machine V1",
    "mac": "aabbabc00013",
    "pin_code": 1234
}' | jq -r '.device_id')

# create user
USER_ID=$(curl -sX POST \
  http://$HOST:$PORT/users \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "machine1337",
	"email":"haroldfinch@poi.com",
	"password":"$2y$10$JcFKLS3oZ5T6VPzvvrHGde6fwouhiD/ghfH6DGO.6EXJcPjd7mNX."
}' | jq -r '.user_id')

# create Binding between previous created User & Device
# referential integrity on user_id & device_id field => 404 if not valid
BINDING_ID=$(curl -sX POST \
  http://$HOST:$PORT/bindings \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"user_id" : "'$USER_ID'",
	"device_id":"'$DEVICE_ID'"
}' | jq -r '.binding_id')

URL=http://$HOST:$PORT/bindings/$BINDING_ID

# retrieve the newly Binding record
# notice the 'device' field containing the embedded device object
echo "Newly created Binding($BINDING_ID) between User($USER_ID) & Device($DEVICE_ID):"
curl -s $URL | jq
echo "======================="

# delete Binding record (Unbind device)
echo "Delete Binding($BINDING_ID) between User($USER_ID) & Device($DEVICE_ID)"
curl -sX DELETE http://$HOST:$PORT/bindings/$BINDING_ID -o /dev/null

# trying to retrieve deleted Binding record => 404
echo "Trying to retrieve deleted Binding($BINDING_ID):"
curl -s $URL
echo
echo "======================="

# cleanup
echo "Delete User($USER_ID)"
curl -sX DELETE http://$HOST:$PORT/users/$USER_ID -o /dev/null
echo "Delete Device($DEVICE_ID)"
curl -sX DELETE http://$HOST:$PORT/devices/$DEVICE_ID -o /dev/null
echo "Delete DeviceType($DEVICE_TYPE)"
curl -sX DELETE http://$HOST:$PORT/types/devices/$DEVICE_TYPE -o /dev/null
