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

import pizzaonline.service.pizza.Pizza;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

/**
 * A PizzaRepository abstracts a collection of Pizza objects.
 */
public interface PizzaRepository {

    /**
     * Retrieves a pizza by its ID.
     *
     * @param id the ID of the pizza to find
     *
     * @return the pizza with the given ID or {@link Optional#empty()} if none found
     *
     * @throws DataAccessException if there is any problem with repository
     */
    Optional<Pizza> findById(int id);

    /**
     * Retrieves all pizzas from the repository.
     *
     * @return all pizzas from the repository
     *
     * @throws DataAccessException if there is any problem with repository
     */
    List<Pizza> findAll();

    /**
     * Retrieves all pizzas with the same name from the repository.
     *
     * @param name pizza name
     *             
     * @return all pizzas with the same name from the repository
     *
     * @throws DataAccessException if there is any problem with repository
     */
    List<Pizza> findAllByName(String name);

    /**
     * Saves the specified pizza in the repository.
     * Generates a unique ID  and sets it to the pizza.
     * <p>
     * If the pizza already exist in the repository, updates its data
     * using the data of the passed argument.
     *
     * @param pizza pizza to save
     *
     * @throws DataIntegrityViolationException if the pizza has invalid data 
     * @throws DataAccessException if there is any problem with repository
     */
    void save(Pizza pizza);

    /**
     * Deletes the pizza with the given ID.
     *
     * @param id the ID of the pizza to delete
     *
     * @throws DataAccessException if there is any problem with repository
     */
    void deleteById(int id);
}
