package org.szuwest.utils;

import java.io.*;

/**
 * Created by zhongchao on 13-6-29.
 */
public class SerializableUtil {

	/**
	 * Read the object from Base64 string.
	 *
	 * @param s 序列化String
	 */
	public static Object fromString(String s) {
		Object o = null;
		try {
			byte[] data = Base64Coder.decode(s);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			o = ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * Write the object to a Base64 string.
	 */
	public static String toString(Serializable o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.close();
			return new String(Base64Coder.encode(baos.toByteArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
