package nekox.mod

import nekox.Launcher
import nekox.core.client.TdHandler
import nekox.core.defaultLog
import nekox.core.fromPrivate
import nekox.core.raw.viewMessages
import nekox.core.utils.delayDelete
import nekox.core.utils.make
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import org.dizitart.no2.objects.filters.ObjectFilters
import td.TdApi

object AutoMask : TdHandler() {

    val maskConn = Launcher.NITRITE.getRepository("mask", MaskEntity::class.java)

    @Indices(Index("chatId"))
    data class MaskEntity(
            @JvmField var chatId: Long,
            @JvmField var enable: Boolean) {

        constructor() : this(-1, false)

    }

    override suspend fun onNewMessage(userId: Int, chatId: Long, message: TdApi.Message) {

        if (userId != OnlineManager.sudo.me.id) {

            if (!message.fromPrivate) return

            var conf = maskConn.find(ObjectFilters.eq("chatId", chatId))

            run {

                when (conf.firstOrDefault()?.enable) {

                    true -> {

                        viewMessages(chatId, longArrayOf(message.id), true)

                    }

                    null -> {

                        return@run

                    }

                }

            }

            conf = maskConn.find(ObjectFilters.eq("chatId", -1))

            if (conf.firstOrDefault()?.enable == true) {

                viewMessages(chatId, longArrayOf(message.id), true)

            }

        } else {

            val (function, param, params, originParams) = message.parseFunction() ?: return

            if (function !in arrayOf("auto_mask", "am")) return

            when (params.getOrNull(0)) {

                "on" -> {

                    maskConn.update(ObjectFilters.eq("chatId", chatId
                            .takeIf { it != sudo.me.id.toLong() } ?: -1),
                            MaskEntity(chatId.takeIf { it != sudo.me.id.toLong() }
                                    ?: -1, true), true)

                }

                "off" -> {

                    maskConn.update(ObjectFilters.eq("chatId", chatId
                            .takeIf { it != sudo.me.id.toLong() } ?: -1),
                            MaskEntity(chatId.takeIf { it != sudo.me.id.toLong() }
                                    ?: -1, false), true)

                }

                else -> {

                    sudo make "usage (for ${if (chatId == sudo.me.id.toLong()) "all chats" else "current chat"}) : !$function <enable/disable>" editTo message

                    delayDelete(message, 5 * 1000L)

                    return

                }

            }

            OnlineManager.sudo make "Setting Saved." editTo message

            delayDelete(message)

        }

    }

}