package top.moxel.plugin

import kotlinx.coroutines.runBlocking
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import top.moxel.plugin.infrastructure.DI

object FabricMod : ModInitializer {
    private val logger = LoggerFactory.getLogger("template-mod")

    override fun onInitialize() = runBlocking {
// 		DI.registerModule()
        DI.startApplication()

        val moxel = Moxel()
        logger.info("Hello Fabric world!")
    }
}
