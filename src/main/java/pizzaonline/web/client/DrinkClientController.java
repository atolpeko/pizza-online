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

import pizzaonline.service.drink.ClientDrinkService;
import pizzaonline.service.drink.Drink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@Controller
@RequestMapping("/drinks")
@SessionAttributes("drink")
public class DrinkClientController {
    private final ClientDrinkService drinkService;

    @Autowired
    public DrinkClientController(ClientDrinkService drinkService) {
        this.drinkService = drinkService;
    }

    @GetMapping
    public String getAllDrinks(Model model) {
        List<Drink> drinks = drinkService.findAll();
        model.addAttribute("drinks", drinks);
        return "drink/drinks";
    }

    @GetMapping("/{id}")
    public String getDrink(@PathVariable int id, Model model) {
        Drink drink = drinkService.findById(id).orElseThrow();
        List<Drink> allDrinks = drinkService.findAll();
        model.addAttribute("drink", drink);
        model.addAttribute("allDrinks", allDrinks);
        return "drink/drink-info";
    }
}
