package com.nokchax.test.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Load {
    String[] command() default "";
    String help() default "";
}
/*
    meta-annotation
    @Retention : 컴파일러가 어노테이션을 참조하는 시점
    RetentionPolicy.CLASS : 컴파일된 클래스까지 유효
    RetentionPolicy.SOURCE : 컴파일 전까지 유효 (컴파일 이후에 사라짐, 즉 소스에서만 유효)
    RetentionPolicy.RUNTIME : 컴파일 이후에도 JVM에서 참조 가능 (리플렉션을 이용해서 런타임 시에 어노테이션 정보를 얻을 수 있음)

    @Type : 어노테이션 적용 대상
    TYPE : 클래스, 인터페이스, 열거 타입
    ANNOTATION_TYEP : 어노테이션
    FIELD : 필드
    CONSTRUCTOR : 생성자
    METHOD : 메소드
    LOCAL_VARIABLE : 로컬변수
    PACKAGE : 패키지
 */
