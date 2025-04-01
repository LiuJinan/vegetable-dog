# Vegetable-Dog


该项目是java工具包


## job 模块
基于elastic-job，提供spring注解方式创建任务

### 1. 快速开始

#### 引入maven

```xml
<dependency>
    <groupId>cn.liujinnan.vegetable.dog</groupId>
    <artifactId>job</artifactId>
    <version>${version}</version>
</dependency>
```
#### 创建任务
```java
/**
 * @see cn.liujinnan.vegetable.dog.job.annotation.JobAnnotation
 */
@Component
@JobAnnotation(jobName = "name", cron = "0/10 * * * * ?", shardingTotalCount = 1, description = "bbb", shardingTotalCount = 3, shardingItemParameters = "0=a,1=b,3=c")
public class Test2 implements AbstractSimpleJob {
    @Override
    public void executeJob(ShardingContext shardingContext) {
        switch (shardingContext.getShardingItem()) {
            case 0:
                // do something by sharding item 0
                //StringUtils.equals(shardingContext.getShardingParameter(), "a") == true
                break;
            case 1:
                // do something by sharding item 1
                //StringUtils.equals(shardingContext.getShardingParameter(), "b") == true
                break;
            case 2:
                // do something by sharding item 2
                //StringUtils.equals(shardingContext.getShardingParameter(), "b") == true
                break;
            // case n: ...
        }
    }
}

```
#### 配置
```properties
# 必要配置
# 多个逗号分隔
elasticjob.reg-center.server-lists=host:2181
elasticjob.reg-center.namespace=namespace
```

### 2. 错误处理

```java
// 新增错误处理类。jobErrorHandlerType="DT", 发送钉钉预警
@JobAnnotation(jobErrorHandlerType="DT")

// 需要配置钉钉url
// elasticjob.jobs.props.dingtalk.webhook=https://oapi.dingtalk.com/robot/send?access_token=xxx

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
