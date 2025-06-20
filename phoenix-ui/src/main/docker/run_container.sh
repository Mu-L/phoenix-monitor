#!/usr/bin/env bash
# 出错立即退出
set -e
# 宿主机目录
HOST_DATA_DIR="/data/phoenix/phoenix-ui"
# 创建目录（如果不存在）
mkdir -p "${HOST_DATA_DIR}/liblog4phoenix" "${HOST_DATA_DIR}/config"
#赋予读写权限
chmod -R o+rw "${HOST_DATA_DIR}"
# 删除已存在的容器（如果有的话）
if [ "$(docker ps -a -f "name=phoenix-ui" --format "{{.Status}}")" ]; then
  echo "Removing existing container 'phoenix-ui'..."
  docker rm -f phoenix-ui
fi
# 启动容器
echo "Starting phoenix-ui container..."
docker run -itd \
  -v /etc/localtime:/etc/localtime:ro \
  -v "${HOST_DATA_DIR}/liblog4phoenix:/app/liblog4phoenix" \
  -v "${HOST_DATA_DIR}/config:/app/config" \
  -p 80:80 \
  --net host \
  --cap-add=NET_BIND_SERVICE \
  --name phoenix-ui \
  phoenix/phoenix-ui:1.2.6.RELEASE-CR3
# 启动容器成功
echo "Container started successfully."