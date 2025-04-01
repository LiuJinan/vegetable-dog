package cn.liujinnan.vegetable.dog.job.factory;

import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobConfigurationAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.settings.JobConfigurationAPIImpl;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author ljn
 * @version 1.0
 * @date 2025-04-01 11:58
 */
@Component
public class JobFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    private static JobConfigurationAPI jobConfigurationApi;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JobFactory.applicationContext = applicationContext;

        CoordinatorRegistryCenter registryCenter = applicationContext.getBean(CoordinatorRegistryCenter.class);
        Map<String, CoordinatorRegistryCenter> map = applicationContext.getBeansOfType(CoordinatorRegistryCenter.class);

        jobConfigurationApi = new JobConfigurationAPIImpl(registryCenter);
    }

    public static JobConfigurationAPI configurationApi() {
        return jobConfigurationApi;
    }

}
