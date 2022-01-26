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

package pizzaonline.persistence.pizza;

import category.IntegrationTest;
import pizzaonline.service.ingredient.Ingredient;
import pizzaonline.service.pizza.Pizza;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@Category(IntegrationTest.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcPizzaRepositoryTest {
    private JdbcPizzaRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Pizza pizzaToSave;

    @Before
    public void setUp() {
        repository = new JdbcPizzaRepository(jdbcTemplate);
    }

    @Before
    public void createPizza() {
        List<Ingredient> ingredients = List.of(
                new Ingredient(1, "White garlic sauce"),
                new Ingredient(2, "Mozzarella")
        );

        pizzaToSave = new Pizza();
        pizzaToSave.setName(RandomStringUtils.randomAlphabetic(20));
        pizzaToSave.setPrice(10);
        pizzaToSave.setIngredients(ingredients);
        pizzaToSave.setImageData(new byte[0]);
    }

    @Test
    public void shouldReturnPizzaWhenContainsIt() {
        Optional<Pizza> pizza = repository.findById(1);
        assertThat(pizza, is(not(Optional.empty())));
    }

    @Test
    public void shouldReturnListOfPizzasWhenContainsMultiplePizzas() {
        List<Pizza> pizzas = repository.findAll();
        assertThat(pizzas.size(), is(not(0)));
    }

    @Test
    public void shouldSavePizzaWhenPizzaIsValid() {
        repository.save(pizzaToSave);

        Optional<Pizza> saved = repository.findById(pizzaToSave.getId());
        assertThat(saved, is(Optional.of(pizzaToSave)));
    }

    @Test
    public void shouldUpdatePizzaWhenPizzaIsValid() {
        Pizza updateData = new Pizza(pizzaToSave);
        updateData.setPrice(111);
        updateData.setName(RandomStringUtils.randomAlphabetic(20));

        repository.save(pizzaToSave);
        updateData.setId(pizzaToSave.getId());
        repository.save(updateData);

        Optional<Pizza> saved = repository.findById(pizzaToSave.getId());
        assertThat(saved, is(Optional.of(updateData)));
    }

    @Test
    public void shouldNotContainPizzaWhenDeletesThisPizza() {
        repository.save(pizzaToSave);
        repository.deleteById(pizzaToSave.getId());

        Optional<Pizza> deleted = repository.findById(pizzaToSave.getId());
        assertThat(deleted, is(Optional.empty()));
    }
}
