package cn.liujinnan.vegetable.dog.job.register;

import cn.liujinnan.vegetable.dog.job.annotation.JobAnnotation;
import org.apache.shardingsphere.elasticjob.annotation.ElasticJobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * 任务注册
 *
 * @author liujinan
 */
@Configuration
@ConditionalOnExpression("'${elasticjob.reg-center.server-lists}'.length() > 0 && '${elasticjob.reg-center.namespace}'.length()>0")
public class JobRegister {

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void registerJob() {
//        ElasticJobConfiguration
        ZookeeperRegistryCenter zookeeperRegistryCenter = applicationContext.getBean(ZookeeperRegistryCenter.class);
        Map<String, ElasticJob> map = applicationContext.getBeansOfType(ElasticJob.class);

        //数据源配置
        TracingConfiguration<?> tracingConfiguration = getTracingConfiguration();
        for (Map.Entry<String, ElasticJob> entry : map.entrySet()) {
            ElasticJob elasticJob = entry.getValue();
            if (AopUtils.isAopProxy(elasticJob)) {
                try {
                    elasticJob = (ElasticJob) ((Advised) elasticJob).getTargetSource().getTarget();
                } catch (Exception ignored) {
                }
            }
            if (Objects.isNull(elasticJob)) {
                continue;
            }

            JobAnnotation jobAnnotation = elasticJob.getClass().getAnnotation(JobAnnotation.class);
            if (Objects.isNull(jobAnnotation)) {
                continue;
            }

            String jobName = elasticJob.getClass().getSimpleName();
            //job任务配置
            JobConfiguration jobConfiguration = JobConfiguration.newBuilder(jobName, jobAnnotation.shardingTotalCount())
                    .cron(jobAnnotation.cron())
                    .description(jobAnnotation.description())
                    .shardingItemParameters(jobAnnotation.shardingItemParameters())
                    .disabled(jobAnnotation.disabled())
                    .overwrite(jobAnnotation.overwrite())
                    .monitorExecution(jobAnnotation.monitorExecution())
                    .jobErrorHandlerType(jobAnnotation.jobErrorHandlerType())
                    .failover(jobAnnotation.failover())
                    .misfire(jobAnnotation.misfire())
                    .build();
            if (Objects.nonNull(tracingConfiguration)) {
                // 数据源。存储执行记录
                jobConfiguration.getExtraConfigurations().add(tracingConfiguration);
            }
            // 创建任务
            ScheduleJobBootstrap scheduleJobBootstrap =
                    new ScheduleJobBootstrap(zookeeperRegistryCenter, elasticJob, jobConfiguration);
            scheduleJobBootstrap.schedule();
        }
    }

    private TracingConfiguration<?> getTracingConfiguration() {
        Map<String, TracingConfiguration> dataSourceConfig = applicationContext.getBeansOfType(TracingConfiguration.class);
        if (!CollectionUtils.isEmpty(dataSourceConfig) && dataSourceConfig.values().size() == 1) {
            return dataSourceConfig.values().stream().findFirst().get();
        }
        return null;
    }
}
