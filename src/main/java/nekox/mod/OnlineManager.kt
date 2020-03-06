package nekox.mod

import nekox.Launcher
import nekox.box.IntEntity
import nekox.core.client.TdHandler
import nekox.core.raw.setOption
import nekox.core.utils.delayDelete
import nekox.core.utils.make
import td.TdApi

object OnlineManager : TdHandler() {

    val db = Launcher.BOX.boxFor(IntEntity::class.java)

    override suspend fun onUserStatus(userId: Int, status: TdApi.UserStatus) {

        if (userId != sudo.me.id) return

        val conf = db[0] ?: return

        when (conf.value) {

            1 -> {

                if (status is TdApi.UserStatusOffline) {

                    setOption("online", TdApi.OptionValueBoolean(true))

                }

            }

            2 -> {

                if (status is TdApi.UserStatusOnline) {

                    setOption("online", TdApi.OptionValueBoolean(false))

                }

            }

        }

    }

    override suspend fun onNewMessage(userId: Int, chatId: Long, message: TdApi.Message) {

        if (userId != sudo.me.id) return

        val (function, param, params, originParams) = message.parseFunction() ?: return

        if (function != "status") return

        when (params[0]) {

            "origin" -> {

                db.remove(0)

                db.put(IntEntity(0, 0))

            }

            "online" -> {

                db.remove(0)

                db.put(IntEntity(0, 1))


            }

            "offline" -> {

                db.remove(0)

                db.put(IntEntity(0, 2))

            }

            else -> {

                sudo make "usage: !status <origin/online/offline>" editTo message

                delayDelete(message,5 * 1000L)

            }

        }

        sudo make "Setting Saved." editTo message

        delayDelete(message)

    }

}