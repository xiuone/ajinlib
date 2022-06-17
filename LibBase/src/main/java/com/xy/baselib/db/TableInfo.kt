package com.xy.baselib.db

class TableInfo {
    var cid = 0
    var name: String? = null
    var type: String? = null
    var notnull = false
    var dfltValue: String? = null
    var pk = false
    override fun equals(o: Any?): Boolean {
        return (this === o || o != null && javaClass == o.javaClass && name == (o as TableInfo).name)
    }

    override fun toString(): String {
        return "TableInfo{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", notnull=" + notnull +
                ", dfltValue='" + dfltValue + '\'' +
                ", pk=" + pk +
                '}'
    }
}