package cn.beehive.cell.midjourney.listener;

import cn.beehive.base.cache.SysParamCache;
import cn.beehive.base.enums.SysParamKeyEnum;
import cn.beehive.cell.midjourney.config.MidjourneyConfig;
import cn.hutool.core.util.BooleanUtil;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocketFactory;
import jakarta.annotation.Resource;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author hncboy
 * @date 2023/5/18
 * Discord 启动类
 */
@Component("discordStarter")
public class DiscordStarter implements InitializingBean {

    @Resource
    private MidjourneyConfig midjourneyConfig;

    @Resource
    private DiscordMessageListener discordMessageListener;

    @Override
    public void afterPropertiesSet() {
        // 判断是否启用 midjourney
        String enable = SysParamCache.getValue(SysParamKeyEnum.ENABLE_MIDJOURNEY);
        if (!BooleanUtil.toBoolean(enable)) {
            return;
        }

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(midjourneyConfig.getBotToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        builder.addEventListeners(this.discordMessageListener);

        // 判断是否需要代理
        if (!BooleanUtil.toBoolean(SysParamCache.getValue(SysParamKeyEnum.ENABLE_PROXY))) {
            builder.build();
            return;
        }

        String proxyHost = SysParamCache.getValue(SysParamKeyEnum.HTTP_PROXY_HOST);
        int proxyPort = Integer.parseInt(SysParamCache.getValue(SysParamKeyEnum.HTTP_PROXY_PORT));

        // 解决报错：java.net.SocketTimeoutException: Connect timed out
        OkHttpClient.Builder okhttpbuilder = new OkHttpClient.Builder();
        okhttpbuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
        builder.setHttpClientBuilder(okhttpbuilder);

        // 解决报错：com.neovisionaries.ws.client.WebSocketException: Failed to connect to 'gateway.discord.gg:443': Connect timed out
        WebSocketFactory webSocketFactory = new WebSocketFactory();
        ProxySettings proxySettings = webSocketFactory.getProxySettings();
        proxySettings.setHost(proxyHost);
        proxySettings.setPort(proxyPort);
        builder.setWebsocketFactory(webSocketFactory);

        builder.build();
    }
}
