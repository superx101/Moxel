package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import top.moxel.plugin.infrastructure.WarningException
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile

@Single
actual open class NativeExtensionLoader : ExtensionLoader, KoinComponent {
    private val logger = KotlinLogging.logger {}

    actual override fun load(path: Path) {
        val jarFile = File(path.toString())
        if ((!jarFile.exists()) || jarFile.extension != "jar") {
            logger.warn { "Jar file $path not found or not a valid JAR file." }
            return
        }

        try {
            val url = jarFile.toURI().toURL()
            val classLoader =
                URLClassLoader(arrayOf(url), NativeExtensionLoader::class.java.classLoader)

            // Read the main class from the JAR manifest
            val jar = JarFile(jarFile)
            val manifest = jar.manifest
            val mainClassName = manifest.mainAttributes.getValue("Main-Class")

            // Execute the main class
            if (mainClassName != null) {
                val clazz = classLoader.loadClass(mainClassName)
                val mainMethod = clazz.getMethod("main", Array<String>::class.java)
                mainMethod.invoke(null, arrayOf<String>())
                logger.debug {
                    "Successfully executed the main class: $mainClassName from " +
                        "$jarFile"
                }
            } else {
                throw WarningException("Main-Class not found in JAR manifest.")
            }
        } catch (e: Exception) {
            throw WarningException("Error loading or executing class from $jarFile: ${e.message}")
        }
    }

    actual override fun loadAll() {
        commonLoadAll(".jar")
    }

    actual override fun freeAll() {
        // do nothing
    }
}
