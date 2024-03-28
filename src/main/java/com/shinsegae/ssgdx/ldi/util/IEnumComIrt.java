/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model
 * @fileName : IEnumComIrt.java
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

import java.util.Arrays;

import lombok.Getter;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
public interface IEnumComIrt {

    public static final int DEF_ENC_HEADER_LEN = 21;

    public static final int DEF_COM_HEADER_LEN = 67;

    public static final int DEF_DMS_COM_HEADER_LEN = 200;

    public static final int DEF_HEADER_LEN_SIZE = 6;

    public static final int DEF_ITEM_HD_LEN = 6;

    public static final int DEF_IRT_TYPE_LEN = 2;

    public static final String DEF_COM_TYPE_REQ = "REQ";

    public static final String DEF_COM_TYPE_RES = "RES";

    public static final String REGR_ID = "q93m9k";

    public static final String UPDR_ID = "q93m9k";

    /**
     * 거래 송신 IRT 응답코드
     */
    @Getter
    public enum EnmIrtTranResCode {

        C000("000", "정상"), C101("101", "네트워크 접속 에러"), C102("102", "송신 실패"), C103("103", "수신 실패"),
        C201("201", "DB 접속에러"), C202("202", "APPL BUSY"), C203("203", "SQL ERROR"), C210("210", "APPL 예외 에러"),
        C301("301", "점코드 오류"), C302("302", "영업일 오류"), C303("303", "메시지 중복"), C304("304", "메시지 길이 이상"),
        C305("305", "전문오류");

        private String resCode;

        private String resMsg;

        EnmIrtTranResCode(String resCode, String resMsg) {
            this.resCode = resCode;
            this.resMsg = resMsg;
        }
    }

    /**
     * 거래 송신 IRT 응답코드
     */
    @Getter
    public enum EnmIrtDmsResCode {

        C0000("0000", "정상 처리"), C9999("9999", "조회내용이 없습니다."), C9888("9888", "DMS 서버에 오류가 발생하였습니다."),
        C1100("1100", "DMS DB오류가 발생하였습니다. - 재시도요망"), C1200("1200", "DMS 전문오류 입니다. - 재시도요망"),
        C1300("1300", "DMS 요청전문 복호화 오류 입니다."), C1400("1400", "DMS 응답전문 암호화 오류 입니다."),
        C1500("1500", "응답전문 최대사이즈를 초과하였습니다."), C1600("1600", "에누리행사 대상MD 마스터 최대개수를 초과하였습니다."),
        C2000("2000", "쿠폰/금액할인권 발급에 실패하였습니다."), C3000("3000", "트란 수집 중복입니다.");

        private String resCode;

        private String resMsg;

        EnmIrtDmsResCode(String resCode, String resMsg) {
            this.resCode = resCode;
            this.resMsg = resMsg;
        }
    }

    /**
     * IRT 전문 데이터 타입
     */
    @Getter
    public enum EnmIrtFieldType {

        LSTR("LS"), LINT("LI"), RSTR("RS"), RINT("RI"), LIST("list"), CLASS("class"), OBJECT("object");

        private String type;

        EnmIrtFieldType(String type) {
            this.type = type;
        }
    }

    /**
     * IRT 전문 타입(상수)
     */
    @Getter
    public enum EnmIrtType {
        // DMS IRT
        Irt_10, Irt_12, Irt_14, Irt_15, Irt_16,

        // 프로모션 IRT
        Irt_96, Irt_97, Irt_98,

        // 매출 관련 IRT
        Irt_56, Irt_57, Irt_74, Irt_78, Irt_81, Irt_82, Irt_83, Irt_89, Irt_90_22, Irt_90_23,

        // UnKnown
        Irt_UnKnown;
    }

    /**
     * IRT 전문 타입(랩핑)
     */
    @Getter
    public enum EnmIrtTypeWrap {

