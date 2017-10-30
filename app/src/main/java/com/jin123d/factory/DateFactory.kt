package com.jin123d.factory

import com.jin123d.weathercloud.IDateFactory

/**
 * Created by jin123d on 10/1 0001.
 **/
object DateFactory {

    enum class ApiType {
        NMC, SINA, PMSC, CMA
    }

    fun create(type: ApiType): IDateFactory {
        return when (type) {
            ApiType.SINA -> {
                SinaDateFactory()
            }
            ApiType.NMC -> {
                NmcDateFactory()
            }
            ApiType.PMSC -> {
                PmscDateFactory()
            }
            ApiType.CMA -> {
                CmaDateFactory()
            }

        }
    }

}

