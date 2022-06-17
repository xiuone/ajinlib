package com.xy.baselib.db

import com.alibaba.fastjson.JSON
import org.greenrobot.greendao.converter.PropertyConverter

class StringConverter : PropertyConverter<List<String>?, String?> {
    override fun convertToEntityProperty(databaseValue: String?): List<String>? {
        return if (databaseValue == null) {
            null
        } else {
            JSON.parseArray(databaseValue,
                String::class.java)
        }
    }

    override fun convertToDatabaseValue(entityProperty: List<String>?): String? {
        return if (entityProperty == null) {
            null
        } else {
            JSON.toJSONString(entityProperty)
        }
    }
}