        // DMS IRT
        Irt_10(EnmIrtType.Irt_10, "10", "10", "DMS 수기 에누리 권한 조회", "", 0, 0),
        Irt_16(EnmIrtType.Irt_16, "16", "16", "DMS 고객 대상행사 및 쿠폰조회", "", 0, 0),
        Irt_12(EnmIrtType.Irt_12, "12", "12", "DMS 에누리 행사 정보조회", "", 0, 0),
        Irt_15(EnmIrtType.Irt_15, "15", "14", "DMS 쿠폰 및 금액할인권 발급 예정 조회 8BIN", "", 0, 0),
        Irt_14(EnmIrtType.Irt_14, "14", "15", "DMS 쿠폰 및 금액할인권 사용 확정 조회", "", 0, 0),

        // 프로모션 IRT
        Irt_96(EnmIrtType.Irt_96, "96", "30", "사은품 내역조회", "", 0, 0),
        Irt_97(EnmIrtType.Irt_97, "97", "31", "대체 영수증 조회", "", 0, 0),
        Irt_98(EnmIrtType.Irt_98, "98", "32", "대체영수증 가능 목록 조회", "", 0, 0),

        // 매출 관련 IRT
        Irt_56(EnmIrtType.Irt_56, "56", "10", "상품권샵 PM지급 영수증 조회", "", 0, 0),
        Irt_57(EnmIrtType.Irt_57, "57", "11", "상품권샵 PM지급 인수증 개인정보 입력", "", 0, 0),
        Irt_74(EnmIrtType.Irt_74, "74", "20", "TRAN호출 조회", "", 0, 0),
        Irt_78(EnmIrtType.Irt_78, "78", "60", "전일자 구매이력 조회", "", 0, 0),
        Irt_81(EnmIrtType.Irt_81, "81", "30", "캐시터미널 E-Accounting 결재 조회", "", 0, 0),
        Irt_82(EnmIrtType.Irt_82, "82", "41", "사용매출 특판 전표 상세 조회", "", 0, 0),
        Irt_83(EnmIrtType.Irt_83, "83", "40", "사용매출 특판 전표 목록 조회", "", 0, 0),
        Irt_89(EnmIrtType.Irt_89, "89", "21", "원거래 배송상태 조회", "", 0, 0),
        Irt_90_22(EnmIrtType.Irt_90_22, "90", "22", "내사인수 조회", "", 0, 0),
        Irt_90_23(EnmIrtType.Irt_90_23, "90", "23", "내사인수 승인", "", 0, 0),

        // UnKnown
        Irt_UnKnown(EnmIrtType.Irt_UnKnown, "!!", "!!", "UnKnown", "UnKnown", 0, 0);

        // IRT 전문 타입(상수)
        private EnmIrtType irtType;

        // IRT 전문 타입(구)
        private String irtTypeO;

        // IRT 전문 타입(신)
        private String irtTypeN;

        // IRT 전문 타입 설명
        private String irtTypeDesc;

        // IRT 전문 타입 접속 Url
        private String irtTypeUrl;

        // IRT 전문 타입(구 길이)
        private int irtTypeLenO;

        // IRT 전문 타입(신 길이)
        private int irtTypeLenN;

        EnmIrtTypeWrap(EnmIrtType irtType, String irtType_O, String irtType_N, String irtTypeDesc, String irtTypeUrl,
                int irtTypeLen_O, int irtTypeLen_N) {
            this.irtType = irtType;
            this.irtTypeO = irtType_O;
            this.irtTypeN = irtType_N;
            this.irtTypeDesc = irtTypeDesc;
            this.irtTypeUrl = irtTypeUrl;
            this.irtTypeLenO = irtTypeLen_O;
            this.irtTypeLenN = irtTypeLen_N;
        }

        public static EnmIrtTypeWrap findIrtTypeWithIrtType(String sIrtType){
            return Arrays.stream(EnmIrtTypeWrap.values())
                    .filter(iType -> (iType.irtTypeO.equals(sIrtType) || iType.irtTypeN.equals(sIrtType))).findAny()
                    .orElse(Irt_UnKnown);
        }
    }

    /*
     * *************************************************************************
     * 거래 전문 변환 처리 관련 정의
     * *************************************************************************
     */
    /**
     * 전문 업데이트 플래그 값
     */
    @Getter
    public enum EnmTranProcFlag {

        TR_PROC_READY("0"), TR_PROC_SUCC("1"), TR_PROC_ERR("2"), TR_PROC_NONE("9");

