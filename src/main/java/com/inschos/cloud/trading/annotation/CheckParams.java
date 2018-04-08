package com.inschos.cloud.trading.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.inschos.cloud.trading.annotation.CheckParams.StringType.STRING;

/**
 * 创建日期：2018/4/3 on 11:20
 * 描述：
 * 作者：zhangyunhe
 */
@Target(ElementType.FIELD)  //这个标识注解应该标在那里   ElementType的几个枚举值就代表了  注解应该写在的位置
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckParams {

    /**
     * 仅限引用类型是否必须，校验是否存在指定参数，默认为：true
     *
     * @return 是否必须
     */
    boolean isNecessity() default true;

    /**
     * 仅限String型参数最大长度检测，默认为-1，默认时不做检测，该值应该在[0,Integer.MAX_VALUE)区间
     *
     * @return 最大长度
     */
    int maxLen() default Integer.MAX_VALUE;

    /**
     * 仅限String型参数最小长度检测，默认为-1，默认时不做检测，该值应该在[0,Integer.MAX_VALUE)区间
     *
     * @return 最小长度
     */
    int minLen() default 0;

    /**
     * 仅限数字最大值检测，默认为{@link Double#MAX_VALUE}，默认时不做检测
     *
     * @return 最大值
     */
    double max() default Double.MAX_VALUE;

    /**
     * 仅限数字最小值检测，默认为{@link Double#MIN_VALUE}，默认时不做检测
     *
     * @return 最小值
     */
    double min() default Double.MIN_VALUE;

    /**
     * 仅针对String型参数，标注该String型为数字或者字符串
     * 当字符串为StringType.STRING型时，只做长度校验
     * 当字符串为StringType.NUMBER型时，只做数字与值大小
     *
     * @return 参数类型
     */
    StringType stringType() default STRING;

    /**
     * String型参数的类型枚举
     */
    enum StringType {
        // String型
        STRING,
        // 数字型
        NUMBER
    }

}
