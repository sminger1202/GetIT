package com.android.getit.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class JsonUtils {
	public static boolean isNull(String str) {
		if (str == null || str.equals(""))
			return true;
		return false;
	}

	public static String getJsonString(JSONObject object, String name) {
		try {
			if (object==null) {
				return "";
			}
			return !object.isNull(name) ? object.getString(name) : "";
		} catch (JSONException e) {
			Log.e("F.getJsonValue()", e.toString());
			return "";
		}
	}

	public static float getJsonDoubleHalf(JSONObject object, String name) {
		try {
			return !object.isNull(name) ? (float) object.getDouble(name) / 2.0f
					: 0;
		} catch (JSONException e) {
			Log.e("F.getJsonDouble()", e.toString());
			return 0;
		}

	}

	public static float getJsonDouble(JSONObject object, String name) {
		try {
			return !object.isNull(name) ? (float) object.getDouble(name) : -1;
		} catch (JSONException e) {
			Log.e("F.getJsonDouble()", e.toString());
			return -1;
		}

	}
	public static long getJsonLong(JSONObject object, String name) {
		try {
			return !object.isNull(name) ?  object.getLong(name) : -1;
		} catch (JSONException e) {
			Log.e("F.getJsonLong()", e.toString());
			return -1;
		}

	}

	public static int getJsonInt(JSONObject object, String name) {
		try {
			return !object.isNull(name) ? object.getInt(name) : 0;
		} catch (JSONException e) {
			Log.e("F.getJsonInt()", e.toString());
			return 0;
		}

	}
	
	public static int getInt(JSONObject object, String name,int fallback) {
		return  object.optInt(name, fallback);
		
	}

	public static boolean getJsonBoolean(JSONObject object, String name) {
		try {
			return !object.isNull(name) ? object.getBoolean(name) : false;
		} catch (JSONException e) {
			Log.e("F.getJsonBoolean()", e.toString());
			return false;
		}
	}
	
	public static JSONArray getJsonArray(JSONObject object, String name) {
		try {
			return !object.isNull(name) ? object.getJSONArray(name) : null;
		} catch (JSONException e) {
			Log.e("F.getJsonBoolean()", e.toString());
			return null;
		}
	}
	
	public static JSONObject getJsonObject(JSONObject object, String name) {
		try {
			return !object.isNull(name) ? object.getJSONObject(name) : null;
		} catch (JSONException e) {
			Log.e("F.getJsonBoolean()", e.toString());
			return null;
		}
	}
	
	public static JSONObject getJsonObject(JSONArray object,int  index) {
		JSONObject  object2=object.optJSONObject(index);
		return  object2;
	}
	
	public static String getString(JSONArray object,int  index) {
		return  object.optString(index);
	}
	
	public static JSONObject loadJson(String  json) {
		try {
			return !TextUtils.isEmpty(json) ?  new JSONObject(json): null;
		} catch (JSONException e) {
			Log.e("F.getJsonBoolean()", e.toString());
			return null;
		}
	}
}
