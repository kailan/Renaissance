package net.hungerstruck.renaissance.xml.builder

import java.io.File
import java.net.URLClassLoader

class MapBuilderClassLoader(path: File) : URLClassLoader(arrayOf(path.toURI().toURL())) {
    override fun loadClass(name: String, resolve: Boolean): Class<*>? {
        if (name.startsWith("Map")) {
            return super.findClass(name)
        }

        val method = ClassLoader::class.java.getDeclaredMethod("loadClass", String::class.java, Boolean::class.java)
        method.isAccessible = true
        return method.invoke(javaClass.classLoader, name, resolve) as Class<*>?
    }

    override fun findClass(name: String): Class<*>? {
        if (name.startsWith("Map")) {
            return super.findClass(name)
        }

        val method = ClassLoader::class.java.getDeclaredMethod("findClass", String::class.java)
        method.isAccessible = true
        return method.invoke(javaClass.classLoader, name) as Class<*>?
    }
}