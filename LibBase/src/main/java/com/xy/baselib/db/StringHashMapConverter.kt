package com.xy.baselib.db

import org.greenrobot.greendao.converter.PropertyConverter
import org.json.JSONException
import org.json.JSONObject

class StringHashMapConverter : PropertyConverter<HashMap<String, String>?, String?> {
    override fun convertToEntityProperty(databaseValue: String?): HashMap<String, String>? {
        return if (databaseValue == null) {
            null
        } else {
            val hashMap = HashMap<String, String>()
            try {
                val jsonObject = JSONObject(databaseValue)
                val it: Iterator<*> = jsonObject.keys()
                while (it.hasNext()) {
                    val key = it.next().toString()
                    hashMap[key] = jsonObject[key].toString()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            hashMap
        }
    }

    override fun convertToDatabaseValue(hashMap: HashMap<String, String>?): String? {
        val jsonObject = JSONObject()
        if (hashMap != null) {
            for (key in hashMap.keys) {
                try {
                    jsonObject.put(key, hashMap[key])
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        return jsonObject.toString()
    }
}