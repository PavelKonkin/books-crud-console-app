#!/bin/bash
# init-mongo.sh
echo "Starting MongoDB initialization"

mongosh -u "$MONGO_INITDB_ROOT_USERNAME" -p "$MONGO_INITDB_ROOT_PASSWORD" --authenticationDatabase admin <<EOF
use ${MONGODB_DB_NAME}
db.createUser({
  user: "${MONGODB_USER}",
  pwd: "${MONGODB_PASSWORD}",
  roles: [
    {
      role: "readWrite",
      db: "${MONGODB_DB_NAME}"
    }
  ]
})
EOF

echo "MongoDB initialization complete"