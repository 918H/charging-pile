package com.charging.user.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class XssFilter implements Filter {

    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|</script|javascript:|on\\w+=|<iframe|</iframe|<object|</object|<embed|</embed>)",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        
        if (XSS_PATTERN.matcher(uri).find()) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        chain.doFilter(new XssHttpServletRequestWrapper(httpRequest), response);
    }

    @Override
    public void destroy() {
    }
}
