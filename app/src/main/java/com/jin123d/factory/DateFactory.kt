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
        if (type == ApiType.SINA) {
            return SinaDateFactory()
        } else {
            return NmcDateFactory()
        }
    }

}

