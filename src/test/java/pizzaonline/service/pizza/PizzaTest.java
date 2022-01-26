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

import category.UnitTest;
import pizzaonline.service.ingredient.Ingredient;

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
public class PizzaTest {
    private Validator validator;

    @Before
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldPassValidationWhenHasValidData() {
        List<Ingredient> ingredients = List.of(
                new Ingredient(0, "1"),
                new Ingredient(2, "2"),
                new Ingredient(3, "3")
        );

        Pizza pizza = new Pizza(0, 100, "Pizza", new byte[0], ingredients);
        Set<ConstraintViolation<Pizza>> violations = validator.validate(pizza);

        assertThat(violations.isEmpty(), is(true));
    }

    @Test
    public void shouldNotPassValidationWhenHasInvalidData() {
        List<Ingredient> ingredients = List.of(
                new Ingredient(),
                new Ingredient(),
                new Ingredient()
        );

        Pizza pizza = new Pizza();
        pizza.setIngredients(ingredients);
        Set<ConstraintViolation<Pizza>> violations = validator.validate(pizza);

        assertThat(violations.size(), is(6));
    }
}
