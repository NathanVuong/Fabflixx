import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        // Redirect to login page if the "user" attribute doesn't exist in session
        if (httpRequest.getSession().getAttribute("user") == null) {
            String url = httpRequest.getRequestURI();
            String[] components = url.split("/");
            if (url.contains("/api/")) {
                components[components.length - 2] = "login.html";
                StringBuilder urlBuilder = new StringBuilder();
                for (int i = 0; i < components.length - 2; i++) {
                    urlBuilder.append(components[i]).append("/");
                }
                urlBuilder.deleteCharAt(urlBuilder.length() - 1);
                String finalUrl = urlBuilder.toString();
                httpResponse.sendRedirect(finalUrl);
            } else{
                httpResponse.sendRedirect("/cs122b_project1/login.html");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("style.css");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("_dashboard.html");
        allowedURIs.add("_dashboard.js");
        allowedURIs.add("api/employeelogin");
        allowedURIs.add("cs122b-project1/login.html");
        allowedURIs.add("cs122b-project1/style.css");
        allowedURIs.add("cs122b-project1/login.js");
        allowedURIs.add("cs122b-project1/api/login");
        allowedURIs.add("cs122b-project1/_dashboard.html");
        allowedURIs.add("cs122b-project1/_dashboard.js");
        allowedURIs.add("cs122b-project1/api/employeelogin");
    }

    public void destroy() {
        // ignored.
    }

}
