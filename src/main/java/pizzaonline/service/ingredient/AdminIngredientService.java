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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Defines the business logic of the admin side of the application.
 * Provides methods for working with ingredients.
 */
public interface AdminIngredientService {

    /**
     * Looks for an ingredient with the specified ID in the repository.
     *
     * @param id ID of the ingredient to get
     *
     * @return the ingredient with the specified ID in the repository
     *
     * @throws IllegalStateException if there is any problem with repository
     */
    Optional<Ingredient> findById(int id);

    /**
     * Looks for all ingredients in the repository.
     *
     * @return all ingredients from the repository
     *
     * @throws IllegalStateException if there is any problem with repository
     */
    List<Ingredient> findAll();

    /**
     * Saves the specified ingredient in the repository.
     * Generates a unique ID  and sets it to the ingredient.
     *
     * @param ingredient ingredient to be saved
     *
     * @throws IllegalArgumentException either if a ingredient has invalid data or already exists
     * @throws IllegalStateException if there is any problem with repository
     */
    void save(Ingredient ingredient);

    /**
     * Updates the ingredient with the specified ID in the repository.
     *
     * @param ingredient ingredient to update
     *
     * @throws IllegalArgumentException if an ingredient has invalid data
     * @throws NoSuchElementException if such an ingredient does not exist
     * @throws IllegalStateException if there is any problem with repository
     */
    void update(Ingredient ingredient);

    /**
     * Deletes the ingredient with the specified ID in the repository.
     * Does nothing if such an ingredient does not exist.
     *
     * @param id the ID of the ingredient to be deleted
     *
     * @throws IllegalStateException if there is any problem with repository
     */
    void deleteById(int id);
}
