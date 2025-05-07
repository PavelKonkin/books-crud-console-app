#!/bin/bash

# Запуск Consul в фоне
consul agent -server -bootstrap-expect=1 -ui -client=0.0.0.0 -config-file=/consul/config/consul.hcl -dev &

# Функция проверки готовности Consul с использованием healthcheck
check_consul_health() {
  echo "Checking if Consul is ready..."
  while true; do
    status=$(curl --silent --output /dev/null --write-out "%{http_code}" http://localhost:8500/v1/status/leader)
    if [ "$status" -eq 200 ]; then
      echo "Consul is ready!"
      break
    fi
    echo "Consul is not ready yet. Waiting..."
    sleep 2
  done
}

# Проверка готовности Consul
check_consul_health

# Инициализация ACL с использованием bootstrap токена
echo "Bootstrapping ACL..."
bootstrap_token=$(curl --request PUT http://localhost:8500/v1/acl/bootstrap | jq -r '.SecretID')

echo "Bootstrap token created: $bootstrap_token"

# Создание политики для сервисов
curl --header "X-Consul-Token: $bootstrap_token" \
     --request PUT \
     --data '{"Name": "service-policy", "Rules": "node_prefix \"\" { policy = \"read\" } service_prefix \"\" { policy = \"write\" }"}' \
     http://localhost:8500/v1/acl/policy

# Создание токена для сервисов
service_token=$(curl --header "X-Consul-Token: $bootstrap_token" \
     --request PUT \
     --data '{"Description": "Service Token", "Policies": [{"Name": "service-policy"}]}' \
     http://localhost:8500/v1/acl/token | jq -r '.SecretID')

echo "Service token created: $service_token"
# Удаление файла, если он существует

#echo "CONSUL_HTTP_TOKEN=$service_token" > /consul/data/env_file
echo "$service_token" > /consul/data/service_token.txt

# Экспорт токена в переменную окружения для передачи другим контейнерам
#export CONSUL_SERVICE_TOKEN=$service_token

wait