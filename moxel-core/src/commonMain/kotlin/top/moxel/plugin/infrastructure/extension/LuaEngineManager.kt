package top.moxel.plugin.infrastructure.extension

import org.koin.core.annotation.Single
import top.moxel.plugin.annotation.lua.LuaEngineType
import top.moxel.plugin.annotation.lua.LuaLibDeclaration
import top.moxel.plugin.ksp.generated.LuaBindingList

@Single
class LuaEngineManager {
    private val engineMap = mutableMapOf<LuaEngineId, LuaEngine>()
    private val libsMap = mutableMapOf<LuaEngineType, MutableList<LuaLib>>()

    fun registerKspLib() {
        LuaBindingList.list.forEach {
            registerLib(it)
        }
    }

    fun registerLib(definition: LuaLibDeclaration) {
        val list = libsMap[definition.type] ?: mutableListOf()
        list.add(
            LuaLib(
                definition.group,
                LuaEngine.buildLuaFunctions(definition.bindings),
                definition.group.isBlank()
            )
        )
        libsMap[definition.type] = list
    }

    fun getOrCreate(id: LuaEngineId): LuaEngine {
        if (!engineMap.containsKey(id)) {
            engineMap[id] = LuaEngine()
        }
        val engine = engineMap[id]!!
        libsMap[id.type]?.let { engine.newLibs(it) }
        return engine
    }

    fun reset(id: LuaEngineId): LuaEngine {
        val engine = getOrCreate(id)
        return engine
    }

    fun delete(id: LuaEngineId) {
        engineMap[id]?.close()
        engineMap.remove(id)
    }

    fun disposeExtensions() {
        engineMap.forEach { (id, value)->
            if(id.type == LuaEngineType.EXTENSION) {
                value.close()
                delete(id)
            }
        }
        libsMap[LuaEngineType.EXTENSION]?.let { LuaEngine.disposeLibs(it) }
    }

    fun disposeMemory() {
        engineMap.forEach { (id, value)->
            value.close()
            delete(id)
        }
        LuaEngineType.entries.forEach { type ->
            libsMap[type]?.let { LuaEngine.disposeLibs(it) }
        }
    }
}