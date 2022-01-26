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

import java.util.NoSuchElementException;

/**
 * Defines the business logic of the admin side of the application.
 * Provides methods for working with drinks.
 */
public interface AdminDrinkService extends BaseDrinkService {

    /**
     * Saves the specified drink in the repository.
     * Generates a unique ID  and sets it to the drink.
     *
     * @param drink drink to be saved
     *
     * @throws IllegalArgumentException either if a drink has invalid data or already exists
     * @throws IllegalStateException if there is any problem with repository
     */
    void save(Drink drink);

    /**
     * Updates the drink with the specified ID in the repository.
     *
     * @param drink drink to update
     *
     * @throws IllegalArgumentException if a drink has invalid data
     * @throws NoSuchElementException if such a drink does not exist
     * @throws IllegalStateException if there is any problem with repository
     */
    void update(Drink drink);

    /**
     * Deletes the drink with the specified ID in the repository.
     * Does nothing if such a drink does not exist.
     *
     * @param id the ID of the drink to be deleted
     *
     * @throws IllegalStateException if there is any problem with repository
     */
    void deleteById(int id);
}
