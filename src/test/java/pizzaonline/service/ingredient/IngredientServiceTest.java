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

package pizzaonline.service.ingredient;

import category.UnitTest;

import pizzaonline.persistence.ingredient.IngredientRepository;

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
public class IngredientServiceTest {
    private IngredientService ingredientService;
    private IngredientRepository repository;
    private Validator validator;

    @Before
    public void setUp() {
        repository = mock(IngredientRepository.class);
        validator = mock(Validator.class);
        ingredientService = new IngredientService(repository, validator);
    }

    @Test
    public void shouldReturnIngredientWhenContainsIt() {
        Ingredient ingredient = new Ingredient(1, "Ingredient");
        when(repository.findById(1)).thenReturn(Optional.of(ingredient));

        Ingredient saved = ingredientService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(ingredient)));
    }

    @Test
    public void shouldReturnListOfIngredientsWhenContainsMultipleIngredients() {
        List<Ingredient> ingredients = List.of(new Ingredient(), new Ingredient(), new Ingredient());
        when(repository.findAll()).thenReturn(ingredients);

        List<Ingredient> saved = ingredientService.findAll();
        assertThat(saved.size(), is(3));
    }

    @Test
    public void shouldSaveIngredientWhenIngredientIsValid() {
        Ingredient validIngredient = new Ingredient(1, "Ingredient");
        when(validator.validate(validIngredient)).thenReturn(Collections.emptySet());
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.of(validIngredient)))
                .when(repository).save(validIngredient);

        ingredientService.save(validIngredient);

        Ingredient saved = repository.findById(1).orElseThrow();
        assertThat(saved, equalTo(validIngredient));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenIngredientIsInvalid() {
        Ingredient invalidIngredient = new Ingredient();
        when(validator.validate(invalidIngredient)).thenThrow(IllegalArgumentException.class);

        ingredientService.save(invalidIngredient);
    }

    @Test
    public void shouldUpdateIngredientWhenIngredientIsValid() {
        Ingredient saved = new Ingredient(1, "Ingredient1");
        Ingredient updated =  new Ingredient(1, "Ingredient2");
        when(repository.findById(1)).thenReturn(Optional.of(saved));
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.of(updated)))
                .when(repository).save(updated);

        ingredientService.update(updated);

        Ingredient ingredient = repository.findById(1).orElseThrow();
        assertThat(ingredient, equalTo(updated));
    }

    @Test
    public void shouldNotContainIngredientWhenDeletesThisIngredient() {
        Ingredient saved = new Ingredient(1, "Ingredient1");
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(saved));
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.empty()))
                .when(repository).deleteById(1);

        ingredientService.deleteById(1);

        Optional<Ingredient> deletedIngredient = ingredientService.findById(1);
        assertThat(deletedIngredient, is(Optional.empty()));
    }
}