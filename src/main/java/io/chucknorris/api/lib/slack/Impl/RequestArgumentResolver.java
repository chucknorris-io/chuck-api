package io.chucknorris.api.lib.slack.Impl;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequestArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(Request.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        Request request = new Request();

        request.setChannelId(nativeWebRequest.getParameter("channel_id"));
        request.setChannelName(nativeWebRequest.getParameter("channel_name"));
        request.setCommand(nativeWebRequest.getParameter("command"));
        request.setEnterpriseId(nativeWebRequest.getParameter("enterprise_id"));
        request.setEnterpriseName(nativeWebRequest.getParameter("enterprise_name"));
        request.setResponseUrl(nativeWebRequest.getParameter("response_url"));
        request.setTeamDomain(nativeWebRequest.getParameter("team_domain"));
        request.setTeamId(nativeWebRequest.getParameter("team_id"));
        request.setText(nativeWebRequest.getParameter("text"));
        request.setToken(nativeWebRequest.getParameter("token"));
        request.setTriggerId(nativeWebRequest.getParameter("trigger_id"));
        request.setUserId(nativeWebRequest.getParameter("user_id"));
        request.setUserName(nativeWebRequest.getParameter("user_name"));

        return request;
    }
}
