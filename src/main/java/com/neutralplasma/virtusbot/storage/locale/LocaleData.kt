package com.neutralplasma.virtusbot.storage.locale

import java.util.*

class LocaleData(var allLocales: HashMap<String, String>) {
    fun getLocale(locale: String?): String? {
        try {
            val data = allLocales[locale]
            if (data != null) {
                return data
            }
        } catch (error: NullPointerException) {
            return null
        }
        return null
    }

    fun updateLocale(locale: String, localedata: String) {
        allLocales[locale] = localedata
    }

}