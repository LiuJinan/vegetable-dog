package cn.liujinnan.vegetable.dog.job.register;

import lombok.Setter;
import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;

/**
 * @author ljn
 * @version 1.0
 * @date 2024-12-16 18:33
 */
@Setter
@Configuration
public class TestConfig implements SmartInitializingSingleton, ApplicationContextAware {


    private ApplicationContext applicationContext;



    @Override
    public void afterSingletonsInstantiated() {

        ZookeeperRegistryCenter zookeeperRegistryCenter = applicationContext.getBean(ZookeeperRegistryCenter.class);

        Map<String, ElasticJob> map = applicationContext.getBeansOfType(ElasticJob.class);
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

            new ScheduleJobBootstrap(zookeeperRegistryCenter, elasticJob).schedule();

        }

    }

}
