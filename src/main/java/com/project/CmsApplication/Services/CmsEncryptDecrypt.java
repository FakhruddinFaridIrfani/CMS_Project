package com.project.CmsApplication.Services;

import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


@Component
public class CmsEncryptDecrypt {

    public static final String TAG = "CMS";
    private static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static String ALGORITHM = "AES";
    private static String DIGEST = "MD5";

    private static String userPassword = "monasbelumjadilo";
    private static Cipher _cipher;
    private static SecretKey _password;
    private static IvParameterSpec _IVParamSpec;

    //16-byte private key
    private static byte[] IV = "gtbobdqtqhbxgrsf".getBytes();
//
//    public CmsEncryptDecrypt(String password) {
//
//        try {
//
//            //Encode digest
//            MessageDigest digest;
//            digest = MessageDigest.getInstance(DIGEST);
//            _password = new SecretKeySpec(digest.digest(password.getBytes()), ALGORITHM);
//
//            //Initialize objects
//            _cipher = Cipher.getInstance(TRANSFORMATION);
//            _IVParamSpec = new IvParameterSpec(IV);
//
//        } catch (NoSuchAlgorithmException e) {
//            System.out.println(TAG + "No such algorithm " + ALGORITHM + e);
//        } catch (NoSuchPaddingException e) {
//            System.out.println(TAG + "No such padding PKCS7" + e);
//        }
//    }

    public static String encrypt(byte[] text) {
        byte[] encryptedData;

        try {
            MessageDigest digest;
            digest = MessageDigest.getInstance(DIGEST);
            _password = new SecretKeySpec(digest.digest(userPassword.getBytes()), ALGORITHM);

            //Initialize objects
            _cipher = Cipher.getInstance(TRANSFORMATION);
            _IVParamSpec = new IvParameterSpec(IV);

            _cipher.init(Cipher.ENCRYPT_MODE, _password, _IVParamSpec);
            encryptedData = _cipher.doFinal(text);


        } catch (InvalidKeyException e) {
            System.out.println(TAG + "Invalid key  (invalid encoding, wrong length, uninitialized+etc)." + e);
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            System.out.println(TAG + "Invalid or inappropriate algorithm parameters for " + ALGORITHM + e);
            return null;
        } catch (IllegalBlockSizeException e) {
            System.out.println(TAG + "The length of data provided to a block cipher is incorrect" + e);
            return null;
        } catch (BadPaddingException e) {
            System.out.println(TAG + "The input data but the data is not padded properly." + e);
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        return new String(Base64.getEncoder().encode(encryptedData));

    }

    public static String decrypt(String text) {
        byte[] decryptedVal;
        try {
            MessageDigest digest;
            digest = MessageDigest.getInstance(DIGEST);
            _password = new SecretKeySpec(digest.digest(userPassword.getBytes()), ALGORITHM);

            //Initialize objects
            _cipher = Cipher.getInstance(TRANSFORMATION);
            _IVParamSpec = new IvParameterSpec(IV);


            _cipher.init(Cipher.DECRYPT_MODE, _password, _IVParamSpec);

            byte[] decodedValue = Base64.getDecoder().decode(text.getBytes());
            decryptedVal = _cipher.doFinal(decodedValue);



        } catch (InvalidKeyException e) {
            System.out.println(TAG + "Invalid key  (invalid encoding, wrong length, uninitialized+etc)." + e);
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            System.out.println(TAG + "Invalid or inappropriate algorithm parameters for " + ALGORITHM + e);
            return null;
        } catch (IllegalBlockSizeException e) {
            System.out.println(TAG + "The length of data provided to a block cipher is incorrect" + e);
            return null;
        } catch (BadPaddingException e) {
            System.out.println(TAG + "The input data but the data is not padded properly." + e);
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return new String(decryptedVal);

    }

}
