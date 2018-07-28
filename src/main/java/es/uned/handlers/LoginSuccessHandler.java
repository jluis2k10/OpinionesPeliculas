package es.uned.handlers;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Manejador para decidir qué hacer tras una operación exitosa de login.
 */
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    /**
     * Constructor de la clase.
     * @param defaultTargetUrl establece la redirección por defecto ({@link es.uned.config.SecurityConfig#successHandler()})
     */
    public LoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Establecemos la redirección a efectuar tras un login realizado con éxito en base al
     * parámetro de sesión "<code>url_prior_login</code>", el cual se genera en
     * {@link es.uned.controllers.AccountController#login(HttpServletRequest)} a
     * partir de la página de la que se procede.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session != null) {
            String redirectURL = (String) session.getAttribute("url_prior_login");
            // No redireccionar a login o a registro (denegado)
            if (redirectURL != null && (redirectURL.contains("/login") || redirectURL.contains("/registro")))
                redirectURL = null;
            if (redirectURL != null) {
                // Eliminar el atriuto de la sesión
                session.removeAttribute("url_prior_login");
                // Redireccionar
                try {
                    getRedirectStrategy().sendRedirect(request, response, redirectURL);
                } catch (IOException e) {
                    throw new IOException(e);
                }
            } else {
                try {
                    super.onAuthenticationSuccess(request, response, authentication);
                } catch (ServletException e) {
                    throw new ServletException(e);
                }
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
