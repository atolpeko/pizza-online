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

package pizzaonline.persistence.order;

import pizzaonline.service.order.Order;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

/**
 * An OrderRepository abstracts a collection of Order objects.
 */
public interface OrderRepository {

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to find
     *
     * @return the order with the given ID or {@link Optional#empty()} if none found
     *
     * @throws DataAccessException if there is any problem with repository
     */
    Optional<Order> findById(int id);

    /**
     * Returns all orders from the repository.
     *
     * @return all orders from the repository
     *
     * @throws DataAccessException if there is any problem with repository
     */
    List<Order> findAll();

    /**
     * Saves the specified order in the repository.
     * Generates a unique ID  and sets it to the order.
     * <p>
     * If the order already exist in the repository, updates its data
     * using the data of the passed argument.
     *
     * @param order order to be saved
     *
     * @throws DataIntegrityViolationException if an order has invalid data
     * @throws DataAccessException if there is any problem with repository
     */
    void save(Order order);
}
