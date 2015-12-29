package net.hungerstruck.renaissance.config.confvar

import net.hungerstruck.renaissance.config.RConfig
import pw.kmp.confvar.Confvar
import pw.kmp.confvar.util.Utils

/**
 * Created by kailan on 29/12/2015.
 */
class RenaissanceDebug : Confvar<Boolean> {

    override fun getName(): String? {
        return "hs_debug"
    }

    override fun set(p0: String?) {
        RConfig.General.debugging = Utils.parseBoolean(p0)
    }

    override fun get(): Boolean? {
        return RConfig.General.debugging
    }

}