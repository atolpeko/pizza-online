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

package pizzaonline.web.admin;

import pizzaonline.service.user.AdminUserService;
import pizzaonline.service.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {
    private final AdminUserService userService;

    @Autowired
    public UserController(AdminUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUsers(@RequestParam boolean registered,
                           Model model) {
        List<User> users = userService.findAll();
        if (registered) {
            users = users.stream()
                    .filter(user -> user.getRole() != User.Role.ANONYMOUS)
                    .collect(Collectors.toList());
        }

        model.addAttribute("users", users);
        return "user/users";
    }

    @GetMapping("/{id}")
    public String getUserInfo(@PathVariable int id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "user/user-info";
    }


    @PatchMapping("/status/{id}")
    public String changeAccountStatus(@PathVariable int id,
                                      @RequestParam boolean block) {
        userService.setActivated(id, !block);
        return "redirect:/users/" + id;
    }
}
