#
# Copyright (C) 2018-2020 toop.eu
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

FROM tomcat:8-jre8


ARG SIMULATOR_JAR_NAME=toop-simulator-0.10.7-SNAPSHOT-bundle.jar

#create tc webapp folder
WORKDIR /simulator

ENV JAVA_OPTS="$JAVA_OPTS -Dtoop.connector.server.properties.path=/toop-dir/tc/config/toop-connector.properties -Djava.security.egd=file:/dev/urandom" \
    SIMULATOR_JAR_NAME="${SIMULATOR_JAR_NAME}" \
    DC_URL="http://localhost:8081/to-dc" \
    DP_URL="http://localhost:8082/to-dp" \
    SIM_PORT=8080

ADD ./target/${SIMULATOR_JAR_NAME} ./

#run connector setup
CMD ["sh", "-c", "java $JAVA_OPTS -jar ${SIMULATOR_JAR_NAME}"]
