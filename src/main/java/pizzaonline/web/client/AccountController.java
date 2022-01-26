/*
 * Copyright (c) 2022 Alexander Tolpeko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pizzaonline.web.client;

import pizzaonline.service.user.ClientUserService;
import pizzaonline.service.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.Optional;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@RequestMapping("/account")
@SessionAttributes("user")
public class AccountController {
    private final ClientUserService userService;

    @Autowired
    public AccountController(ClientUserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public User user() {
        return new User();
    }

    @GetMapping
    public String getAccount(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "account/account";
    }

    @GetMapping("/login")
    public String getLoginForm(@RequestParam Optional<String> error,
                               @ModelAttribute User user,
                               Model model) {
        if (user.getRole() != User.Role.ANONYMOUS) {
            return "redirect:/account";
        }

        model.addAttribute("loginError", error.isPresent());
        return "account/login";
    }

    @GetMapping("/register")
    public String getRegistrationForm(@ModelAttribute User user) {
        return "account/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid User user,
                           BindingResult result,
                           Model model,
                           HttpServletRequest request) {
        if (result.hasErrors()) {
            return "account/register";
        }

        try {
            userService.registerUser(user);
            userService.authorizeUser(user);
            HttpSession session = request.getSession();
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        } catch (IllegalArgumentException e) {
            model.addAttribute("alreadyExists", true);
            return "account/register";
        }

        return "redirect:/account";
    }

    @GetMapping("/edit")
    public String getAccountEditForm(@ModelAttribute User user) {
        return "account/edit-account";
    }

    @PatchMapping("/edit")
    public String editAccount(@ModelAttribute @Valid User user,
                              BindingResult result) {
        if (result.hasErrors()) {
            return "account/edit-account";
        }

        userService.updateUserData(user);
        return "redirect:/account";
    }

    @GetMapping("/edit-login")
    public String getLoginDataEditForm(@ModelAttribute User user,
                                       Model model) {
        model.addAttribute("currPassword", "");
        return "account/edit-login";
    }

    @PatchMapping("/edit-login")
    public String editLoginData(@ModelAttribute @Valid User user,
                                BindingResult result,
                                String currPassword,
                                HttpServletRequest request,
                                Model model) {
        if (result.hasErrors()) {
            return "account/edit-login";
        }

        try {
            userService.updateLogInData(user, currPassword);
            userService.authorizeUser(user);
            HttpSession session = request.getSession();
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        } catch (AccessDeniedException e) {
            model.addAttribute("passwordIsInvalid", true);
            return "account/edit-login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("alreadyExists", true);
            return "account/edit-login";
        }

        return "redirect:/account";
    }
}
