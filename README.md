# mybatis-generator-lombok

mybatis生成实体类简化为lombok形式

## 使用方法

- 仓库配置参考 https://jitpack.io

- 添加pom配置

```xml

<plugin>
  <groupId>org.mybatis.generator</groupId>
  <artifactId>mybatis-generator-maven-plugin</artifactId>
  <!-- 必须1.4.0 以上 -->
  <version>1.4.0</version>
  <configuration>
    <configurationFile>generatorConfig.xml</configurationFile>
    <overwrite>true</overwrite>
    <verbose>true</verbose>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.24</version>
    </dependency>
    <!--    添加插件依赖 -->
    <dependency>
      <groupId>com.github.shenluw</groupId>
      <artifactId>mybatis-generator-lombok</artifactId>
      <version>0.0.1</version>
    </dependency>
  </dependencies>
</plugin>
```

- generatorConfig.xml 配置plugin

```xml
<!-- Lombok插件 -->
<plugin type="top.shenluw.mybatis.generator.LombokPlugin">
  <!-- 按需配置需要注解 -->
  <property name="@Data" value="true"/>
  <property name="@Builder" value="true"/>
  <property name="@AllArgsConstructor" value="true"/>
  <property name="@NoArgsConstructor" value="true"/>
  <property name="@Accessors(chain = true)" value="true"/>
</plugin>
```