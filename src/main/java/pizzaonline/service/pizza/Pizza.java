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

package pizzaonline.service.pizza;

import pizzaonline.service.AbstractProduct;
import pizzaonline.service.ingredient.Ingredient;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * Pizza domain class.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Pizza extends AbstractProduct {

    @NotEmpty(message = "You must specify at least 1 ingredient")
    @Valid
    private List<Ingredient> ingredients;

    public Pizza() {
        ingredients = new ArrayList<>();
    }

    public Pizza(int id,  double price, String name, byte[] imageData,
                 List<Ingredient> ingredients) {
        super(id, price, name, imageData);
        this.ingredients = ingredients;
    }

    public Pizza(Pizza pizza) {
        super(pizza);
        ingredients = new ArrayList<>(pizza.ingredients);
    }

    /**
     * Adds new ingredient to the list of ingredients.
     *
     * @param ingredient ingredient to add
     */
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    /**
     * Creates a new list with the pizza ingredients and returns it.
     *
     * @return list of pizza ingredients
     */
    public List<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }
}
