package top.moxel.plugin.infrastructure.io

class I18n private constructor() {
    private val translations: MutableMap<String, Any> = mutableMapOf()

    companion object {
        val instance: I18n by lazy { I18n() }
    }

    fun addTranslation(key: String, value: Any) {
        val keys = key.split(".")
        var current = translations

        for (i in keys.indices) {
            val k = keys[i]
            if (i == keys.size - 1) {
                current[k] = value
            } else {
                current = current.getOrPut(k) { mutableMapOf<String, Any>() } as MutableMap<String, Any>
            }
        }
    }

    fun getTranslation(key: String): String? {
        val keys = key.split(".")
        var current: Any? = translations

        for (k in keys) {
            if (current is Map<*, *>) {
                current = current[k]
            } else {
                return null
            }
        }

        return current as? String
    }
}