        private String fgVal;

        EnmTranProcFlag(String fgVal) {
            this.fgVal = fgVal;
        }
    }

    /**
     * 트란 전문 데이터 타입
     */
    @Getter
    public enum EnmTranFieldType {

        ASC_CHAR("X"), ASC_NUM("9"), LIST("list"), CLASS("class");

        private String type;

        EnmTranFieldType(String type) {
            this.type = type;
        }
    }

    /**
     * 트란 아이템 코드(상수)
     */
    @Getter
    public enum EnmTranItemCode {
        Tran_CashBillItem, Tran_GiftItem, Tran_CardItem, Tran_GIFTCardItem, Tran_CurrencyItem, Tran_EMPItem,
        Tran_CashICItem, Tran_EventCancelItem, Tran_SSGPayItem, Tran_WeChatPayItem, Tran_TaxRefundItem,
        Tran_GiftScratchItem, Tran_GiftSaleItem, Tran_GiftPmItem, Tran_ReadyAmtItem, Tran_GiftKioskOrgTranItem,
        Tran_MidAmtItem, Tran_CashTerCommItem, Tran_CashTerErrorItem, Tran_CashTerStartItem, Tran_CashTerCashinItem,
        Tran_CashTerGiftinItem, Tran_CashTerCashItem, Tran_CashTerCashrecvItem, Tran_CashTerGiftrecvItem,
        Tran_CashTerGiftSeqItem, Tran_CashTerInOutOrgItem, Tran_ScoScaslupCntItem, Tran_ChicorOnOffItem,
        Tran_ExchangeItem, Tran_PointItem, Tran_VipMileageItem, Tran_SSGConItem, Tran_MobileMmsItem,
        Tran_ShinBaekRewardItem, Tran_ShinBaekRewardCouponItem, Tran_GiftKioskCommItem, Tran_GiftKioskErrorItem,
        Tran_GiftKioskStartItem, Tran_GiftKioskInOutItem, Tran_GiftKioskCalcItem, Tran_GiftKioskGiftSeqItem,
        Tran_CalcItem, Tran_CashItem, Tran_DepositItem, Tran_Header, Tran_GoodsItem, Tran_CCFreeSetItem,
        Tran_CashTerExpenItem, Tran_CashTerAmtItem_DX, Tran_DmsItem, Tran_BmpCampaignItem, Tran_CardIcItem,
        Tran_KakaopayItem, Tran_EasyPaymentItem, Tran_SupyoItem, Tran_EtcPayItem, Tran_UseSaleSpecialItem,
        Tran_PreReservSaleItem, Tran_ShinBaekMoneyItem, Tran_RecommanderItem, Tran_MileageAutoSaveItem,
        Tran_UntactAccountItem, Tran_UntactCardItem, Tran_MidPdaAmtItem, Tran_ChicorClubItem, Tran_CashTerUseSaleItem,
        Tran_SetItem, Tran_PaycoinItem, Tran_UnKnown;
    }

    /**
     * 트란 아이템 코드(랩핑)
     */
    @Getter
    public enum EnmTranItemCodeWrap {

