package es.uned.controllers;

import es.uned.entities.Account;
import es.uned.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador para cuentas de usuario
 */
@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Formulario de login
     * @param request Información sobre la petición HTTP a este controlador
     * @return Página JSP de login
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request) {
        String ref = request.getHeader("Referer");
        request.getSession().setAttribute("url_prior_login", ref);
        return "login";
    }

    /**
     * Formulario de registro de usuario
     * @param model Contenedor de datos para la vista
     * @return Página JSP de registro
     */
    @RequestMapping(value = "/registro", method = RequestMethod.GET)
    public String register(Model model) {
        Account accountForm = new Account();
        model.addAttribute("account", accountForm);
        return "registro";
    }

    /**
     * Recoge el POST del formulario de registro
     * @param accountForm Datos introducidos en el formulario
     * @return Redirección
     */
    @RequestMapping(value = "/registro", method = RequestMethod.POST)
    public String register(@ModelAttribute("account") Account accountForm) {
        accountForm.setActive(true);
        accountService.save(accountForm);
        return "redirect:/";
    }

}
