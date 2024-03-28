/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model
 * @fileName : IIrtFldMeta.java
 * @author : q93m9k
 * @date : 2024.01.08
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.01.08       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.shinsegae.ssgdx.ldi.util.IEnumComIrt.EnmIrtFieldType;
import com.shinsegae.ssgdx.ldi.util.IEnumComIrt.EnmTranFieldType;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface IIrtFldMeta {

    int size() default 0; // 전문 사이즈

    EnmIrtFieldType fldType() default EnmIrtFieldType.LSTR; // 전문 타입

    int loopCount() default 1; // 전문 반복 횟수 (반복일 경우)

    /*
     * *************************************************************************
     * 거래 전문 변환 처리 관련 정의
     * *************************************************************************
     */
    EnmTranFieldType dType() default EnmTranFieldType.ASC_CHAR;

    boolean isItemHeader() default false;

    boolean isLoopCount() default false;
}
