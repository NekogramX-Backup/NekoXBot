package nekox.ext

import nekox.Launcher
import org.dizitart.no2.Document
import org.dizitart.no2.UpdateOptions
import org.dizitart.no2.filters.Filters

val settingColl by lazy { Launcher.NITRITE.getCollection("settings") }

fun confString(key: String, default: String? = null): String? {

    return runCatching {

        settingColl.find(Filters.eq("key", key)).first().get("value", String::class.java)

    }.getOrNull() ?: default

}

fun confInt(key: String, default: Int? = null): Int? {

    return runCatching {

        settingColl.find(Filters.eq("key", key)).first().get("value", Number::class.java).toInt()

    }.getOrNull() ?: default


}

fun confLong(key: String, default: Long? = null): Long? {

    return runCatching {

        settingColl.find(Filters.eq("key", key)).first().get("value", Number::class.java).toLong()

    }.getOrNull() ?: default

}

fun putConf(key: String, value: Any) {

    settingColl.update(Filters.eq("key", key)
            , Document().apply {

        put("key", key)
        put("value", value)

    }, UpdateOptions.updateOptions(true))

}