/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model
 * @fileName : IrtResNJson.java
 * @author : q93m9k
 * @date : 2024.01.12
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.01.12       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.model;

import com.shinsegae.ssgdx.ldi.model.comirt.ComHeader;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.12
 * @see :
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IrtResNJson {

    @JsonProperty("COMM_HEADER")
    private ComHeader COMM_HEADER = new ComHeader(); // 통신 헤더

    @JsonProperty("IRT_RES")
    private Object IRT_RES; // IRT 응답 전문
}
