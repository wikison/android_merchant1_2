package com.zemult.merchant.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

public class EmojiParser {
	private static final String TAG = EmojiParser.class.getSimpleName();
	// emoji表情的格式如[e]1164c[/e]
	private static final String REGEX_STR = "\\[e\\](.*?)\\[/e\\]";
	private EmojiParser(Context mContext) {
		readMap(mContext);
	}

	private static Context mContext;
	private HashMap<List<Integer>, String> convertMap = new HashMap<List<Integer>, String>();
	private HashMap<String, ArrayList<String>> emoMap = new HashMap<String, ArrayList<String>>();
	private static EmojiParser mParser;

	public static EmojiParser getInstance(Context context) {
		mContext = context;
		if (mParser == null) {
			mParser = new EmojiParser(mContext);
		}
		return mParser;
	}

	public HashMap<String, ArrayList<String>> getEmoMap() {
		return emoMap;
	}

	public void readMap(Context mContext) {
		if (convertMap == null || convertMap.size() == 0) {
			convertMap = new HashMap<List<Integer>, String>();
			XmlPullParser xmlpull = null;
			String fromAttr = null;
			String key = null;
			ArrayList<String> emos = null;
			try {
				XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
				xmlpull = xppf.newPullParser();
				InputStream stream = mContext.getAssets().open("emoji.xml");
				xmlpull.setInput(stream, "UTF-8");
				int eventCode = xmlpull.getEventType();
				while (eventCode != XmlPullParser.END_DOCUMENT) {
					switch (eventCode) {
						case XmlPullParser.START_DOCUMENT: {
							break;
						}
						case XmlPullParser.START_TAG: {
							if (xmlpull.getName().equals("key")) {
								emos = new ArrayList<String>();
								key = xmlpull.nextText();
							}
							if (xmlpull.getName().equals("e")) {
								fromAttr = xmlpull.nextText();
								emos.add(fromAttr);
								List<Integer> fromCodePoints = new ArrayList<Integer>();
								if (fromAttr.length() > 6) {
									String[] froms = fromAttr.split("\\_");
									for (String part : froms) {
										fromCodePoints.add(Integer.parseInt(part, 16));
									}
								} else {
									fromCodePoints.add(Integer.parseInt(fromAttr, 16));
								}
								convertMap.put(fromCodePoints, fromAttr);
							}
							break;
						}
						case XmlPullParser.END_TAG: {
							if (xmlpull.getName().equals("dict")) {
								emoMap.put(key, emos);
							}
							break;
						}
						case XmlPullParser.END_DOCUMENT: {
							Log.d("", "parse emoji complete");
							break;
						}
					}
					eventCode = xmlpull.next();
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString(), e);
			}
		}
	}

	/**
	 * 将表情转为unicode字符
	 * @param input
	 * @return
	 */
	public String parseEmoji(String input) {
		if (input == null || input.length() <= 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		int[] codePoints = toCodePointArray(input);
		List<Integer> key = null;
		for (int i = 0; i < codePoints.length; i++) {
			key = new ArrayList<Integer>();
			if (i + 1 < codePoints.length) {
				key.add(codePoints[i]);
				key.add(codePoints[i + 1]);
				if (convertMap.containsKey(key)) {
					String value = convertMap.get(key);
					if (value != null) {
						result.append("[e]" + value + "[/e]");
					}
					i++;
					continue;
				}
			}
			key.clear();
			key.add(codePoints[i]);
			if (convertMap.containsKey(key)) {
				String value = convertMap.get(key);
				if (value != null) {
					result.append("[e]" + value + "[/e]");
				}
				continue;
			}
			result.append(Character.toChars(codePoints[i]));
		}
		return result.toString();
	}

	private int[] toCodePointArray(String str) {
		char[] ach = str.toCharArray();
		int len = ach.length;
		int[] acp = new int[Character.codePointCount(ach, 0, len)];
		int j = 0;
		for (int i = 0, cp; i < len; i += Character.charCount(cp)) {
			cp = Character.codePointAt(ach, i);
			acp[j++] = cp;
		}
		return acp;
	}

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 */
	public void dealExpression(SpannableString spannableString, Pattern patten, int start)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			Log.d("Key", key);
			if (matcher.start() < start) {
				continue;
			}

			try {
				Bitmap mBitmap = BitmapFactory.decodeStream(mContext.getAssets().open("emoji/" + "emoji_"
						+ key.substring(key.indexOf("]") + 1, key.lastIndexOf("[")) + ".png"));

				//得到drawable对象，即所要插入的图片
				Drawable d = new BitmapDrawable(mBitmap);
				d.setBounds(0, 0, 40, 40);

				ImageSpan imageSpan = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);

				int end = matcher.start() + key.length();
					spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				if (end < spannableString.length()) {
					dealExpression(spannableString, patten, end);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @desc <pre>
	 * 解析字符串中的表情字符串替换成表情图片
	 * </pre>
	 * @author Weiliang Hu
	 * @date 2013-12-17
	 * @param str
	 * @return
	 */
	public SpannableString getExpressionString(String str) {
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(REGEX_STR, Pattern.CASE_INSENSITIVE);
		try {
			dealExpression(spannableString, sinaPatten, 0);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return spannableString;
	}
}
