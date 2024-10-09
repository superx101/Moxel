package top.moxel.plugin

import kotlinx.browser.document
import kotlinx.dom.appendElement
import top.moxel.plugin.browser.infrastructure.BrowserPlatform
import top.moxel.plugin.infrastructure.di.DI
import top.moxel.plugin.infrastructure.platform.Platform

@OptIn(ExperimentalJsExport::class)
@JsExport
fun add(x: Int, y: Int): Int {
    return x + y
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun sub(x: Int, y: Int): Int {
    return x - y
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun log(text: JsAny) {
    println("[log] $text")
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun run() {
    println("Hello, World! Console")

    document.body?.appendElement("div") {
        innerHTML = "Hello, World!"
    }

    DI.addSingleton<Platform>(BrowserPlatform())
    DI.startApplication()

    MoxelCore()
}