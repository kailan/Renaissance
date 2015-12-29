package net.hungerstruck.renaissance.util

import com.google.common.io.Files
import java.io.File
import java.io.IOException

/**
 * MapUtils ported to Kotlin.
 *
 * Created by molenzwiebel on 22-12-15.
 */
object FileUtil {
    fun copy(source: File, destination: File, force: Boolean = false) {
        if (!source.exists()) {
            throw IllegalArgumentException("Source (" + source.path + ") doesn't exist.")
        }

        if (!force && destination.exists()) {
            throw IllegalArgumentException("Destination (" + destination.path + ") exists.")
        }

        if (source.isDirectory) {
            copyDirectory(source, destination)
        } else {
            copyFile(source, destination)
        }
    }

    fun copyWorldFolder(from: File, to: File) {
        FileUtil.copy(from, to)
        FileUtil.delete(File(to, "session.lock"))
        FileUtil.delete(File(to, "uid.dat"))
        FileUtil.delete(File(to, "players"))
    }

    private fun copyDirectory(source: File, destination: File) {
        if (!destination.mkdirs()) {
            throw IOException("Failed to create destination directories")
        }

        val files = source.listFiles()

        for (file in files) {
            if (file.isDirectory) {
                copyDirectory(file, File(destination, file.name))
            } else {
                copyFile(file, File(destination, file.name))
            }
        }
    }

    private fun copyFile(source: File, destination: File) {
        Files.copy(source, destination)
    }

    fun delete(f: File) {
        if (!f.exists())
            return

        if (f.isDirectory) {
            for (c in f.listFiles()!!) {
                delete(c)
            }
        }

        f.delete()
    }
}
