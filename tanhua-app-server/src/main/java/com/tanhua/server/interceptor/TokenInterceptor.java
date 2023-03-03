package com.tanhua.server.interceptor;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: token 拦截器
 * @author: ~Teng~
 * @date: 2023/3/2 10:03
 */
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            // 未登录 拦截
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("请重新登录");
            return false;
        }
        boolean flag = JwtUtils.verifyToken(token);
        if (!flag) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(401);
            response.getWriter().write("请重新登录");
            return false;
        }
        // token 有效
        String mobile = (String) JwtUtils.getClaims(token).get("mobile");
        Integer id = (Integer) JwtUtils.getClaims(token).get("id");
        // 构造User对象，存入 Threadlocal
        User user = new User();
        user.setId(Long.valueOf(id));
        user.setMobile(mobile);
        UserHolder.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
}
