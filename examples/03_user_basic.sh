#/bin/bash

# CRUD for User
# jq must be installed

HOST=localhost
PORT=8080

# create a User with bcrpt hashed password (Password=grace)
# Unique constraint on name (should be username) & email
USER_ID=$(curl -sX POST \
  http://$HOST:$PORT/users \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "machine1337",
	"email":"haroldfinch@poi.com",
	"password":"$2y$10$JcFKLS3oZ5T6VPzvvrHGde6fwouhiD/ghfH6DGO.6EXJcPjd7mNX."
}' | jq -r '.user_id')

URL=http://$HOST:$PORT/users/$USER_ID

# retrieve the newly created User
echo "Newly created User($USER_ID):"
curl -s $URL | jq
echo "======================="

# try to create another User with the same username/email
# => 409
echo "Create another user with the same email:"
curl -X POST \
  http://$HOST:$PORT/users \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "decima",
	"email":"haroldfinch@poi.com",
	"password":"$2y$10$JcFKLS3oZ5T6VPzvvrHGde6fwouhiD/ghfH6DGO.6EXJcPjd7mNX."
}'
echo
echo "======================="

# full update
# (if not all required fields are present in body, this will fail) => 400
# if specified email/username already used => 409
curl -sX PUT \
  $URL \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "machine_v2",
	"email":"haroldfinch@poi.com",
	"password":"$2y$10$JcFKLS3oZ5T6VPzvvrHGde6fwouhiD/ghfH6DGO.6EXJcPjd7mNX."
}' -o /dev/null

# retrieve the updated User
# username changed
echo "Updated User($USER_ID):"
curl -s $URL | jq
echo "======================="

# partial update
curl -sX PATCH \
  $URL \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"name" : "machine_reborn"
}' -o /dev/null

# retrieve the updated User
# username changed
echo "Partially updated User($USER_ID):"
curl -s $URL | jq
echo "======================="

# delete
echo "Delete User($USER_ID)"
curl -sX DELETE $URL -o /dev/null

# try to retrieve the deleted User
# and fail (404)
echo "Retrieve deleted User($USER_ID):"
curl -s $URL
echo
