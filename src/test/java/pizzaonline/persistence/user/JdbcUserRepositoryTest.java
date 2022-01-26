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

package pizzaonline.persistence.user;

import category.IntegrationTest;
import pizzaonline.service.user.User;

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
public class JdbcUserRepositoryTest {
    private JdbcUserRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User userToSave;

    @Before
    public void setUp() {
        repository = new JdbcUserRepository(jdbcTemplate);
    }

    @Before
    public void createUser() {
        userToSave = new User();
        userToSave.setEmail(RandomStringUtils.randomAlphabetic(20) + "@gmail.com");
        userToSave.setPassword("some-password");
        userToSave.setName("user");
        userToSave.setPhoneNumber("33445566");
        userToSave.setCountry("some-country");
        userToSave.setCity("some-city");
        userToSave.setStreet("some-street");
        userToSave.setHouseNumber(1);
    }

    @Test
    public void shouldReturnUserWhenContainsIt() {
        Optional<User> user = repository.findById(1);
        assertThat(user, is(not(Optional.empty())));
    }

    @Test
    public void shouldReturnListOfUsersWhenContainsMultipleUsers() {
        List<User> users = repository.findAll();
        assertThat(users.size(), is(not(0)));
    }

    @Test
    public void shouldSaveUserWhenUserIsValid() {
        repository.save(userToSave);

        Optional<User> saved = repository.findById(userToSave.getId());
        assertThat(saved, is(Optional.of(userToSave)));
    }

    @Test
    public void shouldUpdateUserWhenUserIsValid() {
        User updateData = new User(userToSave);
        updateData.setName(RandomStringUtils.randomAlphabetic(20));
        updateData.setCity(RandomStringUtils.randomAlphabetic(10));

        repository.save(userToSave);
        updateData.setId(userToSave.getId());
        repository.save(updateData);

        Optional<User> saved = repository.findById(userToSave.getId());
        assertThat(saved, is(Optional.of(updateData)));
    }

    @Test
    public void shouldNotContainUserWhenDeletesThisUser() {
        repository.save(userToSave);
        repository.deleteById(userToSave.getId());

        Optional<User> deleted = repository.findById(userToSave.getId());
        assertThat(deleted, is(Optional.empty()));
    }
}
