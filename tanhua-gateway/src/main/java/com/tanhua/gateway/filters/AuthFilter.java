package com.tanhua.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.commons.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/7 14:10
 */
@Component
@Order(1)
public class AuthFilter implements GlobalFilter {
    /**
     * 配置不校验的连接
     */
    @Value("${gateway.excludedUrls}")
    private List<String> excludedUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取当前请求连接
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("path:" + path);
        // 放行不需要校验的接口
        if (excludedUrls.contains(path)) {
            // 放行
            return chain.filter(exchange);
        }
        // 2. 获取请求头中的token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        // 后台系统页面发送的token以"Bearer "开头，需要处理
        if (StringUtils.isNotBlank(token)) {
            token = token.replace("Bearer ", "");
        }
        ServerHttpResponse response = exchange.getResponse();
        // 3. 使用工具类，判断token是否有效
        boolean verifyToken = JwtUtils.verifyToken(token);
        // 如果token失效，返回状态码401，拦截
        if (!verifyToken) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("errCode", 401);
            responseData.put("errMessage", "用户未登录");
            return responseError(response, responseData);
        }
        return chain.filter(exchange);
    }
    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData) {
        // 将信息转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 输出错误信息到页面
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }
}
