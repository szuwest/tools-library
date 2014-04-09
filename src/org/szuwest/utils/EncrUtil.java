package org.szuwest.utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;

public class EncrUtil {
	private static final int RADIX = 16;
	private static final String SEED = "0933910847463829232312312";

	public static final String encrypt(String password) {
		if (password == null)
			return "";
		if (password.length() == 0)
			return "";

		BigInteger bi_passwd = new BigInteger(password.getBytes());

		BigInteger bi_r0 = new BigInteger(SEED);
		BigInteger bi_r1 = bi_r0.xor(bi_passwd);

		return bi_r1.toString(RADIX);
	}

	public static final String decrypt(String encrypted) {
		if (encrypted == null)
			return "";
		if (encrypted.length() == 0)
			return "";

		BigInteger bi_confuse = new BigInteger(SEED);

		try {
			BigInteger bi_r1 = new BigInteger(encrypted, RADIX);
			BigInteger bi_r0 = bi_r1.xor(bi_confuse);

			return new String(bi_r0.toByteArray());
		} catch (Exception e) {
			return "";
		}
	}

	/*public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
*/
	public static String paramsSignature(JSONObject jsonObject) throws JSONException {
		int cnt = jsonObject.length();
		String[] keys = new String[cnt];
		int i = 0;
		for (Iterator<String> iterator = jsonObject.keys(); iterator.hasNext();) {
			String key = iterator.next();
			keys[i] = key;
			i++;
		}
		Arrays.sort(keys);
		String request_str = "";
		for (i = 0; i < keys.length; ++i) {
			request_str += HttpUrlEncoder.encode(keys[i]) + "=" + HttpUrlEncoder.encode(jsonObject.getString(keys[i])) + "&";
		}
//		request_str += AppModel.getInstance().getSigKey();
		return EncrUtil.MD5(request_str);
	}

	public static String MD5(String txt) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(txt.getBytes("UTF-8"));
			StringBuffer buf = new StringBuffer();
			for (byte b : md.digest()) {
				buf.append(String.format("%02x", b & 0xff));
			}
			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

    //**********qiniu sdk ***********************//

    public static byte[] urlsafeEncodeBytes(byte[] src) {
        if (src.length % 3 == 0) {
            return encodeBase64Ex(src);
        }

        byte[] b = encodeBase64Ex(src);
        if (b.length % 4 == 0) {
            return b;
        }

        int pad = 4 - b.length % 4;
        byte[] b2 = new byte[b.length + pad];
        System.arraycopy(b, 0, b2, 0, b.length);
        b2[b.length] = '=';
        if (pad > 1) {
            b2[b.length + 1] = '=';
        }
        return b2;
    }

    public static String urlsafeEncodeString(byte[] src) {
        return new String(urlsafeEncodeBytes(src));
    }

    public static String urlsafeEncode(String text) {
        return new String(urlsafeEncodeBytes(text.getBytes()));
    }

    // replace '/' with '_', '+" with '-'
    private static byte[] encodeBase64Ex(byte[] src) {
        // urlsafe version is not supported in version 1.4 or lower.
//        byte[] b64 = Base64.encodeBase64(src);
        byte[] b64 = new String(Base64Coder.encode(src)).getBytes();

        for (int i = 0; i < b64.length; i++) {
            if (b64[i] == '/') {
                b64[i] = '_';
            } else if (b64[i] == '+') {
                b64[i] = '-';
            }
        }
        return b64;
    }

}