        Tran_CashBillItem(EnmTranItemCode.Tran_CashBillItem, "03", "03", "현금영수증 아이템", 0, 0, 0),
        Tran_GiftItem(EnmTranItemCode.Tran_GiftItem, "12", "12", "상품권 아이템", 0, 0, 0),
        Tran_CardItem(EnmTranItemCode.Tran_CardItem, "14", "14", "신용카드(MSR) 아이템", 0, 0, 0),
        Tran_GIFTCardItem(EnmTranItemCode.Tran_GIFTCardItem, "18", "18", "전자상품권 아이템", 0, 0, 0),
        Tran_CurrencyItem(EnmTranItemCode.Tran_CurrencyItem, "19", "19", "외화 아이템", 0, 0, 0),
        Tran_EMPItem(EnmTranItemCode.Tran_EMPItem, "21", "21", "사원인증 아이템", 0, 0, 0),
        Tran_CashICItem(EnmTranItemCode.Tran_CashICItem, "22", "22", "현금IC카드 아이템", 0, 0, 0),
        Tran_EventCancelItem(EnmTranItemCode.Tran_EventCancelItem, "23", "23", "사은품취소 아이템", 0, 0, 0),
        Tran_SSGPayItem(EnmTranItemCode.Tran_SSGPayItem, "24", "24", "통합플랫폼(SSGPAY) 아이템", 0, 0, 0),
        Tran_WeChatPayItem(EnmTranItemCode.Tran_WeChatPayItem, "25", "25", "위챗페이 거래 ITEM", 0, 0, 0),
        Tran_TaxRefundItem(EnmTranItemCode.Tran_TaxRefundItem, "26", "26", "즉시환급 거래 ITEM", 0, 0, 0),
        Tran_GiftScratchItem(EnmTranItemCode.Tran_GiftScratchItem, "27", "27", "스크래치상품권 아이템", 0, 0, 0),
        Tran_GiftSaleItem(EnmTranItemCode.Tran_GiftSaleItem, "28", "28", "상품권샵 상품권 판매/회수 ITEM", 0, 0, 0),
        Tran_GiftPmItem(EnmTranItemCode.Tran_GiftPmItem, "29", "29", "상품권샵 PM 아이템", 121, 121, 90),
        Tran_ReadyAmtItem(EnmTranItemCode.Tran_ReadyAmtItem, "30", "30", "준비금 아이템", 0, 0, 0),
        Tran_GiftKioskOrgTranItem(EnmTranItemCode.Tran_GiftKioskOrgTranItem, "31", "31", "상품권 키오스크 강제지급 원거래 ITEM", 0, 0,
                0),
        Tran_MidAmtItem(EnmTranItemCode.Tran_MidAmtItem, "40", "40", "중간입금 아이템", 0, 0, 0),
        Tran_CashTerCommItem(EnmTranItemCode.Tran_CashTerCommItem, "41", "41", "캐쉬터미널 공통 정보", 0, 0, 0),
        Tran_CashTerErrorItem(EnmTranItemCode.Tran_CashTerErrorItem, "42", "42", "캐쉬터미널 장애상태 정보", 48, 48, 20),
        Tran_CashTerStartItem(EnmTranItemCode.Tran_CashTerStartItem, "43", "43", "캐쉬터미널 개국 아이템", 0, 0, 0),
        Tran_CashTerCashinItem(EnmTranItemCode.Tran_CashTerCashinItem, "44", "44", "캐쉬터미널 시재장입/회수 아이템", 0, 0, 0),
        Tran_CashTerGiftinItem(EnmTranItemCode.Tran_CashTerGiftinItem, "45", "45", "캐쉬터미널 시재회수-상품권 아이템", 20, 20, 0),
        Tran_CashTerCashItem(EnmTranItemCode.Tran_CashTerCashItem, "46", "46", "캐쉬터미널 정산기 입출금 시재 아이템", 0, 0, 0),
        Tran_CashTerCashrecvItem(EnmTranItemCode.Tran_CashTerCashrecvItem, "47", "47", "캐쉬터미널 정산기 입금 아이템", 0, 0, 0),
        Tran_CashTerGiftrecvItem(EnmTranItemCode.Tran_CashTerGiftrecvItem, "48", "C2", "캐쉬터미널 상품권 입금 아이템", 74, 74, 0),
        Tran_CashTerGiftSeqItem(EnmTranItemCode.Tran_CashTerGiftSeqItem, "49", "49", "캐쉬터미널 상품권 일련번호 아이템", 102, 102,
                79),
        Tran_CashTerInOutOrgItem(EnmTranItemCode.Tran_CashTerInOutOrgItem, "50", "50", "캐쉬터미널 입출금 원거래 정보 아이템", 0, 0, 0),
        Tran_ScoScaslupCntItem(EnmTranItemCode.Tran_ScoScaslupCntItem, "52", "52", "SCO스캔저울증가횟수", 0, 0, 0),
        Tran_ChicorOnOffItem(EnmTranItemCode.Tran_ChicorOnOffItem, "55", "55", "시코르 통합 멤버십 ITEM", 524, 524, 347),
        Tran_ExchangeItem(EnmTranItemCode.Tran_ExchangeItem, "63", "63", "교환 거래 ITEM", 0, 0, 0),
        Tran_PointItem(EnmTranItemCode.Tran_PointItem, "64", "64", "신세계 포인트 아이템", 0, 0, 0),
        Tran_VipMileageItem(EnmTranItemCode.Tran_VipMileageItem, "67", "67", "세일리지 아이템", 0, 0, 0),
        Tran_SSGConItem(EnmTranItemCode.Tran_SSGConItem, "68", "68", "쓱콘 아이템", 0, 0, 0),
        Tran_MobileMmsItem(EnmTranItemCode.Tran_MobileMmsItem, "74", "74", "모바일&신세계MMS ITEM", 0, 0, 0),
        Tran_ShinBaekRewardItem(EnmTranItemCode.Tran_ShinBaekRewardItem, "78", "78", "리워드(보상형) 아이템", 0, 0, 0),
        Tran_ShinBaekRewardCouponItem(EnmTranItemCode.Tran_ShinBaekRewardCouponItem, "79", "79", "리워드(쿠폰) 아이템", 0, 0,
                0),
        Tran_GiftKioskCommItem(EnmTranItemCode.Tran_GiftKioskCommItem, "81", "81", "상품권 판매 키오스크 공통정보", 0, 0, 0),
        Tran_GiftKioskErrorItem(EnmTranItemCode.Tran_GiftKioskErrorItem, "82", "82", "상품권 판매 키오스크 장애상태정보", 48, 48, 20),
        Tran_GiftKioskStartItem(EnmTranItemCode.Tran_GiftKioskStartItem, "83", "83", "상품권 판매 키오스크 개국발생", 0, 0, 0),
        Tran_GiftKioskInOutItem(EnmTranItemCode.Tran_GiftKioskInOutItem, "84", "84", "상품권 판매 키오스크 시재장입및회수", 0, 0, 0),
        Tran_GiftKioskCalcItem(EnmTranItemCode.Tran_GiftKioskCalcItem, "86", "86", "상품권 판매 키오스크 정산기시재", 0, 0, 0),
        Tran_GiftKioskGiftSeqItem(EnmTranItemCode.Tran_GiftKioskGiftSeqItem, "89", "89", "상품권 판매 키오스크 상품권일련번호", 72, 72,
                49),
        Tran_CalcItem(EnmTranItemCode.Tran_CalcItem, "99", "99", "정산시재금 아이템", 30, 30, 6),
        Tran_CashItem(EnmTranItemCode.Tran_CashItem, "11", "11", "현금성 아이템", 0, 0, 0),
        Tran_DepositItem(EnmTranItemCode.Tran_DepositItem, "62", "62", "외상거래 아이템", 0, 0, 0),
        Tran_Header(EnmTranItemCode.Tran_Header, "60", "A0", "트란 헤더(TRAN Header)", 0, 396, 0),
        Tran_GoodsItem(EnmTranItemCode.Tran_GoodsItem, "61", "B0", "상품정보 아이템", 0, 576, 0),
        Tran_CCFreeSetItem(EnmTranItemCode.Tran_CCFreeSetItem, "70", "B2", "시코르 증정/SET 아이템", 0, 170, 0),
        Tran_CashTerExpenItem(EnmTranItemCode.Tran_CashTerExpenItem, "75", "C0", "캐시터미널 E-Accounting 결재출금", 0, 101, 0),
        Tran_CashTerAmtItem_DX(EnmTranItemCode.Tran_CashTerAmtItem_DX, "51", "C1", "캐쉬터미널 연동 POS 입출금 거래 금액 정보 아이템", 251,
                261, 0),
        Tran_DmsItem(EnmTranItemCode.Tran_DmsItem, "66", "D0", "NEW DMS ITEM", 0, 0, 0),
        Tran_BmpCampaignItem(EnmTranItemCode.Tran_BmpCampaignItem, "73", "D1", "행사정보 ITEM(BMP 청구할인)", 0, 92, 0),
        Tran_CardIcItem(EnmTranItemCode.Tran_CardIcItem, "65", "E1", "신용카드(IC) 아이템", 2300, 2408, 2102),
        Tran_KakaopayItem(EnmTranItemCode.Tran_KakaopayItem, "90", "E2", "카카오페이 ITEM", 0, 1153, 0),
        Tran_EasyPaymentItem(EnmTranItemCode.Tran_EasyPaymentItem, "98", "E3", "간편결제 통합전문 ITEM", 0, 1770, 0),
        Tran_SupyoItem(EnmTranItemCode.Tran_SupyoItem, "16", "E4", "수표 아이템", 0, 114, 0),
        Tran_EtcPayItem(EnmTranItemCode.Tran_EtcPayItem, "", "E5", "기타결제", 0, 70, 0),
        Tran_UseSaleSpecialItem(EnmTranItemCode.Tran_UseSaleSpecialItem, "", "E6", "사용매출 특판(점포,법인)", 0, 100, 0),
        Tran_PreReservSaleItem(EnmTranItemCode.Tran_PreReservSaleItem, "", "E7", "선수예약판매", 0, 116, 0),
        Tran_ShinBaekMoneyItem(EnmTranItemCode.Tran_ShinBaekMoneyItem, "77", "E8", "신백머니 아이템", 0, 978, 0),
        Tran_RecommanderItem(EnmTranItemCode.Tran_RecommanderItem, "", "E9", "권유자(퍼스널쇼퍼)", 0, 64, 0),
        Tran_MileageAutoSaveItem(EnmTranItemCode.Tran_MileageAutoSaveItem, "", "EA", "마일리지자동적립", 0, 93, 0),
        Tran_UntactAccountItem(EnmTranItemCode.Tran_UntactAccountItem, "", "EB", "비대면 주문", 0, 608, 0),
        Tran_UntactCardItem(EnmTranItemCode.Tran_UntactCardItem, "", "EC", "비대면 결제", 0, 72, 0),
        Tran_MidPdaAmtItem(EnmTranItemCode.Tran_MidPdaAmtItem, "05", "05", "PDA중간입금 아이템", 0, 40, 0),
        Tran_ChicorClubItem(EnmTranItemCode.Tran_ChicorClubItem, "71", "71", "시코르 클럽 세일리지", 0, 486, 0),
        Tran_CashTerUseSaleItem(EnmTranItemCode.Tran_CashTerUseSaleItem, "76", "76", "캐시터미널 사용매출출금", 0, 40, 0),
        Tran_SetItem(EnmTranItemCode.Tran_SetItem, "56", "56", "특정매입 단품세트 구성상품 아이템", 0, 187, 0),
        Tran_PaycoinItem(EnmTranItemCode.Tran_PaycoinItem, "58", "58", "페이코인 아이템", 0, 970, 0),
        Tran_UnKnown(EnmTranItemCode.Tran_UnKnown, "!!", "!!", "UnKnown", 0, 0, 0);

