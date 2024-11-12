package com.nju.software.xmltodb.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @Description 检测方法执行耗时的spring切面类
 * @Author wxy
 * @Date 2024/3/8
 **/
@Aspect
@Component
@Slf4j
public class TimeInterceptor {

    // service层的统计耗时切面，类型必须为final String类型的,注解里要使用的变量只能是静态常量类型的
    public static final String POINT = "execution (* com.nju.software.xmltodb.service.impl.*.*(..))";
    // 一秒钟，即60000ms
    private static final long ONE_SECOND = 1000;

    /**
     * 统计方法执行耗时Around环绕通知
     *
     * @param joinPoint 交点
     * @return 方法返回对象
     */
    @Around(POINT)
    public Object timeAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 定义返回对象、得到方法需要的参数
        Object obj;
        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();

        obj = joinPoint.proceed(args);

        // 获取执行的方法名
        long endTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

        // 打印耗时的信息
        this.printExecTime(methodName, startTime, endTime);

        return obj;
    }

    /**
     * 打印方法执行耗时的信息，如果超过了一定的时间，才打印
     *
     * @param methodName 方法名
     * @param startTime  开始时间
     * @param endTime    结束时间
     */
    private void printExecTime(String methodName, long startTime, long endTime) {
        long diffTime = endTime - startTime;
        if (diffTime > ONE_SECOND) {
            double time = (double) diffTime / ONE_SECOND;
            time = (double) Math.round(time * 1000) / 1000;
            log.info(methodName + " 方法执行耗时：" + time + " s");
        }
    }
}
