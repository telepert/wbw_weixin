#!/bin/bash
# 每次总是重启所有程序的脚本
# 在文件保存在父项目里面，扩展名为.sh
mvn install
cd weixin
mvn spring-boot:start
cd ../subscribe
mvn spring-boot:start
cd ../unsubscribe
mvn spring-boot:start

cd ../self-menu
mvn spring-boot:start