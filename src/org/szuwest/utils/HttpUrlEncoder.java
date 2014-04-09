package org.szuwest.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import my.base.util.LogUtils;

/**
 * Created by venciallee on 13-9-13.
 */
public class HttpUrlEncoder {

	private static String CHARSET = "UTF-8";
	private static final Map<String, String> ENCODING_RULES;

	static
	{
		Map<String, String> rules = new HashMap<String, String>();
		rules.put("*", "%2A");
		rules.put("+", "%20");
		rules.put("%7E", "~");
		ENCODING_RULES = Collections.unmodifiableMap(rules);
	}

	public static String encode(String plain)
	{
		if(null == plain) return null;
		String encoded = "";
		try
		{
			encoded = URLEncoder.encode(plain, CHARSET);
		}
		catch (UnsupportedEncodingException uee)
		{
			LogUtils.d("Charset not found while encoding string: " + CHARSET, uee.getMessage());
		}
		for(Map.Entry<String, String> rule : ENCODING_RULES.entrySet())
		{
			encoded = applyRule(encoded, rule.getKey(), rule.getValue());
		}
		return encoded;
	}

	private static String applyRule(String encoded, String toReplace, String replacement)
	{
		return encoded.replaceAll(Pattern.quote(toReplace), replacement);
	}

}
