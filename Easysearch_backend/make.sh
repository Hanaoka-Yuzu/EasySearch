#!/bin/bash
source /etc/profile

pwd
cd ./common || true
mvn clean install

cd ..
mvn clean package

docker build ./gateway -t gateway
docker build ./download -t download
docker build ./search -t search
docker build ./thirdparty -t thirdparty
docker build ./user -t user
docker build ./xmltodb -t xmltodb
