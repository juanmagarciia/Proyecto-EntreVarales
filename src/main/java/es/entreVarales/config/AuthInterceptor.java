package es.entreVarales.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        String uri = request.getRequestURI();
        if (uri.startsWith("/login") || uri.startsWith("/register") || uri.startsWith("/css") || uri.startsWith("/js") || uri.equals("/")) {
            return true; // Permitir acceso sin login a estas rutas
        }

        if (session != null && session.getAttribute("user") != null) {
            return true; // Usuario autenticado
        }

        response.sendRedirect("/"); // Redirigir si no hay usuario en sesión
        return false;
    }
}
