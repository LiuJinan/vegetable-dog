package cn.liujinnan.vegetable.dog.job.annotation;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * job annotation
 *
 * @author liujinnan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface JobAnnotation {

    /**
     * The default value is the class name
     * 默认类名
     * @return
     */
    String jobName() default "";

    /**
     * Cron expression.
     * cron表达式
     * @return
     */
    String cron();

    String timeZone() default "";


    /**
     * Job parameter.
     * 作业自定义参数
     *
     * @return
     */
    String jobParameter() default "";

    /**
     * Job description.
     * 任务描述
     *
     * @return
     */
    String description() default "";

    /**
     * Sharding total count.
     * Returns: sharding total count
     * <p>
     * 默认分片数量 1
     *
     * @return
     */
    int shardingTotalCount() default 1;

    /**
     * Set mapper of sharding items and sharding parameters.
     * Sharding item start from zero, cannot equal to great than sharding total count. For example: 0=a,1=b,2=c
     * 下标从0开始
     * 分片样例："0=a,1=b,2=c"
     *
     * @return
     */
    String shardingItemParameters() default "";

    /**
     * Set job error handler type.
     * 任务失败预警通知
     * @return
     */
    String jobErrorHandlerType() default "";

    /**
     * Set whether disable job when start
     * 启动后是否禁用任务
     * @return
     */
    boolean disabled() default false;

    /**
     * Set whether overwrite local configuration to registry center when job startup.
     * 作业启动时是否覆盖本地配置到注册中心
     * overwrite=true, 以代码中的任务配置为准，每次重启都会重写注册中心
     * overwrite=false, 以注册中心的任务配置为准，即代码中调整cron等项不生效，任务以注册中心中的cron执行
     *
     * @return
     */
    boolean overwrite() default false;

    boolean monitorExecution() default false;

    /**
     * 失效转移
     *
     * @return
     */
    boolean failover() default false;

    /**
     * 是否开启错过任务重新执行
     *
     * @return
     */
    boolean misfire() default false;


    /**
     * The maximum value for time difference between server and registry center in seconds.
     *
     * @return max time diff seconds
     */
    int maxTimeDiffSeconds() default -1;

    /**
     * Service scheduling interval in minutes for repairing job server inconsistent state.
     *
     * @return reconcile interval minutes
     */
    int reconcileIntervalMinutes() default 10;

    /**
     * Job sharding strategy type.
     *
     * @return job sharding strategy type
     */
    String jobShardingStrategyType() default "";

    /**
     * Job thread pool handler type.
     *
     * @return job executor service handler type
     */
    String jobExecutorServiceHandlerType() default "";

    /**
     * Job listener types.
     *
     * @return job listener types
     */
    String[] jobListenerTypes() default {};
}
