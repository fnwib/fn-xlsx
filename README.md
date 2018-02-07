 # fn-xlxs 

简单易用可扩展的Excel读写工具类
支持 java 1.8 
## 1.  Include 
#### 1. 下载代码
#### 2. 打包 
```
mvn clean install 
```
#### 3. 添加本地依赖
```
<dependencies>
  <dependency>
    <groupId>com.github.fnwib</groupId>
    <artifactId>fn-xlsx</artifactId>
    <version>1.1.3-SNAPSHOT</version>
  </dependency>
</dependencies> 
```
## 2.  Excel to JavaBean 

#### 1. Excel 文档

| TextOne | integer | LocalDte 1 | LocalDte 2 | Map A | Map B |
| :-----: | :-----: | :--------: | :--------: | :---: | :---: |
|  text1  |    1    | 2018-02-07 | 2018/1/07  |  m11  |  m21  |
|  text2  |    2    | 2018-02-08 | 2018/1/08  |  m12  |  m22  |
|  text3  |    3    | 2018-02-09 | 2018/1/09  |  m13  |  m23  |

#### 2. 编写javaBean
```java
@ToString //lombok
@Getter  //lombok
@Setter  //lombok
public class AutoMappingModel {
    @AutoMapping("Text One")    
    private String                text1;
  
    @AutoMapping("integer")
    private Integer               intNum;
  
    @AutoMapping(prefix = "LocalDate", value = "\\d+")
    private List<LocalDate>       localDateList;
  
    @AutoMapping(prefix = "Map", value = "[A-Z]")
    private Map<Integer, String> stringKeyMap;
  
}
```

#### 3. 读取数据
```java
File file = new File("file");
Workbook workbook = new XSSFWorkbook(file);
```
数据量比较大推荐使用
[excel-streaming-reader](https://github.com/monitorjbl/excel-streaming-reader) 

*  简单使用
```java
LineReader<AutoMappingModel> parser = new LineReaderForExcel<>(AutoMappingModel.class);
Workbook workbook = null;
boolean matched = false;
for (Sheet sheet : workbook) {
    for (Row row : sheet) {
        if (matched) {
            Optional<AutoMappingModel> convert = parser.convert(row);
            if (convert.isPresent()) {
                AutoMappingModel autoMappingModel = convert.get();
                System.out.println(autoMappingModel);
            }
        } else {
            boolean match = parser.match(row);
            if (match) {
                matched = true;
            }
        }
    }
}
```
* 使用ExcelReader读取
```java
LineReader<AutoMappingModel> parser = new LineReaderForExcel<>(AutoMappingModel.class);
ExcelReader<AutoMappingModel> reader = new ExcelReaderImpl<>(parser, workbook, 0);
while (reader.hasNext()){
    List<AutoMappingModel> models = reader.fetchData(500);
    System.out.println(models.size());
}
```

### 4  数据预处理配置

####  预处理表头 (表头都按String处理)
```
com.github.fnwib.databing.valuehandler.ValueHandler trim = (s) -> s.trim();
LocalConfig localConfig = new LocalConfig();
localConfig.registerTitleValueHandlers(trim); // 注册表头字符串处理器
LineReader<AutoMappingModel> parser = new LineReaderForExcel<>(AutoMappingModel.class, localConfig);
```

####  预处理内容（只处理cell类型是文本）
  ##### 全局配置
```
com.github.fnwib.databing.valuehandler.ValueHandler trim = (s) -> s.trim(); 
LocalConfig localConfig = new LocalConfig();
localConfig.registerReadContentValueHandlers(trim);
LineReader<AutoMappingModel> parser = new LineReaderForExcel<>(AutoMappingModel.class, localConfig);
```
  #####  单个字段配置
###### 1. 实现一个处理器（小写转大写）

```java
public class ToUpperHandler implements ValueHandler {
    @Override
    public String convert(String param) {
        return param.toUpperCase();
    }
}
```
###### 2.添加注解@ReadValueHandler({ToUpperHandler.class})到指定字段
```java
@ToString //lombok
@Getter  //lombok
@Setter  //lombok
public class AutoMappingModel {
	@ReadValueHandler({ToUpperHandler.class})
    @AutoMapping("Text One")    
    private String                text1;
  
    @AutoMapping("integer")
    private Integer               intNum;
  
    @AutoMapping(prefix = "LocalDate", value = "\\d+")
    private List<LocalDate>       localDateList;
  
    @AutoMapping(prefix = "Map", value = "[A-Z]")
    private Map<Integer, String> stringKeyMap;
  
}
```

## 3.  JavaBean to Excel (待续)



