/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.util
 * @fileName : JsonUtil.java
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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
public class JsonUtil {

    private static ObjectMapper mapper = null;

    public static ObjectMapper getMapper(){
        if(mapper == null){
            mapper = new ObjectMapper();

            DefaultSerializerProvider sp = new DefaultSerializerProvider.Impl();
            sp.setNullValueSerializer(new NullSerializer());
            mapper.setSerializerProvider(sp);
            // mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            // // null 제외, absent 제외, isEmpty() true 제외, Array length 0 제외,
            // String length 0 제외
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return mapper;
    }

    public static class NullSerializer extends JsonSerializer<Object> {

        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException{
            jgen.writeString("");
        }
    }
}
