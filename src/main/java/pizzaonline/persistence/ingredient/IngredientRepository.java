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

import pizzaonline.service.ingredient.Ingredient;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

/**
 * An IngredientRepository abstracts a collection of Ingredient objects.
 */
public interface IngredientRepository {

    /**
     * Retrieves an ingredient by its ID.
     *
     * @param id the ID of the ingredient to find
     *
     * @return the ingredient with the given ID or {@link Optional#empty()} if none found
     *
     * @throws DataAccessException if there is any problem with repository
     */
    Optional<Ingredient> findById(int id);

    /**
     * Retrieves an ingredient by its name.
     *
     * @param name the name of the ingredient to find
     *
     * @return the ingredient with the given name or {@link Optional#empty()} if none found
     *
     * @throws DataAccessException if there is any problem with repository
     */
    Optional<Ingredient> findByName(String name);

    /**
     * Retrieves all ingredients from the repository.
     *
     * @return all ingredients from the repository
     *
     * @throws DataAccessException if there is any problem with repository
     */
    List<Ingredient> findAll();

    /**
     * Saves the specified ingredient in the repository.
     * Generates a unique ID  and sets it to the ingredient.
     * <p>
     * If the ingredient already exist in the repository, updates its data
     * using the data of the passed argument.
     *
     * @param ingredient ingredient to save
     *
     * @throws DataIntegrityViolationException if an ingredient has invalid data
     * @throws DataAccessException if there is any problem with repository
     */
    void save(Ingredient ingredient);

    /**
     * Deletes the ingredient with the given ID.
     *
     * @param id the ID of the ingredient to delete
     *
     * @throws DataAccessException if there is any problem with repository
     */
    void deleteById(int id);
}
