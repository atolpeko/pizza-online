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

import pizzaonline.service.drink.AdminDrinkService;
import pizzaonline.service.drink.Drink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/drinks")
public class DrinkAdminController {
    private final AdminDrinkService drinkService;

    @Autowired
    public DrinkAdminController(AdminDrinkService drinkService) {
        this.drinkService = drinkService;
    }

    @GetMapping("/manage")
    public String getManagePage(Model model) {
        List<Drink> drinks = drinkService.findAll();
        model.addAttribute("drinks", drinks);
        return "drink/manage";
    }

    @GetMapping("/new")
    public String getDrinkCreationForm(Model model) {
        model.addAttribute("drink", new Drink());
        return "drink/new";
    }

    @PostMapping("/new")
    public String createDrink(@RequestParam MultipartFile file,
                              @ModelAttribute @Valid Drink drink,
                              BindingResult result) throws IOException {
        if (drinkIsInvalid(result, file)) {
            return "drink/new";
        }

        drink.setImageData(file.getBytes());
        drinkService.save(drink);

        return "redirect:/drinks/manage";
    }

    private boolean drinkIsInvalid(BindingResult result, MultipartFile file) {
        if (result.hasErrors()) {
            return result.hasFieldErrors("imageData")
                    && file.isEmpty();
        }

        return false;
    }

    @GetMapping("/edit/{id}")
    public String getDrinkEditForm(@PathVariable int id, Model model) {
        Drink drink = drinkService.findById(id).orElseThrow();
        model.addAttribute("drink", drink);
        return "drink/edit";
    }

    @PatchMapping("/{id}")
    public String editDrink(@PathVariable int id,
                            @RequestParam MultipartFile file,
                            @ModelAttribute @Valid Drink drink,
                            BindingResult result) throws IOException {
        if (drinkIsInvalid(result, file)) {
            return "drink/edit";
        }

        drink.setId(id);
        drink.setImageData(file.getBytes());
        drinkService.update(drink);

        return "redirect:/drinks/manage";
    }

    @DeleteMapping("/{id}")
    public String deleteDrink(@PathVariable int id) {
        drinkService.deleteById(id);
        return "redirect:/drinks/manage";
    }
}
