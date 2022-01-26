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

import category.UnitTest;
import pizzaonline.persistence.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class UserServiceTest {
    private UserService userService;
    private UserRepository repository;
    private Validator validator;

    private User user;

    @Before
    public void setUp() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.encode(anyString())).then(returnsFirstArg());
        when(encoder.matches(anyString(), anyString())).then(invocation -> {
            String rawPassword = invocation.getArgument(0);
            String encodedPassword = invocation.getArgument(1);
            return rawPassword.equals(encodedPassword);
        });

        repository = mock(UserRepository.class);
        validator = mock(Validator.class);
        userService = new UserService(repository, encoder, validator);
    }

    @Before
    public void createUser() {
        user = new User();
        user.setEmail("user@gmail.com");
        user.setPassword("some-password");
        user.setName("user");
        user.setPhoneNumber("33445566");
        user.setCountry("some-country");
        user.setCity("some-city");
        user.setStreet("some-street");
        user.setHouseNumber(1);
    }

    @Test
    public void shouldReturnUserWhenContainsIt() {
        when(repository.findById(1)).thenReturn(Optional.of(user));

        User saved = userService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(user)));
    }

    @Test
    public void shouldReturnListOfUsersWhenContainsMultipleUsers() {
        List<User> users = List.of(user, user, user);
        when(repository.findAll()).thenReturn(users);

        List<User> saved = userService.findAll();
        assertThat(saved.size(), is(3));
    }

    @Test
    public void shouldRegistersUserThenThisUserIsValid() {
        when(validator.validate(user)).thenReturn(Collections.emptySet());
        doAnswer(invocation -> when(repository.findById(1)).thenReturn(Optional.of(user)))
                .when(repository).save(user);

        userService.registerUser(user);

        User saved = repository.findById(1).orElseThrow();
        assertThat(saved, equalTo(user));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUserIsInvalid() {
        User invalidUser = new User();
        when(validator.validate(invalidUser)).thenThrow(IllegalArgumentException.class);

        userService.registerUser(invalidUser);
    }

    @Test
    public void shouldUpdateUserWhenUserIsValid() {
        User updated = new User();
        updated.setEmail("user2@gmail.com");
        updated.setPassword("some-other-password");
        updated.setName("user2");
        updated.setPhoneNumber("3443445566");
        updated.setCountry("some-other-country");
        updated.setCity("some-other-city");
        updated.setStreet("some-other-street");
        updated.setHouseNumber(2);

        when(repository.findById(0)).thenReturn(Optional.of(user));
        doAnswer(invocation -> when(repository.findById(0)).thenReturn(Optional.of(updated)))
                .when(repository).save(updated);

        userService.updateUserData(updated);
        userService.updateLogInData(updated, user.getPassword());

        User user = repository.findById(0).orElseThrow();
        assertThat(user, equalTo(updated));
    }
}
