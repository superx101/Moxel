import { run, add, sub, log } from "./Moxel-moxel-browser-wasm-js.mjs";

const scriptFunctions = {
    add,
    sub,
    log
};

async function init() {
    const factory = new wasmoon.LuaFactory();
    const lua = await factory.createEngine();

    try {
        for (let key in scriptFunctions) {
            lua.global.set(key, scriptFunctions[key]);
        }
        await lua.doString(`
                log(add(10, 10))
                log(sub(10, 1))
            `);
    } finally {
        lua.global.close();
        run();
    }
}

init();