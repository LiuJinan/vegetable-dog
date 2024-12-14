package cn.liujinnan.vegetable.dog.job.handler;

import cn.liujinnan.vegetable.dog.job.AbstractSimpleJob;
import cn.liujinnan.vegetable.dog.job.annotation.JobAnnotation;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.shardingsphere.elasticjob.infra.json.GsonFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 钉钉预警
 *
 * @author liujinan
 */
@Component
@Slf4j
public class JobDingTalkErrorHandler {

    private static final int DEFAULT_CONNECT_TIMEOUT_MILLISECONDS = 3000;

    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 5000;

    private final CloseableHttpClient httpclient = HttpClients.createDefault();

    public void handle(String url, Throwable throwable, Class<? extends AbstractSimpleJob> job) {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLISECONDS)
                .setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS).build();
        httpPost.setConfig(requestConfig);
        String errorMessage = getErrorMessage(throwable, job);
        log.info("任务名{}，发送钉钉预警报文{}", job.getSimpleName(), errorMessage);
        StringEntity entity = new StringEntity(errorMessage, StandardCharsets.UTF_8);
        entity.setContentEncoding(StandardCharsets.UTF_8.name());
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            int status = response.getStatusLine().getStatusCode();
            if (HttpURLConnection.HTTP_OK == status) {
                JsonObject responseMessage = GsonFactory.getGson().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
                if (!"0".equals(responseMessage.get("errcode").getAsString())) {
                    log.error("任务执行异常[{}], 发送钉钉预警失败，钉钉返回消息: {}", job.getSimpleName(),
                            responseMessage.get("errmsg").getAsString());
                    return;
                }
                log.info("任务执行异常[{}], 发送钉钉预警成功", job.getSimpleName());
                return;
            }
            log.error("任务执行异常[{}]，发送钉钉预警失败，钉钉响应状态: {}", job.getSimpleName(), status);
        } catch (final IOException ex) {
            log.error("任务执行异常[{}], 调用钉钉预警失败", job.getSimpleName(), ex);
        }
    }

    private String getErrorMessage(final Throwable cause, final Class<? extends AbstractSimpleJob> job) {
        JobAnnotation annotation = job.getAnnotation(JobAnnotation.class);
        DingTalkMessage dingTalkMessage = new DingTalkMessage();
        String title = String.format("<font color=#DC143C>任务%s执行失败</font>", job.getSimpleName());
        StringBuilder text = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        text.append("## ").append(title).append("  \n");
        text.append("**预警时间**: ").append(LocalDateTime.now().format(formatter)).append("  \n");
        text.append("**任务类**: ").append(job.getName()).append("  \n");
        text.append("**任务描述**: ").append(annotation.description()).append("  \n");
        String message = cause.getMessage().length() > 200 ? cause.getMessage().substring(200) : cause.getMessage();
        text.append("**异常信息**: ").append(message).append("  \n");
        MarkDownBody markDownBody = new MarkDownBody();
        markDownBody.setText(text.toString());
        markDownBody.setTitle(title);
        dingTalkMessage.setMarkdown(markDownBody);
        return GsonFactory.getGson().toJson(dingTalkMessage);
    }

    @Data
    static class DingTalkMessage {
        /**
         * 固定格式markdown
         */
        private String msgtype = "markdown";

        private String title;

        private MarkDownBody markdown;

        /**
         * 是否@所有人
         */
        private boolean isAtAll;
    }

    @Data
    static class MarkDownBody {
        private String title;
        private String text;
    }
}
