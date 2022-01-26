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

package pizzaonline.service.drink;

import category.UnitTest;

import pizzaonline.persistence.drink.DrinkRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.validation.Validator;

import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class DrinkServiceTest {
    private DrinkService drinkService;
    private DrinkRepository repository;
    private Validator validator;

    @Before
    public void setUp() {
        repository = mock(DrinkRepository.class);
        validator = mock(Validator.class);
        drinkService = new DrinkService(repository, validator);
    }

    @Test
    public void shouldReturnDrinkWhenContainsIt() {
        Drink drink = new Drink(1, 100, "Drink", new byte[0], 250);
        when(repository.findById(1)).thenReturn(Optional.of(drink));

        Drink saved = drinkService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(drink)));
    }

    @Test
    public void shouldReturnListOfDrinksWhenContainsMultipleDrinks() {
        List<Drink> drinks = List.of(new Drink(), new Drink(), new Drink());
        when(repository.findAll()).thenReturn(drinks);

        List<Drink> saved = drinkService.findAll();
        assertThat(saved.size(), is(3));
    }

    @Test
    public void shouldSaveDrinkWhenDrinkIsValid() {
        Drink validDrink = new Drink(1, 100, "Drink", new byte[0], 250);
        when(validator.validate(validDrink)).thenReturn(Collections.emptySet());
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.of(validDrink)))
                .when(repository).save(validDrink);

        drinkService.save(validDrink);

        Drink saved = repository.findById(1).orElseThrow();
        assertThat(saved, equalTo(validDrink));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDrinkIsInvalid() {
        Drink invalidDrink = new Drink();
        when(validator.validate(invalidDrink)).thenThrow(IllegalArgumentException.class);

        drinkService.save(invalidDrink);
    }

    @Test
    public void shouldUpdateDrinkWhenDrinkIsValid() {
        Drink saved = new Drink(1, 100, "Drink", new byte[0], 250);
        Drink updated = new Drink(1, 200, "Drink2", new byte[0], 350);
        when(repository.findById(1)).thenReturn(Optional.of(saved));
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.of(updated)))
                .when(repository).save(updated);

        drinkService.update(updated);

        Drink drink = repository.findById(1).orElseThrow();
        assertThat(drink, equalTo(updated));
    }

    @Test
    public void shouldNotContainDrinkWhenDeletesThisDrink() {
        Drink saved = new Drink(1, 100, "Drink", new byte[0], 250);
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(saved));
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.empty()))
                .when(repository).deleteById(1);

        drinkService.deleteById(1);

        Optional<Drink> deletedDrink = drinkService.findById(1);
        assertThat(deletedDrink, is(Optional.empty()));
    }
}
