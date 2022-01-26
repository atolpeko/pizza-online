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

package pizzaonline.service.user;

import org.springframework.security.access.AccessDeniedException;
import java.util.NoSuchElementException;

/**
 * Defines the business logic of the client side of the application.
 * Provides methods for working with users.
 */
public interface ClientUserService extends BaseUserService {

    /**
     * Authorizes the specified user.
     *
     * @param user user to authorize
     */
    void authorizeUser(User user);

    /**
     * Registers the specified user with plain user rights.
     * Saves the user in the repository.
     * Generates a unique ID  and sets it to the user.
     *
     * @param user user to register.
     *
     * @throws IllegalArgumentException either if a user has invalid data or already exists             
     * @throws IllegalStateException if there is any problem with repository
     */
    void registerUser(User user);

    /**
     * Updates the user with the specified ID in the repository.
     * Does not update login data.
     *
     * @param user user to update
     *
     * @throws IllegalArgumentException if a user has invalid data
     * @throws NoSuchElementException if such a user does not exist
     * @throws IllegalStateException if there is any problem with repository
     */
    void updateUserData(User user);

    /**
     * Updates the login data (email and password) of the user with
     * the specified ID in the repository.
     *
     * @param user user to update
     * @param currPassword current password
     *
     * @throws AccessDeniedException if currPassword is not correct
     * @throws IllegalArgumentException if a user has invalid data
     * @throws NoSuchElementException if such a user does not exist
     * @throws IllegalStateException if there is any problem with repository
     */
    void updateLogInData(User user, String currPassword);
}
