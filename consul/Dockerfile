FROM hashicorp/consul:1.20

# Копируем скрипт в контейнер
COPY ./bootstrap-consul.sh /bootstrap-consul.sh

# Копируем конфигурационный файл в контейнер
COPY ./consul.hcl /consul/config/consul.hcl

# Даем права на выполнение
RUN chmod +x /bootstrap-consul.sh

HEALTHCHECK --interval=5s --timeout=3s --start-period=5s --retries=5 \
  CMD ["curl", "-f", "http://localhost:8500/v1/status/leader"]

# Запускаем консул с вызовом скрипта
ENTRYPOINT ["sh", "bootstrap-consul.sh"]