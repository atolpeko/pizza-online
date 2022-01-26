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

package pizzaonline.service.order;

import pizzaonline.service.drink.Drink;
import pizzaonline.service.pizza.Pizza;
import pizzaonline.service.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order domain class.
 */
@Data
@AllArgsConstructor
public class Order implements Serializable  {
    private int id;
    private double price;
    private LocalDateTime creationTime;
    private String message;

    @Valid
    private List<Pizza> pizzas;

    @Valid
    private List<Drink> drinks;

    @Valid
    @NotNull
    private User user;

    public Order() {
        pizzas = new ArrayList<>();
        drinks = new ArrayList<>();
    }

    /**
     * Adds new pizza to this order.
     *
     * @param pizza pizza to add
     */
    public void addPizza(Pizza pizza) {
        pizzas.add(pizza);
        price += pizza.getPrice();
    }

    /**
     * Removes the specified pizza from this order.
     *
     * @param pizza pizza to remove
     */
    public void removePizza(Pizza pizza) {
        pizzas.remove(pizza);
        price -= pizza.getPrice();
    }

    /**
     * Adds new drink to this order.
     *
     * @param drink drink to add
     */
    public void addDrink(Drink drink) {
        drinks.add(drink);
        price += drink.getPrice();
    }

    /**
     * Removes the specified drink from this order.
     *
     * @param drink drink to remove
     */
    public void removeDrink(Drink drink) {
        drinks.remove(drink);
        price -= drink.getPrice();
    }

    /**
     * Creates a new list with the ordered pizzas and returns it.
     *
     * @return list of the ordered pizzas
     */
    public List<Pizza> getPizzas() {
        return new ArrayList<>(pizzas);
    }

    /**
     * Sets the ordered pizzas.
     *
     * @param pizzas pizzas to set
     */
    public void setPizzas(List<Pizza> pizzas) {
        price -= this.pizzas.stream()
                .mapToDouble(Pizza::getPrice)
                .sum();
        this.pizzas = pizzas;
        price += pizzas.stream()
                .mapToDouble(Pizza::getPrice)
                .sum();
    }

    /**
     * Creates a new list with the ordered drinks and returns it.
     *
     * @return list of the ordered drinks
     */
    public List<Drink> getDrinks() {
        return new ArrayList<>(drinks);
    }

    /**
     * Sets the ordered drinks.
     *
     * @param drinks drinks to set
     */
    public void setDrinks(List<Drink> drinks) {
        price -= this.drinks.stream()
                .mapToDouble(Drink::getPrice)
                .sum();
        this.drinks = drinks;
        price += drinks.stream()
                .mapToDouble(Drink::getPrice)
                .sum();
    }

    /**
     * @return true if there is no products in this order, false otherwise
     */
    public boolean isEmpty() {
        return pizzas.isEmpty() && drinks.isEmpty();
    }
}
