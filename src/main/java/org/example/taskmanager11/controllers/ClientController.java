package org.example.taskmanager11.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.taskmanager11.model.Client;
import org.example.taskmanager11.services.ClientService;
import org.example.taskmanager11.utils.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    private void checkAuthorized(HttpSession session) {
        if (session.getAttribute("login") == null)
            throw new NotAuthorizedException();
    }

    @GetMapping("register")
    public String register() {
        return "register";
    }

    @GetMapping("login")
    public String login() {
        return "login";
    }

    @PostMapping("register")
    public String register(@RequestParam String login,
                           @RequestParam String password) {
        String salt = Utils.generateRandomString(10);
        String hash = Utils.passwordHash(salt, password);

        clientService.addClient(login, salt, hash);
        return "redirect:/login";
    }

    @PostMapping("login")
    public String login(@RequestParam String login,
                        @RequestParam String password,
                        HttpSession session) {
        if (clientService.checkClient(login, password)) {
            session.setAttribute("login", login);
            return "redirect:/";
        } else
            return "redirect:/login";
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.removeAttribute("login");
        return "redirect:/";
    }

    @GetMapping("/change-password")
    public String changePasswordPage(HttpSession session) {
        checkAuthorized(session);
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        checkAuthorized(session);
        String login = (String) session.getAttribute("login");

        if (login == null) {
            return "redirect:/login";
        }


        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/change-password";
        }


        boolean success = clientService.changePassword(login, oldPassword, newPassword);

        if (success) {
            session.removeAttribute("login");
            return "redirect:/login?changed";
        } else {
            // Ошибка (неверный старый пароль)
            redirectAttributes.addFlashAttribute("error", "Incorrect old password.");
            return "redirect:/change-password";
        }
    }

    @ExceptionHandler(value = NotAuthorizedException.class)
    public String onException() {
        return "redirect:/login";
    }
}
