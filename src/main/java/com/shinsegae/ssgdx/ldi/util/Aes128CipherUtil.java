/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.util
 * @fileName : CipherUtil.java
 * @author : q93m9k
 * @date : 2024.01.25
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.01.25       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.25
 * @see :
 */
@Slf4j
@Getter
@Setter
public class Aes128CipherUtil {

    private final String INSTANCE_TYPE = "AES/CBC/PKCS5Padding";

    private String charSets;

    private String secretKey;

    private IvParameterSpec ivParameterSpec; // IV

    private SecretKeySpec secretKeySpec; // 비밀키

    private Cipher cipher;

    /**
     * @param charSet : 암/복호화 문자셋
     * @param posNo : POS 번호
     * @param tranNo : TRAN 번호
     * @param msgLen : 전문 길이
     * @throws Exception
     */
    public Aes128CipherUtil(String charSets, byte[] posNo, byte[] tranNo, byte[] msgLen) throws Exception {
        this.charSets = charSets;

        try{
            int offSet = 0;
            byte[] secKey = new byte[16];
            byte[] tmpKey1 = new byte[8];
            byte[] tmpKey2 = new byte[8];

            // [1] 데이터 키 생성
            System.arraycopy(posNo, 0, tmpKey1, offSet, 4);
            offSet += 4;

            System.arraycopy(tranNo, 0, tmpKey1, offSet, 4);

            offSet = 0;
            System.arraycopy("00".getBytes(), 0, tmpKey2, offSet, 2);
            offSet += 2;

            System.arraycopy(msgLen, 0, tmpKey2, offSet, 6);

            secKey[0] = (byte)(((byte)((byte)(tmpKey1[3] ^ tmpKey2[0]) % 10)) + 0x30);
            secKey[1] = (byte)(((byte)((byte)(tmpKey1[0] ^ tmpKey2[1]) % 10)) + 0x30);
            secKey[2] = (byte)(((byte)((byte)(tmpKey1[1] ^ tmpKey2[2]) % 10)) + 0x30);
            secKey[3] = (byte)(((byte)((byte)(tmpKey1[2] ^ tmpKey2[3]) % 10)) + 0x30);
            secKey[4] = (byte)(((byte)((byte)(tmpKey1[7] ^ tmpKey2[4]) % 10)) + 0x30);
            secKey[5] = (byte)(((byte)((byte)(tmpKey1[4] ^ tmpKey2[5]) % 10)) + 0x30);
            secKey[6] = (byte)(((byte)((byte)(tmpKey1[5] ^ tmpKey2[6]) % 10)) + 0x30);
            secKey[7] = (byte)(((byte)((byte)(tmpKey1[6] ^ tmpKey2[7]) % 10)) + 0x30);

            System.arraycopy(secKey, 0, secKey, 8, 8);

            secretKey = new String(secKey);

            // [2] 비밀키 생성
            secretKeySpec = new SecretKeySpec(secretKey.getBytes(charSets), "AES");

            // [3] IV 생성
            ivParameterSpec = new IvParameterSpec(secretKey.getBytes(charSets));

            // [4] Cipher 인스턴스 생성
            cipher = Cipher.getInstance(INSTANCE_TYPE);
        }catch(Exception e){
            log.error("Aes128CipherUtil Exception. {}", e);
            throw new Exception("Aes128CipherUtil Error");
        }
    }

    /**
     * <pre>
     * AES 암호화
     * </pre>
     *
     * @param cryptData
     * @return
     * @throws Exception
     */
    public byte[] encryptAes(byte[] cryptData) throws Exception{
        try{
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            // byte[] encryted = cipher.doFinal(cryptData.getBytes(charSets));
            byte[] encryted = cipher.doFinal(cryptData);
            return Base64.getEncoder().encode(encryted);
            // return new String(Base64.getEncoder().encode(encryted),
            // charSets);
        }catch(Exception e){
            log.error("encryptAes Exception. {}", e);
            throw new Exception("encryptAes Error");
        }
    }

    /**
     * <pre>
     * AES 복호화
     * </pre>
     *
     * @param cryptData
     * @return
     * @throws Exception
     */
    public byte[] decryptAes(byte[] cryptData) throws Exception{
        try{
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decoded = Base64.getDecoder().decode(cryptData);
            return cipher.doFinal(decoded);
            // return new String(cipher.doFinal(decoded), charSets);
        }catch(Exception e){
            log.error("decryptAes Exception. {}", e);
            throw new Exception("decryptAes Error");
        }
    }
}
