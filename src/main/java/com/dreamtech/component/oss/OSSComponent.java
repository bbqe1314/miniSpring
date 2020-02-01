package com.dreamtech.component.oss;

import com.dreamtech.anno.Component;
import com.dreamtech.component.IComponent;
import com.dreamtech.context.ApplicationArgs;
import com.dreamtech.context.ApplicationContext;

import java.io.InputStream;
import java.util.HashMap;

/**
 * 阿里云的OSS组件
 */
@Component
public class OSSComponent implements IComponent {

    private HashMap<String, Object> appArgs;
    private OSSUtil ossUtil;

    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String endPoint;
    private boolean singleInit = false;

    @Override
    public void init() {
        this.appArgs = ApplicationContext.getInstance().getAppArgs();
        if (!isInitArgsPrepared() && !singleInit) {
            ossUtil = new OSSUtil(accessKeyId, accessKeySecret, bucketName, endPoint);
            singleInit = true; // 保证这段代码快只被执行一次
        }
    }

    private boolean isInitArgsPrepared() {
        accessKeyId = this.appArgs.get(ApplicationArgs.OSS_ACCESS_KEY_ID).toString();
        accessKeySecret = this.appArgs.get(ApplicationArgs.OSS_ACCESS_KEY_SECRET).toString();
        bucketName = this.appArgs.get(ApplicationArgs.OSS_BUCKET_NAME).toString();
        endPoint = this.appArgs.get(ApplicationArgs.OSS_END_POINT).toString();
        return accessKeyId.equals(ApplicationArgs.DEFAULT_OSS_ACCESS_KEY_ID) ||
                accessKeySecret.equals(ApplicationArgs.DEFAULT_OSS_ACCESS_KEY_SECRET) ||
                bucketName.equals(ApplicationArgs.DEFAULT_OSS_BUCKET_NAME) ||
                endPoint.equals(ApplicationArgs.DEFAULT_OSS_END_POINT);
    }

    /**
     * 上传文件
     *
     * @param fileName    文件名
     * @param inputStream 文件的输入流
     * @return 文件的url
     */
    public String uploadFile(String fileName, InputStream inputStream) {
        return ossUtil.uploadFile(fileName, inputStream);
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     */
    public void deleteFile(String fileName) {
        ossUtil.deleteFile(fileName);
    }

    @Override
    public void stop() {
        ossUtil.stop();
    }
}
