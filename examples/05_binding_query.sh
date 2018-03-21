#!/bin/bash

# Query API for Binding
# jq must be installed

HOST=localhost
PORT=8080
DEVICES_PER_USER=5

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

DEVICES=()
USERS=()

# create users (harold, root and john)
USERS+=($(curl -sX POST \
  http://$HOST:$PORT/users \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "machine1337",
	"email":"haroldfinch@poi.com",
	"password":"$2y$10$JcFKLS3oZ5T6VPzvvrHGde6fwouhiD/ghfH6DGO.6EXJcPjd7mNX."
}' | jq -r '.user_id'))
USERS+=($(curl -sX POST \
  http://$HOST:$PORT/users \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "root",
	"email":"samanthagroves@poi.com",
	"password":"$2y$10$sZPQ89ZwVCS1KC9.6WOuGeF8kyfH8j9w5ABNG7XNp3InzYCoTnqQ6"
}' | jq -r '.user_id'))
USERS+=($(curl -sX POST \
  http://$HOST:$PORT/users \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "maninthesuit",
	"email":"johnreese@poi.com",
	"password":"$2y$10$VNJ.PMQbtmya67S1o6uxn.VLa11dkS4NyXdir4/uu4KQXE3UFIk7q"
}' | jq -r '.user_id'))

# create devices
for (( i=0; i<${#USERS[@]}*$DEVICES_PER_USER; i++))
do
    # im lazy :(
    # DEVICE_PER_USER cannot be too big
    val="$(($i+16))"
    # generate
    mac=$(printf '%x%x%x%x%x%x' 85 90 110 77 23 $val)
    DEVICES+=($(curl -sX POST \
        http://$HOST:$PORT/devices \
        -H 'Cache-Control: no-cache' \
        -H 'Content-Type: application/json' \
        -d '{
            "type": '$DEVICE_TYPE',
            "name": "Machine Replica '$i'",
            "mac": "'$mac'",
            "pin_code": 1234
        }' | jq -r '.device_id'))
    echo "Device created ID = ${DEVICES[$i]}"
done

BINDINGS=()
idx=0
# create bindings for each user
for userId in "${USERS[@]}"
do
    for (( i=0; i<$DEVICES_PER_USER; i++ ))
    do
        # retrieve device ID
        deviceId=${DEVICES[$idx]}
        # create binding
        BINDINGS+=($(curl -sX POST \
            http://$HOST:$PORT/bindings \
            -H 'Cache-Control: no-cache' \
            -H 'Content-Type: application/json' \
            -d '{
                "user_id" : "'$userId'",
                "device_id":"'$deviceId'"
            }' | jq -r '.binding_id'))
        echo "Binding created ID = ${BINDINGS[$idx]}"
        ((idx++))
    done
    # 1 sec delay to separate the records
    sleep 1
done

#
# QUERY STARTS HERE
#
echo "====================="

URL=http://$HOST:$PORT/bindings

# filter by userId
curl -s "$URL?offset=0&limit=5&user_id=${USERS[0]}" | jq

# basic pagination (items per page is 5)
# offset must be multiple of limit
echo "====================="
echo "Page 1"
curl -s "$URL?offset=0&limit=5" | jq
echo "Page 2"
curl -s "$URL?offset=5&limit=5" | jq
echo "Page 3"
curl -s "$URL?offset=10&limit=5" | jq

# embedded device objects
#
# by default, only device id is in the response (no join)
# enable embedded device by adding entities=device query parameter
echo "====================="
echo "Embedded device object (json key: device)"
curl -s "$URL?offset=0&limit=5&user_id=${USERS[0]}&entities=device" | jq

echo "====================="

#
# QUERY ENDS HERE
#

# cleanup
# delete bindings
for bindingId in "${BINDINGS[@]}"
do
    curl -sX DELETE http://$HOST:$PORT/bindings/$bindingId -o /dev/null
done
# delete users
for userId in "${USERS[@]}"
do
    curl -sX DELETE http://$HOST:$PORT/users/$userId -o /dev/null
done
# delete devices
for deviceId in "${DEVICES[@]}"
do
    curl -sX DELETE http://$HOST:$PORT/devices/$deviceId -o /dev/null
done
# delete device type
echo "Delete DeviceType($DEVICE_TYPE)"
curl -sX DELETE http://$HOST:$PORT/types/devices/$DEVICE_TYPE -o /dev/null
