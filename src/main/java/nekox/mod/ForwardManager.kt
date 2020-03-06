package nekox.mod

import nekox.core.client.TdHandler
import nekox.mod.OnlineManager.parseFunction
import td.TdApi

object ForwardManager : TdHandler() {

    override suspend fun onNewMessage(userId: Int, chatId: Long, message: TdApi.Message) {

        if (userId != OnlineManager.sudo.me.id) {



        } else {

            val (function, param, params, originParams) = message.parseFunction() ?: return

            if (function !in arrayOf("forward", "fwd")) return

        }

    }

}