/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.comirt
 * @fileName : ComHeader.java
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
package com.shinsegae.ssgdx.ldi.model.comirt;

import com.shinsegae.ssgdx.ldi.util.IIrtFldMeta;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Getter
@Setter
public class ComHeader {

    @IIrtFldMeta(size = 6)
    @JsonProperty("MsgLength")
    private String MsgLength; // 메시지길이

    @IIrtFldMeta(size = 2)
    @JsonProperty("MsgPath")
    private String MsgPath; // 메시지경로

    @IIrtFldMeta(size = 2)
    @JsonProperty("MsgType")
    private String MsgType; // 메시지타입

    @IIrtFldMeta(size = 2)
    @JsonProperty("MsgKind")
    private String MsgKind; // 메시지종류

    @IIrtFldMeta(size = 8)
    @JsonProperty("SaleDate")
    private String SaleDate; // 영업일자

    @IIrtFldMeta(size = 3)
    @JsonProperty("StoreNo")
    private String StoreNo; // 점코드

    @IIrtFldMeta(size = 4)
    @JsonProperty("PosNo")
    private String PosNo; // POS번호

    @IIrtFldMeta(size = 4)
    @JsonProperty("TranNo")
    private String TranNo; // 거래번호

    @IIrtFldMeta(size = 8)
    @JsonProperty("SendDate")
    private String SendDate; // 시스템날짜

    @IIrtFldMeta(size = 6)
    @JsonProperty("SendTime")
    private String SendTime; // 시스템시간

    @IIrtFldMeta(size = 4)
    @JsonProperty("ExtStoreNo")
    private String ExtStoreNo; // 新점포코드

    @IIrtFldMeta(size = 1)
    @JsonProperty("TrainMode")
    private String TrainMode; // 교육모드

    @IIrtFldMeta(size = 1)
    @JsonProperty("VersionFlag")
    private String VersionFlag; // 신/구전문구분

    @IIrtFldMeta(size = 12)
    @JsonProperty("UserInfo")
    private String UserInfo; // 사용자 정보

    @IIrtFldMeta(size = 4)
    @JsonProperty("ReplyCode")
    private String ReplyCode; // 응답코드
}
