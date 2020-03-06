package nekox.ext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import nekox.core.client.TdClient
import nekox.core.client.TdHandler
import nekox.core.mkLock
import nekox.core.raw.getMessage
import nekox.core.raw.searchPublicChat
import nekox.core.text
import nekox.core.utils.delete
import nekox.core.utils.make
import td.TdApi
import java.net.InetSocketAddress
import javax.net.ssl.SSLSocketFactory

suspend fun gfwtest(ctx: TdClient, host: String, port: Int, mRetry: Int = 2): Long = GlobalScope.async(Dispatchers.IO) {

    val wooMaiBot = ctx.searchPublicChat("WooMaiBot")

    val request = ctx make "/gfwtest tcp $host $port" syncTo wooMaiBot.id

    var retry = mRetry

    val lock = mkLock<Long>()

    ctx.addHandler(object : TdHandler() {

        override suspend fun onMessageContent(chatId: Long, messageId: Long, newContent: TdApi.MessageContent) {

            if (chatId != wooMaiBot.id) return

            val message = getMessage(chatId, messageId)

            val text = message.text

            if (message.replyToMessageId != request.id || text == null) return

            ctx.removeHandler(this)

            sudo delete message
            sudo delete request

            runCatching {

                lock.send(text
                        .substringAfter("国内: 通过, ")
                        .substringBefore("ms")
                        .toLong())

            }.onFailure {

                if (retry > 0) {

                    retry--

                    lock.send(gfwtest(ctx, host, port, retry))

                } else {

                    lock.send(-1)

                }

            }

        }

    })

    return@async lock.waitFor()

}.await()

fun tcping(host: String, port: Int): Long {

    val start = System.currentTimeMillis()

    runCatching {

        val socket = SSLSocketFactory.getDefault().createSocket()

        socket.connect(InetSocketAddress(host, port))
        socket.getOutputStream().bufferedWriter().write("HTTP 1.1")

        socket.close()

        return System.currentTimeMillis() - start

    }

    return -1

}