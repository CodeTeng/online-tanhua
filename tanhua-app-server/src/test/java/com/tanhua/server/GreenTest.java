package com.tanhua.server;

import com.tanhua.autoconfig.template.AliyunGreenTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 15:59
 */
@SpringBootTest
public class GreenTest {
    @Autowired
    private AliyunGreenTemplate aliyunGreenTemplate;

    @Test
    public void test() throws Exception {
//        Long data = analysisMapper.sumAnalysisData("num_registered", "2020-09-14", "2020-09-18");
//        System.out.println(data);
//        Map<String, String> map = template.greenTextScan("本校小额贷款，安全、快捷、方便、无抵押，随机随贷，当天放款，上门服务");
//        map.forEach((k,v)-> System.out.println(k +"--" + v));
        List<String> list = new ArrayList<>();
        list.add("http://p7.itc.cn/images01/20210707/ecdf3bf5aaf34c67b305d3d52e0d72af.jpeg");
        Map<String, String> map = aliyunGreenTemplate.imageScan(list);
        System.out.println("------------");
        map.forEach((k, v) -> System.out.println(k + "--" + v));
    }

}
