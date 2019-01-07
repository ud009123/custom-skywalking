源码参考
https://github.com/apache/incubator-skywalking


docker 构建

cd docker

make build

docker-compose up


agent 编译

cd apm-sniffer

mvn install -DskipTests -Drat.skip=true  -Dcheckstyle.skip=true


agent 监控包

check: apm-sniffer/apm-sdk-plugin/mysql-5.x-plugin/src/main/java/org/apache/skywalking/apm/plugin/jdbc/mysql/define/CustomYongda.java

apm-sniffer/apm-sdk-plugin/mysql-5.x-plugin/src/main/java/org/apache/skywalking/apm/plugin/jdbc/mysql/define/MultiClassNameContainsMatch.java
