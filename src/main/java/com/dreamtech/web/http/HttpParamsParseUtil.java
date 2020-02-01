package com.dreamtech.web.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import com.dreamtech.utils.ApplicationUtil;
import com.dreamtech.web.handler.HandlerParam;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class HttpParamsParseUtil {
    private static final String TEMP_SUFFIX_NAME = ".tmp";

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String MULTIPART_FORM_URLENCODED = "multipart/x-www-form-urlencoded";
    private static final String APPLICATION_JSON = "application/json";

    public static Object[] parse(HttpServletRequest req, HandlerParam[] handlerParams) throws Exception {
        String contentType = req.getContentType();
        if (StringUtils.isBlank(contentType)) {
            //说明是GET请求里的那种憨憨参数
            return parseUrlParameters(req, handlerParams);
        } else if (contentType.contains(MULTIPART_FORM_DATA) || contentType.contains(MULTIPART_FORM_URLENCODED)) {
            return parseFormData(req, handlerParams);
        } else if (contentType.contains(APPLICATION_JSON)) {
            return parseJson(req, handlerParams);
        } else {
            throw new Exception(String.format("[%s] this type of content-type can't handle", contentType));
        }
    }


    //处理[ multipart/form-data | multipart/x-www-form-urlencoded ]
    private static Object[] parseFormData(HttpServletRequest req, HandlerParam[] handlerParams) throws Exception {
        HashMap<String, Object> formDataParams = new HashMap<>();

        //TODO  要把这些配置 搞到application.properties中去
        // 上传配置
        int memory_threshold = 1024 * 1024 * 3;  // 3MB
        int max_file_size = 1024 * 1024 * 40; // 40MB
        int max_request_size = 1024 * 1024 * 50; // 50MB

        // 创建工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //设置临时目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        // 设置缓冲区大小
        factory.setSizeThreshold(memory_threshold);
        // 设置缓冲区目录
        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 设置最大文件上传值
        upload.setFileSizeMax(max_file_size);
        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(max_request_size);

        // 创建解析器
        // 得到所有的文件
        List<FileItem> fileItems = upload.parseRequest(new ServletRequestContext(req));

        if (fileItems != null && fileItems.size() > 0) {
            for (FileItem fileItem : fileItems) {
                //fileItem.isFormField()是false 说明是文件   是true说明是普通参数c
                if (fileItem.isFormField()) {
                    //普通参数
                    formDataParams.put(fileItem.getFieldName(), new String(fileItem.get(), StandardCharsets.UTF_8));
                } else {
                    //文件
                    File tempFile = File.createTempFile(fileItem.getFieldName(), getExtensionByContentType(fileItem.getContentType()));
                    fileItem.write(tempFile);
                    formDataParams.put(fileItem.getFieldName(), tempFile);
                }
            }
        }

        Object[] httpParams = new Object[handlerParams.length];
        for (int i = 0; i < handlerParams.length; i++) {
            httpParams[i] = formDataParams.get(handlerParams[i].getParamName());
        }

        return httpParams;
    }

    //处理[ application/json ]
    private static Object[] parseJson(HttpServletRequest req, HandlerParam[] handlerParams) throws IOException {
        req.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        String json = ApplicationUtil.bufferedReaderToString(req.getReader());
        Object[] httpParams = new Object[handlerParams.length];
        int index = 0;
        for (HandlerParam handlerParam : handlerParams) {
            httpParams[index] = handlerParam.isRequestBody() ?
                    JSON.parseObject(json, handlerParam.getParamType()) : null;
            index++;
        }
        return httpParams;

    }

    private static Object[] parseUrlParameters(HttpServletRequest req, HandlerParam[] handlerParams) {
        Object[] httpParams = new Object[handlerParams.length];
        for (int i = 0; i < httpParams.length; i++) {
            httpParams[i] = req.getParameter(handlerParams[i].getParamName());
        }
        return httpParams;
    }


    private static String getExtensionByContentType(String contentType) throws MimeTypeException {
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType mimeType = allTypes.forName(contentType);
        String extension = mimeType.getExtension();
        return StringUtils.isBlank(extension) ? TEMP_SUFFIX_NAME : extension;
    }
}
