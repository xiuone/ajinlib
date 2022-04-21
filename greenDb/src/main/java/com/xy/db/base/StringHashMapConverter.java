package com.xy.db.base;


import org.greenrobot.greendao.converter.PropertyConverter;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;

public class StringHashMapConverter implements PropertyConverter<HashMap<String,String>, String> {

    @Override
    public HashMap<String,String> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            HashMap<String,String> hashMap = new HashMap<>();
            try {
                JSONObject jsonObject = new JSONObject(databaseValue);
                Iterator it = jsonObject.keys();
                while(it.hasNext()){
                    String key = it.next().toString();
                    String value = (String)jsonObject.get(key).toString();
                    hashMap.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return hashMap;
        }
    }

    @Override
    public String convertToDatabaseValue(HashMap<String, String> hashMap) {
        JSONObject jsonObject = new JSONObject();
        if (hashMap != null) {
            for (String key : hashMap.keySet()) {
                try {
                    jsonObject.put(key, hashMap.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject.toString();
    }
}
