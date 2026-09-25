package com.gianteyes.gaarigar.webapp;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
@ControllerAdvice
public class PageContext {
    @ModelAttribute("requestUri") public String requestUri(HttpServletRequest request) { return request.getRequestURI(); }
    @ModelAttribute("canEdit") public boolean canEdit(HttpServletRequest request) { return request.isUserInRole("ADMIN"); }
}
