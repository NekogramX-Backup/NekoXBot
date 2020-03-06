package nekox

import io.objectbox.BoxStore
import kotlinx.coroutines.runBlocking
import nekox.box.ObjectBox
import nekox.core.client.TdBot
import nekox.core.client.TdCliClient
import nekox.core.defaultLog
import nekox.core.displayName
import nekox.core.raw.getUser
import nekox.core.text
import nekox.mod.OnlineManager
import org.yaml.snakeyaml.Yaml
import td.TdApi
import java.io.File
import kotlin.system.exitProcess

class Launcher : TdCliClient() {

    companion object {

        lateinit var INSTANCE: Launcher
        lateinit var BOT: TdBot
        lateinit var BOX: BoxStore

        @JvmStatic
        fun main(args: Array<String>) = runBlocking<Unit> {

            defaultLog.info("NekoX 正加载...")

            runCatching {

                defaultLog.info("加载 TDLib")

                TdLoader.tryLoad()

            }.onFailure {

                defaultLog.error("TDLib 加载失败... 检查您的类路径", it)

                exitProcess(100)

            }

            INSTANCE = Launcher()

            INSTANCE.start()

        }

    }

    override suspend fun onNewMessage(userId: Int, chatId: Long, message: TdApi.Message) {

        defaultLog.debug("${getUser(userId).displayName}: ${message.text}")

    }

    override suspend fun onLogin() {

        super.onLogin()

        BOX = ObjectBox.create(File(sudo.options.databaseDirectory),"nekox")

        addHandler(OnlineManager)

        defaultLog.info("加载伴生机器人")

        val config = Yaml().load<HashMap<String, String>>(File("config.yml").readText())

        BOT = TdBot(config["BOT_TOKEN"]!!).apply { start() }

        if (!BOT.waitForAuth()) {

            defaultLog.error("机器人密钥无效")

            BOT.stop()

            stop()

            exitProcess(100)

        }

    }

}