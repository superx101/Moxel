package top.moxel.plugin.infrastructure.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import top.moxel.plugin.infrastructure.environment.Environment
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile

actual object NativeExtensionLoader {
    private val logger = KotlinLogging.logger {}

    /***
     * Load jar file
     */
    actual suspend fun load(filepath: String) {
        val jarFile = File(filepath)
        if ((!jarFile.exists()) || jarFile.extension != "jar") {
            logger.warn { "Jar file $filepath not found or not a valid JAR file." }
            return
        }

        try {
            val url = jarFile.toURI().toURL()
            val classLoader = URLClassLoader(arrayOf(url), NativeExtensionLoader::class.java.classLoader)

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
               logger.error {"Main-Class not found in JAR manifest."  }
            }
        } catch (e: Exception) {
            logger.error{ "Error loading or executing class from $jarFile: ${e.message}" }
        }
    }

    /***
     * Load all jar files
     */
    actual suspend fun loadAll() {
        val dataRoot = Environment.dataRoot
        val extensionsFolder = File(dataRoot, "extension")

        val jarFiles = extensionsFolder.listFiles { _, name -> name.endsWith(".jar") }
        if (jarFiles != null && jarFiles.isNotEmpty()) {
            jarFiles.forEach { jarFile ->
                load(jarFile.name)
            }
        } else {
            logger.warn { "No JAR files found in the extensions folder." }
        }
    }
}