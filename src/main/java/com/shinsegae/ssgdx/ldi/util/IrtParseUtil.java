/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.util
 * @fileName : ProtoParseUtil.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.shinsegae.ssgdx.ldi.model.comirt.ComHeader;
import com.shinsegae.ssgdx.ldi.model.comirt.DmsComHeader;
import com.shinsegae.ssgdx.ldi.model.comirt.EncHeader;
import com.shinsegae.ssgdx.ldi.util.IEnumComIrt.EnmIrtFieldType;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Slf4j
public class IrtParseUtil {

    /**
     * 주어진 Object의 전체 멤버 변수의 길이 계산
     * 
     * @param <T>
     * @param irtObj
     * @return
     */
    public static <T> int getIrtTotLen(T irtObj){
        int irtTotLen = 0;

        if(irtObj != null){
            try{
                for(Field field : irtObj.getClass().getDeclaredFields()){
                    IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
                    if(irtFldMeta != null){
                        field.setAccessible(true);
                        // 제너릭의 파라미터는 1개만 존재해야 한다!!
                        if("list".equals(irtFldMeta.fldType().getType())){
                            Type type = field.getGenericType();
                            if(type instanceof ParameterizedType){
                                Type[] inType = ((ParameterizedType)type).getActualTypeArguments();
                                if(inType.length > 1){
                                    log.error("func:getIrtTotLen -- {}", "[ERROR] Collection parameter는 하나만 존재해야 한다!!");
                                    return 0;
                                }
                                irtTotLen = irtTotLen + (getIrtTotLen(Class.forName(inType[0].getTypeName()))
                                        * irtFldMeta.loopCount());
                            }
                        }else if("class".equals(irtFldMeta.fldType().getType())){
                            irtTotLen = irtTotLen
                                    + (getIrtTotLen(Class.forName(field.getType().getName())) * irtFldMeta.loopCount());
                        }
                        // Object일 경우 필드의 클래스 정보 획득
                        else if("object".equals(irtFldMeta.fldType().getType())){
                            irtTotLen = irtTotLen
                                    + (getIrtTotLen(field.get(irtObj).getClass()) * irtFldMeta.loopCount());
                        }else{
                            irtTotLen += irtFldMeta.size();
                        }
                    }
                }
                log.debug("func:getIrtTotLen -- Obj:{} -- irtTotLen:{}", irtObj.getClass().getName(), irtTotLen);
            }catch(Exception e){
                // TODO Auto-generated catch block
                log.error("getIrtTotLen1 Exception. {}", e);
            }
        }
        return irtTotLen;
    }

    /**
     * 주어진 Class의 전체 멤버 변수의 길이 계산
     * 
     * @param <T>
     * @param irtClazz
     * @return
     */
    public static <T> int getIrtTotLen(Class<?> irtClazz){
        int irtTotLen = 0;

        if(irtClazz != null){
            try{
                for(Field field : irtClazz.getDeclaredFields()){
                    IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
                    if(irtFldMeta != null){
                        field.setAccessible(true);
                        // 제너릭의 파라미터는 1개만 존재해야 한다!!
                        if("list".equals(irtFldMeta.fldType().getType())){
                            Type type = field.getGenericType();
                            if(type instanceof ParameterizedType){
                                Type[] inType = ((ParameterizedType)type).getActualTypeArguments();
                                if(inType.length > 1){
                                    log.error("func:getIrtTotLen -- {}", "[ERROR] Collection parameter는 하나만 존재해야 한다!!");
                                    return 0;
                                }
                                irtTotLen = irtTotLen + (getIrtTotLen(Class.forName(inType[0].getTypeName()))
                                        * irtFldMeta.loopCount());
                            }
                        }else if("class".equals(irtFldMeta.fldType().getType())){
                            irtTotLen = irtTotLen
                                    + (getIrtTotLen(Class.forName(field.getType().getName())) * irtFldMeta.loopCount());
                        }else{
                            irtTotLen += irtFldMeta.size();
                        }
                    }
                }
                log.debug("func:getIrtTotLen -- Obj:{} -- irtTotLen:{}", irtClazz.getName(), irtTotLen);
            }catch(Exception e){
                // TODO Auto-generated catch block
                log.error("getIrtTotLen2 Exception. {}", e);
            }
        }else{
            log.error("func:getIrtTotLen -- {}", "[ERROR] object is null!!");
        }
        return irtTotLen;
    }

