package top.moxel.plugin

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.moxel.plugin.infrastructure.io.LoggingConfiguration
import top.moxel.plugin.infrastructure.platform.Platform

class MoxelCore : KoinComponent {
    private val logger = KotlinLogging.logger {}
    private val platform by inject<Platform>()

    init {
        LoggingConfiguration()
        logger.debug { "Moxel Core initialized" }

        logger.debug { "Moxel Core started" }
        logger.info { platform.name + " " + platform.target + " " + platform.edition }

//        val result = luaEngine.eval("print('Hello from Lua!'); return 42")
//        println("Result: $result")
//
//        val exp = luaEngine.getCompiledScript<(x: Int, y: Int)->Int>("""
//                function multiply(x, y)
//                    return x * y
//                end
//
//                function add(x, y)
//                    return x + y
//                end
//            """.trimIndent(), "multiply")
//        println ( "Exp: ${exp(4, 5)}" )
    }
}