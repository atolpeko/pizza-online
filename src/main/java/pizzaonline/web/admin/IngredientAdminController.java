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

import pizzaonline.service.ingredient.AdminIngredientService;
import pizzaonline.service.ingredient.Ingredient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/ingredients")
public class IngredientAdminController {
    private final AdminIngredientService ingredientService;

    @Autowired
    public IngredientAdminController(AdminIngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/manage")
    public String getManagePage(Model model) {
        List<Ingredient> ingredients = ingredientService.findAll();
        model.addAttribute("ingredients", ingredients);
        return "ingredient/manage";
    }

    @GetMapping("/new")
    public String getIngredientCreationForm(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        return "ingredient/new";
    }

    @PostMapping("/new")
    public String createIngredient(@ModelAttribute @Valid Ingredient ingredient,
                                   BindingResult result) {
        if (result.hasErrors()) {
            return "ingredient/new";
        }

        ingredientService.save(ingredient);
        return "redirect:/ingredients/manage";
    }

    @GetMapping("/edit/{id}")
    public String getIngredientEditForm(@PathVariable int id, Model model) {
        Ingredient ingredient = ingredientService.findById(id).orElseThrow();
        model.addAttribute("ingredient", ingredient);
        return "ingredient/edit";
    }

    @PatchMapping("/{id}")
    public String editIngredient(@PathVariable int id,
                                 @ModelAttribute @Valid Ingredient ingredient,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "ingredient/edit";
        }

        ingredient.setId(id);
        ingredientService.update(ingredient);

        return "redirect:/ingredients/manage";
    }

    @DeleteMapping("/{id}")
    public String deleteIngredient(@PathVariable int id) {
        ingredientService.deleteById(id);
        return "redirect:/ingredients/manage";
    }
}