    /**
     * <pre>
     * 고정길이 bytes 를 객체로 변환
     * </pre>
     *
     * @param <T>
     * @param irtBytes
     * @param offSet
     * @param tgObj
     * @param charSet
     * @return
     */
    public static <T> int irtMapToObj(byte[] irtBytes, int[] offSet, T tgObj, String charSet){
        int ret = -1;

        if(tgObj != null){
            ret = 0;
            StringBuffer sb = new StringBuffer();
            try{
                sb.append("\n");
                sb.append("func:irtMapToObj -- Obj:" + tgObj.getClass().getName() + " -- offset:" + offSet[0]
                        + " -- start\n");

                // 대상 객체의 멤버 조회
                for(Field field : tgObj.getClass().getDeclaredFields()){
                    // 어노테이션 선언 멤버만 대상으로 본다
                    IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
                    if(irtFldMeta != null){
                        // 멤버 접근 설정
                        field.setAccessible(true);

                        // field 타입이 제너릭 배열이면 제너릭 타입의 클래스(inner) 멤버 값 세팅
                        // 리스트일 경우 제너릭 클래스 파싱
                        // 클래스 멤버일 경우 클래스 파싱
                        if("list".equals(irtFldMeta.fldType().getType())
                                || "class".equals(irtFldMeta.fldType().getType())){
                            ret = irtMapToObjForCollListOrInnClsMemb(irtBytes, offSet, tgObj, charSet, sb, field,
                                    irtFldMeta);

                            if(ret != 0)
                                break;
                        }
                        // 기본타입 멤버일 경우 파싱
                        else{
                            String fv = "";
                            int size = irtFldMeta.size();
                            if(size > 0){
                                byte[] b = new byte[size];
                                System.arraycopy(irtBytes, offSet[0], b, 0, size);
                                fv = new String(b, charSet);
                                field.set(tgObj, fv);
                                offSet[0] += size;
                            }
                            sb.append(String.format("fn:%-30s -- fs:%-8d -- fv:[%s]\n", field.getName(),
                                    irtFldMeta.size(), fv));
                        }
                    }
                }

                if(ret == 0)
                    sb.append("func:irtMapToObj -- Obj:" + tgObj.getClass().getName() + " -- offset:" + offSet[0]
                            + " -- end");
                else
                    log.error("func:irtMapToObj -- {}", "[ERROR] parsing error!!");

                // DEBUG TEST
                log.info(sb.toString());
            }catch(Exception e){
                // TODO Auto-generated catch block
                log.error("irtMapToObj Exception. {}", e);
            }
        }else{
            log.error("func:irtMapToObj -- {}", "[ERROR] object is null!!");
        }

        return ret;
    }

    /**
     * <pre>
     * 멤버변수가 List, Class일 경우 변환 처리 (고정길이 bytes 를 객체로 변환)
     * </pre>
     *
     * @param <T>
     * @param irtBytes
     * @param offSet
     * @param tgObj
     * @param charSet
     * @param sb
     * @param field
     * @param irtFldMeta
     * @return
     * @throws Exception
     */
    public static <T> int irtMapToObjForCollListOrInnClsMemb(byte[] irtBytes, int[] offSet, T tgObj, String charSet,
            StringBuffer sb, Field field, IIrtFldMeta irtFldMeta) throws Exception{
        int ret = -1;

        // [1] 멤버변수가 ArrayList 제너릭 타입일 경우
        if("list".equals(irtFldMeta.fldType().getType())){
            Type type = field.getGenericType();
            if(type instanceof ParameterizedType){
                Type[] inType = ((ParameterizedType)type).getActualTypeArguments();
                if(inType.length > 1){
                    log.error("func:irtMapToObjForCollListOrInnClsMemb -- {}",
                            "[ERROR] Collection parameter는 하나만 존재해야 한다!!");
                    return ret;
                }

                // ArrayList 제너릭 타입 생성
                Class<?> clazz = Class.forName(inType[0].getTypeName());
                int loopCount = irtFldMeta.loopCount();

                ret = 0;
                for(int i = 0; i < irtFldMeta.loopCount(); i++){
                    String typeName = clazz.getName();
                    Object inObj = null;

                    // 사용자 정의 클래스일때만 매핑
                    if(typeName != null && typeName.contains("com.shinsegae.ssgdx")){
                        // 내부 클래스 체크해서 내부 클래스용 인스턴스 생성
                        if(typeName.contains("$")){
                            Class<?> innerClass = Class.forName(typeName);

                            // inner class를 static으로 변경해서 수정 함
                            if(Modifier.isStatic(innerClass.getModifiers())){
                                Constructor<?> ctor = innerClass.getDeclaredConstructor();
                                ctor.setAccessible(true);
                                inObj = ctor.newInstance();
                            }else{
                                Constructor<?> ctor = innerClass.getDeclaredConstructor(tgObj.getClass());
                                ctor.setAccessible(true);
                                inObj = ctor.newInstance(tgObj);
                            }
                        }
                        // 외부 클래스용 인스턴스 생성
                        else{
                            inObj = clazz.getDeclaredConstructor().newInstance();
                        }

                        sb.append(String.format("[class:%s (loopCount:%2d, idx:%2d)]\n",
                                inObj.getClass().getSimpleName(), loopCount, i + 1));
                        for(Field inField : clazz.getDeclaredFields()){
                            IIrtFldMeta inIrtFldMeta = inField.getAnnotation(IIrtFldMeta.class);
                            if(inIrtFldMeta != null){
                                inField.setAccessible(true);

                                // field 타입이 제너릭 배열이면 제너릭 타입의 클래스(inner) 멤버 값 세팅
                                // 리스트일 경우 제너릭 클래스 파싱
                                // 클래스 멤버일 경우 클래스 파싱
                                if("list".equals(inIrtFldMeta.fldType().getType())
                                        || "class".equals(inIrtFldMeta.fldType().getType())){
                                    ret = irtMapToObjForCollListOrInnClsMemb(irtBytes, offSet, inObj, charSet, sb,
                                            inField, inIrtFldMeta);

                                    if(ret != 0)
                                        break;
                                }else{
                                    String fv = "";
                                    int size = inIrtFldMeta.size();
                                    if(size > 0){
                                        byte[] b = new byte[size];
                                        System.arraycopy(irtBytes, offSet[0], b, 0, size);
                                        fv = new String(b, charSet);
                                        inField.set(inObj, fv);
                                        offSet[0] += size;
                                    }
                                    sb.append(String.format("fn:%-30s -- fs:%-8d -- fv:[%s]\n", inField.getName(),
                                            inIrtFldMeta.size(), fv));
                                }
                            }
                        }

                        if(ret != 0)
                            break;

                        // add ArrayList
                        @SuppressWarnings("unchecked")
                        ArrayList<Object> actualList = (ArrayList<Object>)field.get(inObj);
                        actualList.add(inObj);
                        field.set(tgObj, actualList);
                    }
                }
            }else{
                log.error("func:irtMapToObjForCollListOrInnClsMemb -- {}",
                        "[ERROR] type instanceof ParameterizedType) error!!");
            }
        }
        // [2] 멤버변수가 클래스일 경우
        else if("class".equals(irtFldMeta.fldType().getType())){
            Class<?> clazz = field.getType();
            String typeName = field.getType().getName();
            Object inObj = null;

            // 사용자 정의 클래스일때만 매핑
            if(typeName != null && typeName.contains("com.shinsegae.ssgdx") && !clazz.isPrimitive()){
                // 내부 클래스 체크해서 내부 클래스용 인스턴스 생성
                if(typeName.contains("$")){
                    Class<?> innerClass = Class.forName(typeName);

                    // inner class를 static으로 변경해서 수정 함
                    if(Modifier.isStatic(innerClass.getModifiers())){
                        Constructor<?> ctor = innerClass.getDeclaredConstructor();
                        ctor.setAccessible(true);
                        inObj = ctor.newInstance();
                    }else{
                        Constructor<?> ctor = innerClass.getDeclaredConstructor(tgObj.getClass());
                        ctor.setAccessible(true);
                        inObj = ctor.newInstance(tgObj);
                    }
                }
                // 외부 클래스용 인스턴스 생성
                else{
                    inObj = clazz.getDeclaredConstructor().newInstance();
                }

                sb.append(String.format("[class:%s]\n", inObj.getClass().getSimpleName()));

                ret = 0;
                for(Field inField : clazz.getDeclaredFields()){
                    IIrtFldMeta inIrtFldMeta = inField.getAnnotation(IIrtFldMeta.class);
                    if(inIrtFldMeta != null){
                        inField.setAccessible(true);

                        // field 타입이 제너릭 배열이면 제너릭 타입의 클래스(inner) 멤버 값 세팅
                        // 리스트일 경우 제너릭 클래스 파싱
                        // 클래스 멤버일 경우 클래스 파싱
                        if("list".equals(inIrtFldMeta.fldType().getType())
                                || "class".equals(inIrtFldMeta.fldType().getType())){
                            ret = irtMapToObjForCollListOrInnClsMemb(irtBytes, offSet, inObj, charSet, sb, inField,
                                    inIrtFldMeta);

                            if(ret != 0)
                                break;
                        }else{
                            String fv = "";
                            int size = inIrtFldMeta.size();
                            if(size > 0){
                                byte[] b = new byte[size];
                                System.arraycopy(irtBytes, offSet[0], b, 0, size);
                                fv = new String(b, charSet);
                                inField.set(inObj, fv);
                                offSet[0] += size;
                            }
                            sb.append(String.format("fn:%-30s -- fs:%-8d -- fv:[%s]\n", inField.getName(),
                                    inIrtFldMeta.size(), fv));
                        }
                    }
                }

                if(ret == 0)
                    field.set(tgObj, inObj);
            }
        }

        return ret;
    }

