package com.github.kevin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //该注解作用在方法之上
//@Retention(RetentionPolicy.RUNTIME) //JVM在运行时通过反射来获取注解
@Retention(RetentionPolicy.CLASS) //在源码和class文件中都存在，运行时不存在，编译期注解方式
public @interface OnShowRationable {




}
