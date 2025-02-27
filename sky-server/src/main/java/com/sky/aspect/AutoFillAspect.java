package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，用于自动填充实体类属性
 */
@Aspect    // 声明这是一个切面类，用于AOP编程
@Component   // 将类声明为Spring容器管理的Bean
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}
        /**
         * 前置通知，在目标方法执行前执行
         */
        @Before("autoFillPointCut()")
        public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            log.info("执行自动填充实体类属性……");

            //获取拦截到的操作类型
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名
            AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取注解
            OperationType operationType = autoFill.value();//获取操作类型

            //获取拦截到的实体类
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0){
                return;
            }
            Object entity = args[0];

            //准备赋值的数据
            LocalDateTime now = LocalDateTime.now();
            Long currentId= BaseContext.getCurrentId();

            //为拦截到的实体类属性赋值
            if(operationType==OperationType.INSERT){
                try {
                //为4个基础属性赋值
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射调用方法赋值
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else if(operationType==OperationType.UPDATE){
                //为2个基础属性赋值
                try {
                    Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                    //通过反射调用方法赋值
                    setUpdateTime.invoke(entity, now);
                    setUpdateUser.invoke(entity, currentId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }




}
