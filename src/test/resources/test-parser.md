# h1

content

## code

```java
/**
 * Created by Beancan on 2016/10/26.
 */
public class MarkdownJ {
    public static String parseFile(String fileName) throws Exception {
        return Parser.parseFile(fileName);
    }

    public static String parseFile(File file) throws Exception {
        return Parser.parseFile(file);
    }

    public static String parseString(String content) throws Exception {
        return Parser.parseString(content);
    }
}
```

## Table

|a|a
|---| ---
|b|2

### List

 - a1
 - a2
 
   - b1
   - b2
 - a3


### dict

[A]: [bbbbb]
[B]: [bbbbb]
[C]: [bbbbb]