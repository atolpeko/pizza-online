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

import pizzaonline.service.drink.Drink;
import pizzaonline.service.order.ClientOrderService;
import pizzaonline.service.order.Order;
import pizzaonline.service.pizza.Pizza;
import pizzaonline.service.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
@SessionAttributes("order")
public class CartController {
    private final ClientOrderService orderService;

    @Autowired
    public CartController(ClientOrderService orderService) {
        this.orderService = orderService;
    }

    @ModelAttribute
    public Order order(@SessionAttribute Optional<User> user) {
        Order order = new Order();
        order.setUser(user.map(User::new).orElseGet(User::new));
        return order;
    }

    @GetMapping
    public String getCart(@ModelAttribute Order order) {
        return "cart/cart";
    }

    @GetMapping("/submit")
    public String getSubmissionForm(@ModelAttribute Order order,
                                    @SessionAttribute Optional<User> user) {
        user.ifPresent(order::setUser);
        if (!order.isEmpty()) {
            return "cart/order-details";
        } else {
            return "redirect:/cart";
        }
    }

    @PostMapping("/submit")
    public String submitOrder(@ModelAttribute @Valid Order order,
                              BindingResult result, SessionStatus status) {
        if (order.isEmpty()) {
            return "redirect:/cart";
        }

        if (result.hasErrors()) {
            return "cart/order-details";
        }

        orderService.submit(order);
        status.setComplete();

        return "cart/submitted";
    }

    @PostMapping("/add-pizza")
    public String addPizzaToOrder(@SessionAttribute Pizza pizza,
                                  @ModelAttribute Order order) {
        order.addPizza(pizza);
        return "redirect:/pizzas";
    }

    @DeleteMapping("/pizza/{id}")
    public String deletePizza(@PathVariable int id,
                              @ModelAttribute Order order) {
        order.getPizzas().forEach(pizza -> {
            if (pizza.getId() == id) {
                order.removePizza(pizza);
            }
        });

        return "cart/cart";
    }

    @PostMapping("/add-drink")
    public String addDrinkToOrder(@SessionAttribute Drink drink,
                                  @ModelAttribute Order order) {
        order.addDrink(drink);
        return "redirect:/drinks";
    }

    @DeleteMapping("/drink/{id}")
    public String deleteDrink(@PathVariable int id,
                              @ModelAttribute Order order) {
        order.getDrinks().forEach(drink -> {
            if (drink.getId() == id) {
                order.removeDrink(drink);
            }
        });

        return "cart/cart";
    }
}
