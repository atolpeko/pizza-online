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

import category.UnitTest;

import pizzaonline.service.drink.Drink;
import pizzaonline.service.ingredient.Ingredient;
import pizzaonline.service.pizza.Pizza;
import pizzaonline.service.user.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(UnitTest.class)
public class OrderTest {
    private Validator validator;
    private Order validOrder;

    @Before
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Before
    public void createValidOrder() {
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setPassword("some-password");
        user.setName("user");
        user.setPhoneNumber("33445566");
        user.setCountry("some-country");
        user.setCity("some-city");
        user.setStreet("some-street");
        user.setHouseNumber(1);

        List<Ingredient> ingredients = List.of(
                new Ingredient(0, "1"),
                new Ingredient(2, "2"),
                new Ingredient(3, "3")
        );
        Pizza pizza = new Pizza(0, 100, "Pizza", new byte[0], ingredients);
        Drink drink = new Drink(0, 100, "Drink", new byte[0], 250);

        validOrder = new Order();
        validOrder.setUser(user);
        validOrder.addPizza(pizza);
        validOrder.addDrink(drink);
    }

    @Test
    public void shouldPassValidationWhenHasValidData() {
        Set<ConstraintViolation<Order>> violations = validator.validate(validOrder);
        assertThat(violations.isEmpty(), is(true));
    }

    @Test
    public void shouldNotPassValidationWhenHasInvalidData() {
        Order order = new Order();
        order.addPizza(new Pizza());
        order.addDrink(new Drink());

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertThat(violations.size(), is(9));
    }

}
