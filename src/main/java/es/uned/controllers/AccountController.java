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
 *
 */
@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        String ref = request.getHeader("Referer");
        request.getSession().setAttribute("url_prior_login", ref);
        return "login";
    }

    @RequestMapping(value = "/registro", method = RequestMethod.GET)
    public String register(Model model) {
        Account accountForm = new Account();
        model.addAttribute("account", accountForm);
        return "registro";
    }

    @RequestMapping(value = "/registro", method = RequestMethod.POST)
    public String register(@ModelAttribute("account") Account accountForm, Model model) {
        accountForm.setActive(true);
        accountService.save(accountForm);
        return "redirect:/";
    }

}
