package com.tanhua.autoconfig.template;

import com.baidu.aip.face.AipFace;
import com.tanhua.autoconfig.properties.AipFaceProperties;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 22:52
 */
public class AipFaceTemplate {
    private AipFaceProperties aipFaceProperties;

    public AipFaceTemplate(AipFaceProperties aipFaceProperties) {
        this.aipFaceProperties = aipFaceProperties;
    }

    /**
     * 检测图片中是否包含人脸
     *
     * @param imageUrl 图片 url
     * @return true:包含 false:不包含
     */
    public boolean detect(String imageUrl) {
        AipFace client = new AipFace(
                aipFaceProperties.getAppId(),
                aipFaceProperties.getApiKey(),
                aipFaceProperties.getSecretKey()
        );
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        String imageType = "URL";
        HashMap<String, Object> options = new HashMap<>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "NORMAL");
        // 人脸检测
        JSONObject res = client.detect(imageUrl, imageType, options);
        System.out.println(res.toString(2));
        Integer error_code = (Integer) res.get("error_code");
        return error_code == 0;
    }
}
