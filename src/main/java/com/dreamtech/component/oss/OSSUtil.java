package com.dreamtech.component.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.dreamtech.utils.ApplicationUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class OSSUtil {

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    private String endpoint;


    private OSS ossClient;


    OSSUtil(String accessKeyId, String accessKeySecret, String bucketName, String endpoint) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;
        this.endpoint = endpoint;
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 分片上传单个文件到阿里云OSS
     *
     * @param fileName    文件 在阿里云的 路径 + 名称
     * @param inputStream 文件的输入流
     * @return 可以访问到文件的url
     */
    String uploadFile(String fileName, InputStream inputStream) {
        try {
            ByteArrayOutputStream baos = ApplicationUtil.inputStreamToByteArrayOutputStream(inputStream);
            InitiateMultipartUploadRequest request =
                    new InitiateMultipartUploadRequest(bucketName, fileName);
            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个ID来发起相关的操作，如取消分片上传、查询分片上传等。
            String uploadId = upresult.getUploadId();
            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> partETags = new ArrayList<>();
            // 计算文件有多少个分片。
            final long partSize = 1024 * 1024L;   // 1MB
            //文件总长度
            long fileLength = baos.toByteArray().length;
            //分片数
            int partCount = (int) (fileLength / partSize);
            // 遍历分片上传。
            if (fileLength % partSize != 0) {
                partCount++;
            }
            for (int i = 0; i < partCount; i++) {
                InputStream is = new ByteArrayInputStream(baos.toByteArray());
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                // 跳过已经上传的分片。
                is.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(fileName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(is);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果会包含一个PartETag。PartETag将被保存到partETags中。
                partETags.add(uploadPartResult.getPartETag());
                System.out.println(String.format("%s upload progressing : %s", fileName, 100f * startPos / fileLength) + "%");
            }
            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, fileName, uploadId, partETags);
            // 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
            completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);
            // 完成上传。
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println(String.format("%s upload progressing : %s", fileName, 100) + "%");
            return completeMultipartUploadResult.getLocation();
        } catch (Exception e) {
            System.err.println(String.format("%s upload file failed : [%s]", fileName, e.toString()));
            return "error_url";
        }
    }

    /**
     * 根据文件名删除某个文件
     *
     * @param fileName 文件名
     */
    void deleteFile(String fileName) {
        // 删除文件。
        ossClient.deleteObject(bucketName, fileName);
    }

    void stop() {
        ossClient.shutdown();
    }
}
