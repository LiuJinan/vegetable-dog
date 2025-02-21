# Vegetable-Dog


该项目是java工具包


## job 模块
基于elastic-job，提供spring注解方式创建任务
```java

/**
 * @see cn.liujinnan.vegetable.dog.job.annotation.JobAnnotation
 */
@Component
@JobAnnotation(jobName = "name", cron = "0/10 * * * * ?", shardingTotalCount = 1, description = "bbb")
public class Test2 implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        // run
    }
}

```

## utils 模块

```java

public static void main(String[] args) {
    try{
        // 生成图片. 确保服务器有宋体字体，否则乱码
        BufferedImage img = TableImageUtils.rotateImage("图片标题", allValue, headers);
        ImageIO.write(img, "jpg",new File("pic.jpg"));
    }catch(Exception e){
        //log
    }
}


```
