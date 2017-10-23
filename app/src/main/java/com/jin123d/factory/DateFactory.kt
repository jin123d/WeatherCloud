package com.jin123d.factory

import com.jin123d.weathercloud.IDateFactory

/**
 * Created by jin123d on 10/1 0001.
 **/
object DateFactory {

    enum class ApiType {
        NMC, SINA
    }

    fun create(type: ApiType): IDateFactory {
        return if (type == ApiType.SINA) {
            SinaDateFactory()
        } else {
            NmcDateFactory()
        }
    }

}

