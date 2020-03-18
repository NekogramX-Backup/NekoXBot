package nekox.mod

import nekox.Launcher
import nekox.core.client.TdHandler
import nekox.core.defaultLog
import nekox.core.raw.setOption
import nekox.core.utils.delayDelete
import nekox.core.utils.make
import nekox.ext.confInt
import nekox.ext.putConf
import td.TdApi

object OnlineManager : TdHandler() {

    override suspend fun onUserStatus(userId: Int, status: TdApi.UserStatus) {

        if (userId != sudo.me.id) return

        when (confInt("online", 0)) {

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

                putConf("online", 0)

            }

            "online" -> {

                putConf("online", 1)

            }

            "offline" -> {

                putConf("online", 2)

            }

            else -> {

                sudo make "usage: !status <origin/online/offline>" editTo message

                delayDelete(message, 5 * 1000L)

                return

            }

        }

        sudo make "Setting Saved." editTo message

        delayDelete(message)

    }

}