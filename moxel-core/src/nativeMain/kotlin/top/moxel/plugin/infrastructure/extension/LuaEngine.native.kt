package top.moxel.plugin.infrastructure.extension

import cnames.structs.lua_State
import kotlinx.cinterop.*
import org.lua.*
import top.moxel.plugin.annotation.lua.LuaBinding
import top.moxel.plugin.annotation.lua.LuaBindingFunction
import top.moxel.plugin.infrastructure.common.ActualWrapper

@OptIn(ExperimentalForeignApi::class)
data class LuaCFunctionWrapper(
    override var value: StableRef<lua_CFunction>
) : ActualWrapper<StableRef<lua_CFunction>>

@OptIn(ExperimentalForeignApi::class)
@Suppress("ACTUAL_WITHOUT_EXPECT")
actual typealias LuaCFunctionRef = LuaCFunctionWrapper

@OptIn(ExperimentalForeignApi::class)
actual open class LuaEngine {
    private var luaState: CPointer<lua_State> = createState()

    actual companion object {
        @OptIn(ExperimentalForeignApi::class)
        internal inline fun luaUpValueIndex(i: Int): Int {
            return LUA_REGISTRYINDEX - i
        }

        internal inline fun luaPop(luaState: CPointer<lua_State>,  i: Int) {
            lua_settop(luaState, -i-1)
        }

        @OptIn(ExperimentalForeignApi::class)
        internal fun luaToKotlin(luaState: CPointer<lua_State>, index: Int): Any? {
            return when (lua_type(luaState, index)) {
                LUA_TNIL -> null
                LUA_TBOOLEAN -> lua_toboolean(luaState, index) != 0
                LUA_TLIGHTUSERDATA -> {
                    // TODO: test
                    val ref = lua_touserdata(luaState, index)
                        ?.asStableRef<Any>()
                        ?: error("Failed to get Kotlin value")
                    val value = ref.get()
                    value
                }
                LUA_TNUMBER -> lua_tonumberx(luaState, index, null)
                LUA_TSTRING -> lua_tolstring(luaState, index, null)?.toKString()
                LUA_TTABLE -> {
                    val map = mutableMapOf<Any?, Any?>()
                    lua_pushnil(luaState)

                    while (lua_next(luaState, index) != 0) {
                        val key = luaToKotlin(luaState, -2)
                        val value = luaToKotlin(luaState, -1)
                        map[key] = value
                        luaPop(luaState, 1)
                    }
                    map
                }
                LUA_TFUNCTION ->{
                    TODO()
                }
                LUA_TUSERDATA -> {
                    TODO()
                }
                LUA_TTHREAD ->{
                    TODO()
                }
                else -> error("Unsupported Lua type: ${lua_type(luaState, index)}")
            }
        }

        @OptIn(ExperimentalForeignApi::class)
        internal fun pushKotlinValue(luaState: CPointer<lua_State>?, value: Any?) {
            if (lua_checkstack(luaState, 1) == 0)
                error("Failed to push Kotlin value")

            when (value) {
                null -> lua_pushnil(luaState)
                is Number -> lua_pushnumber(luaState, value.toDouble())
                is Boolean -> lua_pushboolean(luaState, if (value) 1 else 0)
                is String -> lua_pushstring(luaState, value)
                else -> {
                    TODO()
                }
            }
        }

        actual fun buildLuaFunctions(bindingList: List<LuaBinding>): List<LuaLibFunction> {
            val cFunction: lua_CFunction = staticCFunction { state ->
                val args = mutableListOf<Any?>()
                val argCount = lua_gettop(state)
                for (i in 1..argCount) {
                    args.add(luaToKotlin(state!!, i))
                }

                // call Kotlin function
                val fnRef = lua_touserdata(state, luaUpValueIndex(1))
                    ?.asStableRef<LuaBindingFunction>()
                    ?: error("Failed to get Kotlin function")
                val fn = fnRef.get()
                val result = fn(args.toTypedArray())
                pushKotlinValue(state, result)

                1 // return 1
            }
            val cFunctionRef = cFunction.asStableRef<lua_CFunction>()

            val luaCFunctionRef = LuaCFunctionRef(cFunctionRef)
            return bindingList.map {
                LuaLibFunction(it.name, luaCFunctionRef)
            }
        }

        actual fun disposeLibs(libs: List<LuaLib>) {
            for (lib in libs) {
                lib.luaLibFunctions.forEach { it.luaCFunctionRef.value.dispose() }
            }
        }
    }

    private inline fun createState(): CPointer<lua_State> {
        val state = luaL_newstate() ?: error("Failed to create Lua state")
        luaL_openlibs(state)
        return state
    }

    actual fun newLib(lib: LuaLib) {
        if(lib.isGlobal) {
            for (luaLibFun in lib.luaLibFunctions) {
                val fnRef = luaLibFun.luaCFunctionRef.value
                lua_pushlightuserdata(luaState, fnRef.asCPointer())
                lua_pushcclosure(luaState, fnRef.get(), 0)
                lua_setglobal(luaState, luaLibFun.name)
            }
            return
        }

        // get table
        lua_getglobal(luaState, lib.name)
        if (lua_type(luaState, -1) == LUA_TNIL) {
            lua_settop(luaState, 0)
            lua_createtable(luaState, 0, 0)
            lua_setglobal(luaState, lib.name)

            lua_getglobal(luaState, lib.name)
        }
        // set functions
        for (luaLibFun in lib.luaLibFunctions) {
            val fnRef = luaLibFun.luaCFunctionRef.value
            lua_pushlightuserdata(luaState, fnRef.asCPointer())
            lua_pushcclosure(luaState, fnRef.get(), 0)
            lua_setfield(luaState, -2, luaLibFun.name)
        }
        lua_settop(luaState, 0) // NOT SURE: whether it need to clean stack
    }

    actual fun newLibs(libList: List<LuaLib>) = commonNewLibs(libList)

    actual fun eval(code: String): Any? {
        if (luaL_loadstring(luaState, code) != LUA_OK) {
            error("Failed to load Lua code: ${lua_tolstring(luaState, -1, null)?.toKString()}")
        }
        if (lua_pcallk(luaState, 0, LUA_MULTRET, 0, 0, null) != LUA_OK) {
            error("Failed to run Lua code: ${lua_tolstring(luaState, -1, null)?.toKString()}")
        }

        val returnValues = mutableListOf<Any?>()
        val result = when (val returnCount = lua_gettop(luaState)) {
            0 -> null
            1 -> luaToKotlin(luaState, 1)
            else -> {
                for (i in 1..returnCount) {
                    val returnValue = luaToKotlin(luaState, i)
                    returnValues.add(returnValue)
                }
                returnValues.toTypedArray()
            }
        }

        lua_settop(luaState, 0)
        return result
    }

    actual fun close() {
        lua_close(luaState)
    }
}