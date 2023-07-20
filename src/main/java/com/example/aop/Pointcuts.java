package com.example.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.example.controller.*.*(..))")
    public void allEndpoints() {}

    @Pointcut("execution(* com.example.service.*.*(..))")
    public void allServiceMethods() {}

//    @Pointcut("execution(* com.example.model.*.*(..))") // does not find methods
//    public void allModelMethods() {}

    @Pointcut("execution(* com.example.utils.*.*.*(..))")
    public void allUtilMethods() {}
}
