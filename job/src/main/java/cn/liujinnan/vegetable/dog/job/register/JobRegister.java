package cn.liujinnan.vegetable.dog.job.register;

import cn.liujinnan.vegetable.dog.job.annotation.JobAnnotation;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
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

            String jobName = StringUtils.isBlank(jobAnnotation.jobName()) ? elasticJob.getClass().getSimpleName() : jobAnnotation.jobName();
            //job任务配置
            JobConfiguration jobConfiguration = JobConfiguration.newBuilder(jobName, jobAnnotation.shardingTotalCount())
                    .shardingItemParameters(jobAnnotation.shardingItemParameters())
                    .cron(Strings.isNullOrEmpty(jobAnnotation.cron()) ? null : jobAnnotation.cron())
                    .timeZone(Strings.isNullOrEmpty(jobAnnotation.timeZone()) ? null : jobAnnotation.timeZone())
                    .jobParameter(jobAnnotation.jobParameter())
                    .monitorExecution(jobAnnotation.monitorExecution())
                    .failover(jobAnnotation.failover())
                    .misfire(jobAnnotation.misfire())
                    .maxTimeDiffSeconds(jobAnnotation.maxTimeDiffSeconds())
                    .reconcileIntervalMinutes(jobAnnotation.reconcileIntervalMinutes())
                    .jobShardingStrategyType(Strings.isNullOrEmpty(jobAnnotation.jobShardingStrategyType()) ? null : jobAnnotation.jobShardingStrategyType())
                    .jobExecutorServiceHandlerType(Strings.isNullOrEmpty(jobAnnotation.jobExecutorServiceHandlerType()) ? null : jobAnnotation.jobExecutorServiceHandlerType())
                    .jobErrorHandlerType(Strings.isNullOrEmpty(jobAnnotation.jobErrorHandlerType()) ? null : jobAnnotation.jobErrorHandlerType())
                    .jobListenerTypes(jobAnnotation.jobListenerTypes())
                    .description(jobAnnotation.description())
                    .disabled(jobAnnotation.disabled())
                    .overwrite(jobAnnotation.overwrite())
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
