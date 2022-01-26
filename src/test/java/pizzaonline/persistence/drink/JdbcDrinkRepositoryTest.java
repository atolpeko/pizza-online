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

package pizzaonline.persistence.drink;

import category.IntegrationTest;
import pizzaonline.service.drink.Drink;

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
public class JdbcDrinkRepositoryTest {
    private JdbcDrinkRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Drink drinkToSave;

    @Before
    public void setUp() {
        repository = new JdbcDrinkRepository(jdbcTemplate);
    }

    @Before
    public void createDrink() {
        drinkToSave = new Drink();
        drinkToSave.setName(RandomStringUtils.randomAlphabetic(20));
        drinkToSave.setPrice(10);
        drinkToSave.setImageData(new byte[0]);
        drinkToSave.setCapacity(200);
    }

    @Test
    public void shouldReturnDrinkWhenContainsIt() {
        Optional<Drink> drink = repository.findById(5);
        assertThat(drink, is(not(Optional.empty())));
    }

    @Test
    public void shouldReturnListOfDrinksWhenContainsMultipleDrinks() {
        List<Drink> drinks = repository.findAll();
        assertThat(drinks.size(), is(not(0)));
    }

    @Test
    public void shouldSaveDrinkWhenDrinkIsValid() {
        repository.save(drinkToSave);

        Optional<Drink> saved = repository.findById(drinkToSave.getId());
        assertThat(saved, is(Optional.of(drinkToSave)));
    }

    @Test
    public void shouldUpdateDrinkWhenDrinkIsValid() {
        repository.save(drinkToSave);
        Drink updateData = new Drink(
                drinkToSave.getId(), 15, RandomStringUtils.randomAlphabetic(20), new byte[1], 120
        );
        repository.save(updateData);

        Optional<Drink> saved = repository.findById(drinkToSave.getId());
        assertThat(saved, is(Optional.of(updateData)));

    }

    @Test
    public void shouldNotContainDrinkWhenDeletesThisDrink() {
        repository.save(drinkToSave);
        repository.deleteById(drinkToSave.getId());

        Optional<Drink> deleted = repository.findById(drinkToSave.getId());
        assertThat(deleted, is(Optional.empty()));
    }
}
