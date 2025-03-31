package cn.liujinnan.vegetable.dog.job;


import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

import java.util.UUID;

/**
 * 任务抽象类。 继承该类，任务失败可发送通知。日志打印mdc记录
 *
 * @author liujinnan
 */
public abstract class AbstractSimpleJob implements SimpleJob {

    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 默认空字符串
     */
    @Value("${job.dingtalk.webhook:}")
    private String webhook;

    @Value("${vegetable.dog.job.mdc.key:TRACE_ID}")
    private String mdcKey;

    @Override
    public void execute(ShardingContext shardingContext) {
        MDC.put(mdcKey, UUID.randomUUID().toString());
        StopWatch stopWatch = new StopWatch();
        String jobName = this.getClass().getSimpleName();
        try {
            stopWatch.start();
            log.info("任务[{}]开始执行", jobName);
            executeJob(shardingContext);
            stopWatch.stop();
            log.info("任务[{}]执行完成, 耗时={}ms", jobName, stopWatch.getTotalTimeMillis());
        } finally {
            MDC.remove(mdcKey);
        }
    }

    /**
     * 子类执行的任务
     *
     * @param shardingContext
     */
    protected abstract void executeJob(ShardingContext shardingContext);

    /**
     * 默认发送预警消息
     *
     * @return
     */
    @Deprecated
    protected boolean isAlarm() {
        return true;
    }

}
