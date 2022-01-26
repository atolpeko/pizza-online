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

package pizzaonline.persistence.ingredient;

import category.IntegrationTest;
import pizzaonline.service.ingredient.Ingredient;

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
public class JdbcIngredientRepositoryTest {
    private JdbcIngredientRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        repository = new JdbcIngredientRepository(jdbcTemplate);
    }

    @Test
    public void shouldReturnIngredientWhenContainsIt() {
        Optional<Ingredient> ingredient = repository.findById(1);
        assertThat(ingredient, is(not(Optional.empty())));
    }

    @Test
    public void shouldReturnListOfIngredientsWhenContainsMultipleIngredients() {
        List<Ingredient> ingredients = repository.findAll();
        assertThat(ingredients.size(), is(not(0)));
    }

    @Test
    public void shouldSaveIngredientWhenIngredientIsValid() {
        Ingredient ingredient = new Ingredient(0, RandomStringUtils.randomAlphabetic(20));
        repository.save(ingredient);

        Optional<Ingredient> saved = repository.findById(ingredient.getId());
        assertThat(saved, is(Optional.of(ingredient)));
    }

    @Test
    public void shouldUpdateIngredientWhenIngredientIsValid() {
        Ingredient ingredient = new Ingredient(0, RandomStringUtils.randomAlphabetic(20));
        repository.save(ingredient);

        Ingredient updateData = new Ingredient(
                ingredient.getId(), RandomStringUtils.randomAlphabetic(20)
        );
        repository.save(updateData);

        Optional<Ingredient> saved = repository.findById(ingredient.getId());
        assertThat(saved, is(Optional.of(updateData)));

    }

    @Test
    public void shouldNotContainIngredientWhenDeletesThisIngredient() {
        Ingredient ingredient = new Ingredient(0, RandomStringUtils.randomAlphabetic(20));
        repository.save(ingredient);
        repository.deleteById(ingredient.getId());

        Optional<Ingredient> deleted = repository.findById(ingredient.getId());
        assertThat(deleted, is(Optional.empty()));
    }
}
