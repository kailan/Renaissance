package net.hungerstruck.renaissance.xml.module

import net.hungerstruck.renaissance.match.RMatch
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

/**
 * Created by molenzwiebel on 21-12-15.
 */
class RModuleRegistry {
    companion object {
        val MODULES: MutableList<RModuleInfo> = arrayListOf()

        public inline fun <reified T : RModule> register() {
            MODULES.add(RModuleInfo.of(T::class))
        }
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Dependencies(vararg val value: KClass<out RModule> = arrayOf())

data class RModuleInfo(val clazz: KClass<out RModule>, val dependencies: Array<RModuleInfo>, val constructor: Constructor<out RModule>) {
    companion object {
        val INSTANCES: MutableMap<KClass<out RModule>, RModuleInfo> = hashMapOf()

        public fun of(clazz: KClass<out RModule>): RModuleInfo {
            if (INSTANCES[clazz] != null) return INSTANCES[clazz]!!

            val dependencies = clazz.java.annotations.filterIsInstance<Dependencies>().firstOrNull()?.value ?: arrayOf()
            INSTANCES.put(clazz, RModuleInfo(clazz, dependencies.map { RModuleInfo.of(it) }.toTypedArray(), clazz.java.getConstructor(RMatch::class.java, RModuleContext::class.java)))
            return of(clazz)
        }
    }
}