package cn.liujinnan.vegetable.dog.job.handler;

import org.apache.shardingsphere.elasticjob.error.handler.JobErrorHandler;

import java.util.Collections;
import java.util.List;

/**
 * Job error handler properties configuration prefix
 * @author ljn
 * @version 1.0
 * @date 2025-05-06 16:57
 */
public interface JobPropErrorHandler extends JobErrorHandler {

    /**
     * 错误处理作业属性配置前缀
     * @return
     */
    default List<String> propsPrefixes(){
        return Collections.emptyList();
    }
}
