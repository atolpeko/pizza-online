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
import pizzaonline.persistence.pizza.PizzaRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pizzaonline.service.ingredient.Ingredient;

import javax.validation.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class PizzaServiceTest {
    private PizzaService pizzaService;
    private PizzaRepository repository;
    private Validator validator;

    private List<Ingredient> ingredients;

    @Before
    public void setUp() {
        repository = mock(PizzaRepository.class);
        validator = mock(Validator.class);
        pizzaService = new PizzaService(repository, validator);
    }

    @Before
    public void createIngredients() {
        ingredients = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ingredients.add(new Ingredient(1, "Ingredient" + i));
        }
    }

    @Test
    public void shouldReturnPizzaWhenContainsIt() {
        Pizza pizza = new Pizza(1, 100, "Pizza", new byte[0], ingredients);
        when(repository.findById(1)).thenReturn(Optional.of(pizza));

        Pizza saved = pizzaService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(pizza)));
    }

    @Test
    public void shouldReturnListOfPizzasWhenContainsMultiplePizzas() {
        List<Pizza> pizzas = List.of(new Pizza(), new Pizza(), new Pizza());
        when(repository.findAll()).thenReturn(pizzas);

        List<Pizza> saved = pizzaService.findAll();
        assertThat(saved.size(), is(3));
    }

    @Test
    public void shouldSavePizzaWhenPizzaIsValid() {
        Pizza validPizza = new Pizza(1, 100, "Pizza", new byte[0], ingredients);
        when(validator.validate(validPizza)).thenReturn(Collections.emptySet());
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.of(validPizza)))
                .when(repository).save(validPizza);

        pizzaService.save(validPizza);

        Pizza saved = repository.findById(1).orElseThrow();
        assertThat(saved, equalTo(validPizza));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenPizzaIsInvalid() {
        Pizza invalidPizza = new Pizza();
        when(validator.validate(invalidPizza)).thenThrow(IllegalArgumentException.class);

        pizzaService.save(invalidPizza);
    }

    @Test
    public void shouldUpdatePizzaWhenPizzaIsValid() {
        Pizza saved = new Pizza(1, 100, "Pizza", new byte[0], ingredients);
        Pizza updated = new Pizza(1, 200, "Pizza2", new byte[0], ingredients.subList(0, 1));
        when(repository.findById(1)).thenReturn(Optional.of(saved));
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.of(updated)))
                .when(repository).save(updated);

        pizzaService.update(updated);

        Pizza pizza = repository.findById(1).orElseThrow();
        assertThat(pizza, equalTo(updated));
    }

    @Test
    public void shouldNotContainPizzaWhenDeletesThisPizza() {
        Pizza saved = new Pizza(1, 100, "Pizza", new byte[0], ingredients);
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(saved));
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.empty()))
                .when(repository).deleteById(1);

        pizzaService.deleteById(1);

        Optional<Pizza> deletedPizza = pizzaService.findById(1);
        assertThat(deletedPizza, is(Optional.empty()));
    }
}
