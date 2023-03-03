package com.tanhua.server;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 22:42
 */
public class AipFaceTest {
    public static final String APP_ID = "30865459";
    public static final String API_KEY = "0nnsoeDWvymtVg7GsXvZjc57";
    public static final String SECRET_KEY = "OgkjDo4BQtf5zaMwx1Wtpzg1dNEzok83";

    public static void main(String[] args) {
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        String image = "https://tanhua0823.oss-cn-shanghai.aliyuncs.com/2023/03/01/860add6d-c5d7-4bdf-aec4-b04baff2975c.jpeg";
        String imageType = "URL";

        HashMap<String, Object> options = new HashMap<>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
        System.out.println(res.toString(2));
    }
}
