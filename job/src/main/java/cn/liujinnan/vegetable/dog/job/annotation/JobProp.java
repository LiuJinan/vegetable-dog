package cn.liujinnan.vegetable.dog.job.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation that specify elastic-job prop.
 * @author ljn
 * @version 1.0
 * @date 2025-05-06 16:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface JobProp {

    /**
     * Prop key.
     *
     * @return key
     */
    String key();

    /**
     * Prop value.
     *
     * @return value
     */
    String value() default "";
}
