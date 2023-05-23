package cn.beehive.cell.midjourney.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjounery 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "midjounery")
public class MidjourneyConfig {

    /**
     * 服务器 id
     */
    private String guildId;

    /**
     * 频道 id
     */
    private String channelId;

    /**
     * 用户 token
     */
    private String userToken;

    /**
     * 机器人 token
     */
    private String botToken;

    /**
     * Midjourney 机器人的名称，需要一致
     */
    private String mjBotName = "Midjourney Bot";

    /**
     * 调用 discord 接口时的 user-agent
     */
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36";

    /**
     * 图片存储路径
     */
    private String imageLocation;

    /**
     * 等待队列最大长度
     */
    private Integer maxWaitQueueSize = 5;

    /**
     * 执行队列最大长度
     */
    private Integer maxExecuteQueueSize = 2;

    /**
     * 最大文件大小，用于 descibe 上传图片，单位字节
     * 默认 6M
     */
    private Integer maxFileSize = 1024 * 1024 * 6;

    /**
     * Discord Api Url
     */
    private String discordApiUrl = "https://discord.com/api/v9/interactions";

    /**
     * Discord 上传图片 Url
     */
    private String discordUploadUrl;

    /**
     * 获取 Discord 上传图片 Url
     */
    public String getDiscordUploadUrl() {
        return "https://discord.com/api/v9/channels/" + channelId + "/attachments";
    }
}
