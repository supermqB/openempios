# openempios
## 本地环境
1. 使用cnhealth maven仓库
```
<settings>
  <mirrors>
    <mirror>
      <id>cnhealth</id>
      <name>cnhealth</name>
      <url>http://nexus.cnhealth.com/repository/maven-public/</url>
      <mirrorOf>*</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```
2. 确定jdk runtime是1.8
3. 运行mvn clean compile 