        private EnmTranItemCode itemCode;

        private String itemCode_O;

        private String itemCode_N;

        private String itemCodeDesc;

        private int itemCodeLen_O; // 구 전문 전체 길이

        private int itemCodeLen_N; // 신전문 전체 길이

        private int itemCodeLen_O_exLoopLen; // 구전문 (전체길이 - 반복 길이)

        EnmTranItemCodeWrap(EnmTranItemCode itemCode, String itemCode_O, String itemCode_N, String itemCodeDesc,
                int itemCodeLen_O, int itemCodeLen_N, int itemCodeLen_O_exLoopLen) {
            this.itemCode = itemCode;
            this.itemCode_O = itemCode_O;
            this.itemCode_N = itemCode_N;
            this.itemCodeDesc = itemCodeDesc;
            this.itemCodeLen_O = itemCodeLen_O;
            this.itemCodeLen_N = itemCodeLen_N;
            this.itemCodeLen_O_exLoopLen = itemCodeLen_O_exLoopLen;
        }

        public static EnmTranItemCodeWrap findItemCodeWithItemCode(String sItemCode){
            return Arrays.stream(EnmTranItemCodeWrap.values())
                    .filter(icode -> (icode.itemCode_O.equals(sItemCode) || icode.itemCode_N.equals(sItemCode)))
                    .findAny().orElse(Tran_UnKnown);
        }
    }
}