    /**
     * <pre>
     * 객체를 고정길이 bytes로 변환
     * </pre>
     *
     * @param <T>
     * @param irtBytes
     * @param offSet
     * @param tgObj
     * @param charSet
     * @return
     */
    public static <T> int irtMapToBytes(byte[] irtBytes, int[] offSet, T tgObj, String charSet){
        int ret = -1;

        if(tgObj != null){
            ret = 0;
            StringBuffer sb = new StringBuffer();
            try{
                sb.append("\n");
                sb.append("func:irtMapToBytes -- Obj:" + tgObj.getClass().getName() + " -- offset:" + offSet[0]
                        + " -- start\n");

                // 대상 객체의 멤버 조회
                for(Field field : tgObj.getClass().getDeclaredFields()){
                    // 어노테이션 선언 멤버만 대상으로 본다
                    IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
                    if(irtFldMeta != null){
                        // 멤버 접근 설정
                        field.setAccessible(true);

                        // field 타입이 제너릭 배열이면 제너릭 타입의 클래스(inner) 멤버 값 세팅
                        // 리스트일 경우 제너릭 클래스 파싱
                        // 클래스 멤버일 경우 클래스 파싱
                        if("list".equals(irtFldMeta.fldType().getType())
                                || "class".equals(irtFldMeta.fldType().getType())
                                || "object".equals(irtFldMeta.fldType().getType())){

                            ret = irtMapToBytesForCollListOrInnClsMemb(irtBytes, offSet, tgObj, charSet, sb, field,
                                    irtFldMeta);

                            if(ret != 0)
                                break;
                        }
                        // 기본타입 멤버일 경우 파싱
                        else{
                            String fv = "";
                            int size = irtFldMeta.size();
                            if(size > 0){
                                fv = field.get(tgObj).toString();
                                byte[] bFldVal = fv.getBytes(charSet);
                                byte[] bTgVal = new byte[size];

                                setFixLengthByteValue(bFldVal, bTgVal, size, irtFldMeta.fldType());
                                System.arraycopy(bTgVal, 0, irtBytes, offSet[0], size);
                                offSet[0] += size;
                            }
                            sb.append(String.format("fn:%-30s -- fs:%-8d -- fv:[%s]\n", field.getName(),
                                    irtFldMeta.size(), fv));
                        }
                    }
                }

                if(ret == 0)
                    sb.append("func:irtMapToBytes -- Obj:" + tgObj.getClass().getName() + " -- offset:" + offSet[0]
                            + " -- end");
                else
                    log.error("func:irtMapToBytes -- {}", "[ERROR] parsing error!!");

                // DEBUG TEST
                log.info(sb.toString());
            }catch(Exception e){
                // TODO Auto-generated catch block
                log.error("irtMapToBytes Exception. {}", e);
            }
        }else{
            log.error("func:irtMapToBytes -- {}", "[ERROR] object is null!!");
        }

        return ret;
    }

