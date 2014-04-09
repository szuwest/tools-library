package org.szuwest.utils;

/**
 * Created by szuwest on 13-9-16.
 */

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

import my.base.util.LogUtils;

/**
 *
 */
public class Mac {
    public String accessKey;
    public String secretKey;

    public Mac(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public static byte[] sign(String dataString, String secretKey) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            byte[] secretKeyData = secretKey.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyData, "HmacSHA1");
            mac.init(secretKeySpec);
            return mac.doFinal(dataString.getBytes());
        } catch (InvalidKeyException e) {
            LogUtils.d(Mac.class.getSimpleName(), "invalid key!");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            LogUtils.d(Mac.class.getSimpleName(), "no algorithm called HmacSHA1!");
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * makes a download token.
     * @param data
     * @return
     */
    public String sign(byte[] data) {
        System.out.println("data : " + new String(data));
        javax.crypto.Mac mac = null;
        try {
            mac = javax.crypto.Mac.getInstance("HmacSHA1");
            byte[] secretKey = this.secretKey.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA1");
            mac.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            LogUtils.d(Mac.class.getSimpleName(),"invalid key!");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            LogUtils.d(Mac.class.getSimpleName(), "no algorithm called HmacSHA1!");
            e.printStackTrace();
        }

        String encodedSign = EncrUtil.urlsafeEncodeString(mac.doFinal(data));
        return this.accessKey + ":" + encodedSign;
    }

    /**
     * makes a upload token.
     * @param data
     * @return
     */
    public String signWithData(byte[] data) {
        byte[] accessKey = this.accessKey.getBytes();
        byte[] secretKey = this.secretKey.getBytes();

        try {
            byte[] policyBase64 = EncrUtil.urlsafeEncodeBytes(data);

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "HmacSHA1");
            mac.init(keySpec);

            byte[] digest = mac.doFinal(policyBase64);
            byte[] digestBase64 = EncrUtil.urlsafeEncodeBytes(digest);
            byte[] token = new byte[accessKey.length + 30 + policyBase64.length];

            System.arraycopy(accessKey, 0, token, 0, accessKey.length);
            token[accessKey.length] = ':';
            System.arraycopy(digestBase64, 0, token, accessKey.length + 1,
                    digestBase64.length);
            token[accessKey.length + 29] = ':';
            System.arraycopy(policyBase64, 0, token, accessKey.length + 30,
                    policyBase64.length);

            return new String(token);
        } catch (Exception e) {
            LogUtils.d(Mac.class.getSimpleName(), "Fail to sign with data!");
        }
        return null;
    }

    /*
     * makes an access token.
     */
    public String signRequest(HttpPost post) {
        URI uri = post.getURI();
        String path = uri.getRawPath();
        String query = uri.getRawQuery();
        HttpEntity entity = post.getEntity();

        byte[] secretKey = this.secretKey.getBytes();
        javax.crypto.Mac mac = null;
        try {
            mac = javax.crypto.Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            LogUtils.d(Mac.class.getSimpleName(), "No algorithm called HmacSHA1!");
            e.printStackTrace();
        }

        SecretKeySpec keySpec = new SecretKeySpec(secretKey, "HmacSHA1");
        try {
            mac.init(keySpec);
            mac.update(path.getBytes());
        } catch (InvalidKeyException e) {
            LogUtils.d(Mac.class.getSimpleName(), "You've passed an invalid secret key!");
        } catch (IllegalStateException e) {
           e.printStackTrace();
        }

        if (query != null && query.length() != 0) {
            mac.update((byte) ('?'));
            mac.update(query.getBytes());
        }
        mac.update((byte) '\n');
        if (entity != null) {
            org.apache.http.Header ct = entity.getContentType();
            if (ct != null
                    && ct.getValue() == "application/x-www-form-urlencoded") {
                ByteArrayOutputStream w = new ByteArrayOutputStream();
                try {
                    entity.writeTo(w);
                } catch (IOException e) {
                   e.printStackTrace();
                }
                mac.update(w.toByteArray());
            }
        }

        byte[] digest = mac.doFinal();
        byte[] digestBase64 = EncrUtil.urlsafeEncodeBytes(digest);

        StringBuffer b = new StringBuffer();
        b.append(this.accessKey);
        b.append(':');
        b.append(new String(digestBase64));

        return b.toString();
    }

}
