package com.charging.user.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.regex.Pattern;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
        "<script.*?>.*?</script>", 
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return cleanXss(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        String[] cleaned = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleaned[i] = cleanXss(values[i]);
        }
        return cleaned;
    }

    private String cleanXss(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }

        value = SCRIPT_PATTERN.matcher(value).replaceAll("");
        value = value.replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("(", "&#40;")
                    .replaceAll(")", "&#41;")
                    .replaceAll("'", "&#39;")
                    .replaceAll("\"", "&quot;");
        
        return value;
    }
}