    /**
     * <pre>
     * 멤버변수가 List, Class일 경우 변환 처리 (객체를 고정길이 bytes로 변환)
     * </pre>
     *
     * @param <T>
     * @param irtBytes
     * @param offSet
     * @param tgObj
     * @param charSet
     * @param sb
     * @param field
     * @param irtFldMeta
     * @return
     * @throws Exception
     */
    public static <T> int irtMapToBytesForCollListOrInnClsMemb(byte[] irtBytes, int[] offSet, T tgObj, String charSet,
            StringBuffer sb, Field field, IIrtFldMeta irtFldMeta) throws Exception{
        int ret = -1;

        // [1] 멤버변수가 ArrayList 제너릭 타입일 경우
        if("list".equals(irtFldMeta.fldType().getType())){
            Type type = field.getGenericType();
            if(type instanceof ParameterizedType){
                Type[] inType = ((ParameterizedType)type).getActualTypeArguments();
                if(inType.length > 1){
                    log.error("func:irtMapToBytesForCollListOrInnClsMemb -- {}",
                            "[ERROR] Collection parameter는 하나만 존재해야 한다!!");
                    return ret;
                }

                // ArrayList 제너릭 타입 생성
                Class<?> clazz = Class.forName(inType[0].getTypeName());
                int loopCount = irtFldMeta.loopCount();

                ret = 0;
                for(int i = 0; i < irtFldMeta.loopCount(); i++){
                    String typeName = clazz.getName();

                    // 사용자 정의 클래스일때만 매핑
                    if(typeName != null && typeName.contains("com.shinsegae.ssgdx")){
                        Object inObj = field.get(tgObj);

                        sb.append(String.format("[class:%s (loopCount:%2d, idx:%2d)]\n",
                                inObj.getClass().getSimpleName(), loopCount, i + 1));
                        for(Field inField : clazz.getDeclaredFields()){
                            IIrtFldMeta inIrtFldMeta = inField.getAnnotation(IIrtFldMeta.class);
                            if(inIrtFldMeta != null){
                                inField.setAccessible(true);

                                // field 타입이 제너릭 배열이면 제너릭 타입의 클래스(inner) 멤버 값 세팅
                                // 리스트일 경우 제너릭 클래스 파싱
                                // 클래스 멤버일 경우 클래스 파싱
                                if("list".equals(inIrtFldMeta.fldType().getType())
                                        || "class".equals(inIrtFldMeta.fldType().getType())
                                        || "object".equals(inIrtFldMeta.fldType().getType())){
                                    ret = irtMapToBytesForCollListOrInnClsMemb(irtBytes, offSet, inObj, charSet, sb,
                                            inField, inIrtFldMeta);

                                    if(ret != 0)
                                        break;
                                }else{
                                    String fv = "";
                                    int size = inIrtFldMeta.size();
                                    if(size > 0){
                                        fv = inField.get(inObj).toString();
                                        byte[] bFldVal = fv.getBytes(charSet);
                                        byte[] bTgVal = new byte[size];

                                        setFixLengthByteValue(bFldVal, bTgVal, size, inIrtFldMeta.fldType());
                                        System.arraycopy(bTgVal, 0, irtBytes, offSet[0], size);
                                        offSet[0] += size;
                                    }
                                    sb.append(String.format("fn:%-30s -- fs:%-8d -- fv:[%s]\n", inField.getName(),
                                            inIrtFldMeta.size(), fv));
                                }
                            }
                        }

                        if(ret != 0)
                            break;
                    }
                }
            }else{
                log.error("func:irtMapToBytesForCollListOrInnClsMemb -- {}",
                        "[ERROR] type instanceof ParameterizedType) error!!");
            }
        }
        // [2] 멤버변수가 클래스일 경우
        else if("class".equals(irtFldMeta.fldType().getType()) || "object".equals(irtFldMeta.fldType().getType())){
            Class<?> clazz = null;
            String typeName = null;

            if("class".equals(irtFldMeta.fldType().getType())){
                clazz = field.getType();
                typeName = field.getType().getName();
            }else{
                clazz = field.get(tgObj).getClass();
                typeName = field.get(tgObj).getClass().getName();
            }

            // 사용자 정의 클래스일때만 매핑
            if(typeName != null && typeName.contains("com.shinsegae.ssgdx") && !clazz.isPrimitive()){
                Object inObj = field.get(tgObj);

                sb.append(String.format("[class:%s]\n", inObj.getClass().getSimpleName()));

                ret = 0;
                for(Field inField : clazz.getDeclaredFields()){
                    IIrtFldMeta inIrtFldMeta = inField.getAnnotation(IIrtFldMeta.class);
                    if(inIrtFldMeta != null){
                        inField.setAccessible(true);

                        // field 타입이 제너릭 배열이면 제너릭 타입의 클래스(inner) 멤버 값 세팅
                        // 리스트일 경우 제너릭 클래스 파싱
                        // 클래스 멤버일 경우 클래스 파싱
                        if("list".equals(inIrtFldMeta.fldType().getType())
                                || "class".equals(inIrtFldMeta.fldType().getType())
                                || "object".equals(inIrtFldMeta.fldType().getType())){
                            ret = irtMapToBytesForCollListOrInnClsMemb(irtBytes, offSet, inObj, charSet, sb, inField,
                                    inIrtFldMeta);

                            if(ret != 0)
                                break;
                        }else{
                            String fv = "";
                            int size = inIrtFldMeta.size();
                            if(size > 0){
                                fv = inField.get(inObj).toString();
                                byte[] bFldVal = fv.getBytes(charSet);
                                byte[] bTgVal = new byte[size];

                                setFixLengthByteValue(bFldVal, bTgVal, size, inIrtFldMeta.fldType());
                                System.arraycopy(bTgVal, 0, irtBytes, offSet[0], size);
                                offSet[0] += size;
                            }
                            sb.append(String.format("fn:%-30s -- fs:%-8d -- fv:[%s]\n", inField.getName(),
                                    inIrtFldMeta.size(), fv));
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * <pre>
     * 주어진 byte 배을의 값을 특정 필드에 세팅
     * </pre>
     *
     * @param <T>
     * @param irtBytes
     * @param offSet
     * @param tgObj
     * @param fldName
     * @param charSet
     * @return
     * @throws Exception
     */
    public static <T> int irtMapToObjFromObjFld(byte[] irtBytes, int[] offSet, T tgObj, String fldName, String charSet)
            throws Exception{
        int ret = -1;
        Field field = tgObj.getClass().getDeclaredField(fldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        StringBuffer sb = new StringBuffer();

        if(irtFldMeta != null){
            ret = 0;
            // field 타입이 제너릭 배열이면 제너릭 타입의 클래스(inner) 멤버 값 세팅
            // 리스트일 경우 제너릭 클래스 파싱
            // 클래스 멤버일 경우 클래스 파싱
            if("list".equals(irtFldMeta.fldType().getType()) || "class".equals(irtFldMeta.fldType().getType())){
                ret = irtMapToObjForCollListOrInnClsMemb(irtBytes, offSet, tgObj, charSet, sb, field, irtFldMeta);
            }
            // 기본타입 멤버일 경우 파싱
            else{
                String fv = "";
                int size = irtFldMeta.size();
                if(size > 0){
                    byte[] b = new byte[size];
                    System.arraycopy(irtBytes, offSet[0], b, 0, size);
                    fv = new String(b, charSet);
                    field.set(tgObj, fv);
                    offSet[0] += size;
                }
                sb.append(String.format("fn:%-30s -- fs:%-8d -- fv:[%s]\n", field.getName(), irtFldMeta.size(), fv));
            }
        }

        if(ret == 0)
            sb.append("func:irtMapToObjFromObjFld -- Obj:" + tgObj.getClass().getName() + " -- offset:" + offSet[0]
                    + " -- end");
        else
            log.error("func:irtMapToObjFromObjFld -- {}", "[ERROR] parsing error!!");

        log.debug(sb.toString());

        return ret;
    }

    /**
     * <pre>
     * 특정 필드의 값을 주어진 byte 배열에 세팅
     * </pre>
     *
     * @param <T>
     * @param irtBytes
     * @param offSet
     * @param tgObj
     * @param fldName
     * @param charSet
     * @return
     * @throws Exception
     */
    public static <T> int irtMapToBytesFromObjFld(byte[] irtBytes, int[] offSet, T tgObj, String fldName,
            String charSet) throws Exception{
        int ret = -1;
        Field field = tgObj.getClass().getDeclaredField(fldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        StringBuffer sb = new StringBuffer();

        if(irtFldMeta != null){
            ret = 0;
            // field 타입이 제너릭 배열이면 제너릭 타입의 클래스(inner) 멤버 값 세팅
            // 리스트일 경우 제너릭 클래스 파싱
            // 클래스 멤버일 경우 클래스 파싱
            if("list".equals(irtFldMeta.fldType().getType()) || "class".equals(irtFldMeta.fldType().getType())
                    || "object".equals(irtFldMeta.fldType().getType())){
                ret = irtMapToBytesForCollListOrInnClsMemb(irtBytes, offSet, tgObj, charSet, sb, field, irtFldMeta);
            }
            // 기본타입 멤버일 경우 파싱
            else{
                String fv = "";
                int size = irtFldMeta.size();
                if(size > 0){
                    fv = field.get(tgObj).toString();
                    byte[] bFldVal = fv.getBytes(charSet);
                    byte[] bTgVal = new byte[size];

                    setFixLengthByteValue(bFldVal, bTgVal, size, irtFldMeta.fldType());
                    System.arraycopy(bTgVal, 0, irtBytes, offSet[0], size);
                    offSet[0] += size;
                }
                sb.append(String.format("fn:%-30s -- fs:%-8d -- fv:[%s]\n", field.getName(), irtFldMeta.size(), fv));
            }
        }

        if(ret == 0)
            sb.append("func:irtMapToBytesFromObjFld -- Obj:" + tgObj.getClass().getName() + " -- offset:" + offSet[0]
                    + " -- end");
        else
            log.error("func:irtMapToBytesFromObjFld -- {}", "[ERROR] parsing error!!");

        log.debug(sb.toString());

        return ret;
    }

    /**
     * <pre>
     * 고정길이 바이트 값 변환 (주어진 byte 배열에 값 세팅)
     * </pre>
     *
     * @param bFldVal
     * @param bTgVal
     * @param fldType
     */
    public static void setFixLengthByteValue(byte[] bFldVal, byte[] bTgVal, int nTgSize, EnmIrtFieldType fldType){
        // [1] 초기화
        for(int i = 0; i < nTgSize; i++){
            if("LS".equals(fldType.getType()) || "RS".equals(fldType.getType()) || "LI".equals(fldType.getType())){
                bTgVal[i] = 32; // ' '
            }else if("RI".equals(fldType.getType())){
                bTgVal[i] = 48; // '0'
            }
        }

        // [2] 왼쪽 정렬 문자일경우 : '문자값' + Space, 왼쪽 정렬 숫자일경우 : '숫자값' + Space
        if("LS".equals(fldType.getType()) || "LI".equals(fldType.getType())){
            for(int i = 0; i < (bFldVal.length < nTgSize ? bFldVal.length : nTgSize); i++){
                bTgVal[i] = bFldVal[i];
            }
        }
        // [2] 오른쪽 정렬 문자일경우 : Space + '문자값', 오른쪽 정렬 숫자일경우 : '0' + '숫자값'
        else if("RS".equals(fldType.getType()) || "RI".equals(fldType.getType())){
            for(int i = (bFldVal.length < nTgSize ? nTgSize - bFldVal.length : 0); i < nTgSize; i++){
                bTgVal[i] = bFldVal[i - (nTgSize - bFldVal.length)];
            }
        }
    }

    /**
     * <pre>
     * 고정길이 바이트 값 변환 (바이트 배열 반환)
     * </pre>
     *
     * @param bFldVal
     * @param nTgSize
     * @param fldType
     * @return byte[]
     */
    public static byte[] getFixLengthByteValue(byte[] bFldVal, int nTgSize, EnmIrtFieldType fldType){
        byte[] bTgVal = new byte[nTgSize];

        // [1] 초기화
        for(int i = 0; i < nTgSize; i++){
            if("LS".equals(fldType.getType()) || "RS".equals(fldType.getType()) || "LI".equals(fldType.getType())){
                bTgVal[i] = 32; // ' '
            }else if("RI".equals(fldType.getType())){
                bTgVal[i] = 48; // '0'
            }
        }

        // [2] 왼쪽 정렬 문자일경우 : '문자값' + Space, 왼쪽 정렬 숫자일경우 : '숫자값' + Space
        if("LS".equals(fldType.getType()) || "LI".equals(fldType.getType())){
            for(int i = 0; i < (bFldVal.length < nTgSize ? bFldVal.length : nTgSize); i++){
                bTgVal[i] = bFldVal[i];
            }
        }
        // [2] 오른쪽 정렬 문자일경우 : Space + '문자값', 오른쪽 정렬 숫자일경우 : '0' + '숫자값'
        else if("RS".equals(fldType.getType()) || "RI".equals(fldType.getType())){
            for(int i = (bFldVal.length < nTgSize ? nTgSize - bFldVal.length : 0); i < nTgSize; i++){
                bTgVal[i] = bFldVal[i - (nTgSize - bFldVal.length)];
            }
        }

        return bTgVal;
    }

    /**
     * <pre>
     * 고정길이 바이트 값 변환 (String 반환)
     * </pre>
     *
     * @param bFldVal
     * @param nTgSize
     * @param fldType
     * @return String
     */
    public static String getFixLengthByteValueConvStr(byte[] bFldVal, int nTgSize, EnmIrtFieldType fldType,
            String charSet){
        String sRet = "";
        byte[] bTgVal = new byte[nTgSize];

        try{
            // [1] 초기화
            for(int i = 0; i < nTgSize; i++){
                if("LS".equals(fldType.getType()) || "RS".equals(fldType.getType()) || "LI".equals(fldType.getType())){
                    bTgVal[i] = 32; // ' '
                }else if("RI".equals(fldType.getType())){
                    bTgVal[i] = 48; // '0'
                }
            }

            // [2] 왼쪽 정렬 문자일경우 : '문자값' + Space, 왼쪽 정렬 숫자일경우 : '숫자값' + Space
            if("LS".equals(fldType.getType()) || "LI".equals(fldType.getType())){
                for(int i = 0; i < (bFldVal.length < nTgSize ? bFldVal.length : nTgSize); i++){
                    bTgVal[i] = bFldVal[i];
                }
            }
            // [2] 오른쪽 정렬 문자일경우 : Space + '문자값', 오른쪽 정렬 숫자일경우 : '0' + '숫자값'
            else if("RS".equals(fldType.getType()) || "RI".equals(fldType.getType())){
                for(int i = (bFldVal.length < nTgSize ? nTgSize - bFldVal.length : 0); i < nTgSize; i++){
                    bTgVal[i] = bFldVal[i - (nTgSize - bFldVal.length)];
                }
            }

            sRet = new String(bTgVal, charSet);
        }catch(Exception e){
            e.printStackTrace();
        }

        return sRet;
    }

    /**
     * 주어진 대상 객체의 필드 길이를 구한다
     * 
     * @param <T>
     * @param tgObj : 전문 파싱 대상 객체
     * @param fieldName : 대상 필드 이름
     * @return
     * @throws Exception
     */
    public static <T> int getFieldSize(T tgObj, String fieldName) throws Exception{
        Field field = tgObj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        return irtFldMeta.size();
    }

    /**
     * 주어진 대상 객체의 필드 타입을 구한다
     * 
     * @param <T>
     * @param tgObj : 전문 파싱 대상 객체
     * @param fieldName : 대상 필드 이름
     * @return
     * @throws Exception
     */
    public static <T> EnmIrtFieldType getFieldType(T tgObj, String fieldName) throws Exception{
        Field field = tgObj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        return irtFldMeta.fldType();
    }

    /**
     * 문자열 LPAD (' ')
     * 
     * @param <T>
     * @param tgObj : 대상 객체
     * @param fldName : 대상 필드 이름
     * @param inStr : 입력 값
     * @return
     * @throws Exception
     */
    public static <T> String getLpadFillSpaceStringForTgFld(T tgObj, String fldName, String inStr) throws Exception{
        Field field = tgObj.getClass().getDeclaredField(fldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        int size = irtFldMeta.size();
        if(inStr == null)
            inStr = "";

        // 대상 필드의 사이즈가 입력 값 보다 작으면 스페이스로 채운다.
        if(size < inStr.length() || inStr.length() == 0)
            inStr = "";

        String sf = "%" + String.valueOf(size) + "s";
        return String.format(sf, inStr);
    }

    public static <T> String getLpadFillSpaceStringConvIntForTgFld(T tgObj, String fldName, String inStr)
            throws Exception{
        Field field = tgObj.getClass().getDeclaredField(fldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        int size = irtFldMeta.size();
        int o_val = convStrToInt(inStr);

        String sf = "%" + String.valueOf(size) + "d";
        return String.format(sf, o_val);
    }

    public static String getLpadFillSpaceString(String sTg, int size) throws Exception{
        if(sTg == null)
            sTg = "";

        // 대상 필드의 사이즈가 입력 값 보다 작으면 스페이스로 채운다.
        if(size < sTg.length() || sTg.length() == 0)
            sTg = "";

        String sf = "%" + String.valueOf(size) + "s";
        return String.format(sf, sTg);
    }

    public static String getLpadFillSpaceStringConvInt(String sTg, int size) throws Exception{
        int o_val = convStrToInt(sTg);

        String sf = "%" + String.valueOf(size) + "d";
        return String.format(sf, o_val);
    }

    /**
     * 숫자 LPAD ('0')
     * 
     * @param <T>
     * @param tgObj : 대상 객체
     * @param fldName : 대상 필드 이름
     * @param inStr : 입력 값
     * @return
     * @throws Exception
     */
    public static <T> String getLpadFillZeroStringForTgFld(T tgObj, String fldName, String inStr) throws Exception{
        Field field = tgObj.getClass().getDeclaredField(fldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        int size = irtFldMeta.size();
        int o_val = 0;
        try{
            o_val = Integer.valueOf(inStr.trim());
        }catch(Exception e){
            // e.printStackTrace();
        }

        String sf = "%0" + String.valueOf(size) + "d";
        return String.format(sf, o_val);
    }

    public static <T> String getLpadFillZeroStringForTgFld(T tgObj, String fldName, int o_val) throws Exception{
        Field field = tgObj.getClass().getDeclaredField(fldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        int size = irtFldMeta.size();

        String sf = "%0" + String.valueOf(size) + "d";
        return String.format(sf, o_val);
    }

    public static String getLpadFillZeroString(String sTg, int size) throws Exception{
        int nVal = 0;
        try{
            nVal = Integer.valueOf(sTg.trim());
        }catch(Exception e){
            // e.printStackTrace();
        }

        String sf = "%0" + String.valueOf(size) + "d";
        return String.format(sf, nVal);
    }

    /**
     * 문자열 RPAD (' ')
     * 
     * @param <T>
     * @param tgObj : 대상 객체
     * @param fldName : 대상 필드 이름
     * @param inStr : 입력 값
     * @return
     * @throws Exception
     */
    public static <T> String getRpadFillSpaceStringForTgFld(T tgObj, String fldName, String inStr) throws Exception{
        Field field = tgObj.getClass().getDeclaredField(fldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        int size = irtFldMeta.size();
        if(inStr == null)
            inStr = "";

        // 대상 필드의 사이즈가 입력 값 보다 작으면 스페이스로 채운다.
        if(size < inStr.length() || inStr.length() == 0)
            inStr = "";

        String sf = "%-" + String.valueOf(size) + "s";
        return String.format(sf, inStr);
    }

    public static <T> String getRpadFillSpaceStringConvIntForTgFld(T tgObj, String fldName, String inStr)
            throws Exception{
        Field field = tgObj.getClass().getDeclaredField(fldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        int size = irtFldMeta.size();
        int o_val = convStrToInt(inStr);

        String sf = "%-" + String.valueOf(size) + "d";
        return String.format(sf, o_val);
    }

    public static String getRpadFillSpaceString(String sTg, int size) throws Exception{
        if(sTg == null)
            sTg = "";

        // 대상 필드의 사이즈가 입력 값 보다 작으면 스페이스로 채운다.
        if(size < sTg.length() || sTg.length() == 0)
            sTg = "";

        String sf = "%-" + String.valueOf(size) + "s";
        return String.format(sf, sTg);
    }

    public static String getRpadFillSpaceStringConvInt(String sTg, int size) throws Exception{
        int o_val = convStrToInt(sTg);

        String sf = "%-" + String.valueOf(size) + "d";
        return String.format(sf, o_val);
    }

    /**
     * 숫자 RPAD ('0')
     * 
     * @param <T>
     * @param tgObj : 대상 객체
     * @param fldName : 대상 필드 이름
     * @param inStr : 입력 값
     * @return
     * @throws Exception
     */
    public static <T> String getRpadFillZeroStringForTgFld(T tgObj, String fldName, String inStr) throws Exception{
        Field field = tgObj.getClass().getDeclaredField(fldName);
        field.setAccessible(true);
        IIrtFldMeta irtFldMeta = field.getAnnotation(IIrtFldMeta.class);
        int size = irtFldMeta.size();
        int o_val = 0;
        try{
            o_val = Integer.valueOf(inStr.trim());
        }catch(Exception e){
            // e.printStackTrace();
            inStr = "";
        }

        // 신전문 사이즈가 구전문보다 작으면 스페이스로 채운다.
        if(size < inStr.length() || inStr.length() == 0 || o_val == 0)
            inStr = "";

        return getRpadFillSpaceStringForTgFld(tgObj, fldName, inStr).replace(" ", "0");
    }

    /**
     * <pre>
     * 주어진 문자를 int로 변환 (예외 발생시 0 반환)
     * </pre>
     *
     * @param sSrc
     * @return
     */
    public static int convStrToInt(String sSrc){
        int nVal = 0;
        try{
            nVal = Integer.valueOf(sSrc.trim());
        }catch(Exception e){
            // e.printStackTrace();
        }
        return nVal;
    }

    /**
     * <pre>
     * 주어진 숫자를 문자로 변환 (예외 발생시 "" 반환)
     * </pre>
     *
     * @param sSrc
     * @return
     */
    public static String convIntToStr(int nSrc){
        String sVal = "";
        try{
            sVal = Integer.toString(nSrc);
        }catch(Exception e){
            // e.printStackTrace();
        }
        return sVal;
    }

    /**
     * <pre>
     * 둘 이상의 바이트 어레이 연결
     * </pre>
     *
     * @param arrays
     * @return
     */
    public static byte[] byteArraysConcat(byte[]... arrays){
        byte[] result = null;
        int len = Arrays.stream(arrays).filter(Objects::nonNull).mapToInt(s -> s.length).sum();

        if(len > 0){
            result = new byte[len];
            int lengthSoFar = 0;

            if(arrays != null){
                for(byte[] array : arrays){
                    if(array != null){
                        System.arraycopy(array, 0, result, lengthSoFar, array.length);
                        lengthSoFar += array.length;
                    }
                }
            }
        }

        return result;
    }

    /**
     * <pre>
     * 암호화 헤더 Copy
     * </pre>
     *
     * @param srcEncHd
     * @param tgEncHd
     * @param totLen
     * @param msgVli
     * @throws Exception
     */
    public static void copySrcEncHdToTgEncHd(EncHeader srcEncHd, EncHeader tgEncHd){
        tgEncHd.setENCTYPE(srcEncHd.getENCTYPE());
        tgEncHd.setPOSNO(srcEncHd.getPOSNO());
        tgEncHd.setTRANNO(srcEncHd.getTRANNO());
    }

    /**
     * <pre>
     * DMS Header Copy
     * </pre>
     *
     * @param srcDmsHd
     * @param tgDmsHd
     */
    public static void copySrcDmsHdToTgDmsHd(DmsComHeader srcDmsHd, DmsComHeader tgDmsHd){
        // tgDmsHd.setMSG_LEN(srcDmsHd.getMSG_LEN());
        tgDmsHd.setMSG_ID(srcDmsHd.getMSG_ID());
        tgDmsHd.setMSG_VER(srcDmsHd.getMSG_VER());
        tgDmsHd.setSALE_DATE(srcDmsHd.getSALE_DATE());
        tgDmsHd.setSTORE_CD(srcDmsHd.getSTORE_CD());
        tgDmsHd.setPOS_NO(srcDmsHd.getPOS_NO());
        tgDmsHd.setTRAN_NO(srcDmsHd.getTRAN_NO());
        tgDmsHd.setCOM_TYPE(IEnumComIrt.DEF_COM_TYPE_RES);
        tgDmsHd.setCOM_ID(srcDmsHd.getCOM_ID());

        LocalDateTime now = LocalDateTime.now();
        tgDmsHd.setCOM_DATE(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        tgDmsHd.setCOM_TIME(now.format(DateTimeFormatter.ofPattern("HHmmss")));

        tgDmsHd.setUSER_INFO(srcDmsHd.getUSER_INFO());
        // tgDmsHd.setRES_CODE(srcDmsHd.getRES_CODE());
        // tgDmsHd.setRES_MSG(srcDmsHd.getRES_MSG());
        tgDmsHd.setFILLER(srcDmsHd.getFILLER());
    }

    /**
     * <pre>
     * 통신 Header Copy
     * </pre>
     *
     * @param srcComHd
     * @param tgComHd
     */
    public static void copySrcComHdToTgComHd(ComHeader srcComHd, ComHeader tgComHd){
        // tgComHd.setMsgLength(srcComHd.getMsgLength());
        tgComHd.setMsgPath("MS"); // 매출수집 -> POS서버
        tgComHd.setMsgType(srcComHd.getMsgType());
        tgComHd.setMsgKind(srcComHd.getMsgKind());
        tgComHd.setSaleDate(srcComHd.getSaleDate());
        tgComHd.setStoreNo(srcComHd.getStoreNo());
        tgComHd.setPosNo(srcComHd.getPosNo());
        tgComHd.setTranNo(srcComHd.getTranNo());

        LocalDateTime now = LocalDateTime.now();
        tgComHd.setSendDate(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        tgComHd.setSendTime(now.format(DateTimeFormatter.ofPattern("HHmmss")));

        tgComHd.setExtStoreNo(srcComHd.getExtStoreNo());
        tgComHd.setTrainMode(srcComHd.getTrainMode());
        tgComHd.setVersionFlag(srcComHd.getVersionFlag());
        tgComHd.setUserInfo(srcComHd.getUserInfo());
        // tgComHd.setReplyCode(srcComHd.getReplyCode());
    }
}
