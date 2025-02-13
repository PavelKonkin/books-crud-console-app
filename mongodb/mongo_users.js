print("Creating application user...");

db = db.getSiblingDB('${MONGODB_DB_NAME}');
db.createUser({
    user: "${MONGODB_USER}",
    pwd: "${MONGODB_PASSWORD}",
    roles: [
        {
            role: "readWrite",
            db: "${MONGODB_DB_NAME}"
        }
    ]
});