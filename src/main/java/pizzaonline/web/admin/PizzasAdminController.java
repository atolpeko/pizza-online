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
import pizzaonline.service.pizza.AdminPizzaService;
import pizzaonline.service.pizza.Pizza;

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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pizzas")
public class PizzasAdminController {
    private final AdminPizzaService pizzaService;
    private final AdminIngredientService ingredientService;

    @Autowired
    public PizzasAdminController(AdminPizzaService pizzaService,
                                 AdminIngredientService ingredientService) {
        this.pizzaService = pizzaService;
        this.ingredientService = ingredientService;
    }

    @GetMapping("/manage")
    public String getManagePage(Model model) {
        List<Pizza> pizzas = pizzaService.findAll();
        model.addAttribute("pizzas", pizzas);
        return "pizza/manage";
    }

    @GetMapping("/new")
    public String getPizzaCreationForm(Model model) {
        List<Ingredient> ingredients = ingredientService.findAll();
        model.addAttribute("allIngredients", ingredients);
        model.addAttribute("pizza", new Pizza());
        return "pizza/new";
    }

    @PostMapping("/new")
    public String createPizza(@RequestParam MultipartFile image,
                              @ModelAttribute @Valid Pizza pizza,
                              BindingResult result,
                              int[] ingredientIds,
                              Model model) throws IOException  {
        if (pizzaIsInvalid(result, image, ingredientIds)) {
            List<Ingredient> ingredients = ingredientService.findAll();
            model.addAttribute("allIngredients", ingredients);
            return "pizza/new";
        }

        setUpPizza(pizza, image, ingredientIds, 0);
        pizzaService.save(pizza);

        return "redirect:/pizzas/manage";
    }

    private boolean pizzaIsInvalid(BindingResult result, MultipartFile file,
                                   int[] ingredientIds) {
        if (result.hasErrors()) {
            return (result.hasFieldErrors("ingredients")
                    && ingredientIds == null)
                    || (result.hasFieldErrors("imageData")
                    && file.isEmpty());
        }

        return false;
    }

    private void setUpPizza(Pizza pizza, MultipartFile image,
                            int[] ingredientIds, int id) throws IOException {
        List<Ingredient> ingredients = new ArrayList<>();
        for (int ingredientId : ingredientIds) {
            Ingredient ingredient = ingredientService.findById(ingredientId).orElseThrow();
            ingredients.add(ingredient);
        }

        pizza.setIngredients(ingredients);
        pizza.setImageData(image.getBytes());
        pizza.setId(id);
    }

    @GetMapping("/edit/{id}")
    public String getPizzaEditForm(@PathVariable int id, Model model) {
        Pizza pizza = pizzaService.findById(id).orElseThrow();
        List<Ingredient> ingredients = ingredientService.findAll();
        model.addAttribute("pizza", pizza);
        model.addAttribute("allIngredients", ingredients);
        return "pizza/edit";
    }

    @PatchMapping("/{id}")
    public String editPizza(@PathVariable int id,
                            @RequestParam MultipartFile file,
                            @ModelAttribute @Valid Pizza pizza,
                            BindingResult result,
                            int[] ingredientIds,
                            Model model) throws IOException {
        if (pizzaIsInvalid(result, file, ingredientIds)) {
            List<Ingredient> ingredients = ingredientService.findAll();
            model.addAttribute("allIngredients", ingredients);
            return "pizza/edit";
        }

        setUpPizza(pizza, file, ingredientIds, id);
        pizzaService.update(pizza);

        return "redirect:/pizzas/manage";
    }

    @DeleteMapping("/{id}")
    public String deletePizza(@PathVariable int id) {
        pizzaService.deleteById(id);
        return "redirect:/pizzas/manage";
    }
}
