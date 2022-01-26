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

import pizzaonline.persistence.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
@Transactional
public class UserService implements ClientUserService, AdminUserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final Validator validator;

    @Autowired
    public UserService(UserRepository repository,
                       PasswordEncoder encoder,
                       Validator validator) {
        this.repository = repository;
        this.encoder = encoder;
        this.validator = validator;
    }

    @Override
    public Optional<User> findById(int id) {
        try {
            return repository.findById(id);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Database unavailable", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            return repository.findByEmail(email);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Database unavailable", e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return repository.findAll();
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Database unavailable", e);
        }
    }

    @Override
    public void authorizeUser(User user) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public void registerUser(User user) {
        validate(user);
        String initialPassword = user.getPassword();
        User.Role initialRole = user.getRole();
        user.setPassword(encoder.encode(initialPassword));
        user.setRole(User.Role.USER);

        Consumer<User> reset = usr -> {
            usr.setRole(initialRole);
            usr.setPassword(initialPassword);
        };

        try {
            save(user);
        } catch (DataIntegrityViolationException e) {
            reset.accept(user);
            throw new IllegalArgumentException("Such a user already exist", e);
        } catch (DataAccessException e) {
            reset.accept(user);
            throw new IllegalArgumentException("Database unavailable", e);
        } catch (Exception e) {
            reset.accept(user);
            throw e;
        }
    }

    private void validate(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<User> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);

            throw new IllegalArgumentException("Invalid data: " + msg);
        }
    }

    private void save(User user) {
        Optional<User> savedUser = repository.findByEmail(user.getEmail());
        if (savedUser.isPresent()) {
            if (savedUser.get().getRole() != User.Role.ANONYMOUS) {
                throw new IllegalArgumentException("Such a user already exists");
            } else {
                user.setId(savedUser.get().getId());
                repository.save(user);
            }
        }

        repository.save(user);
    }

    @Override
    public void updateUserData(User user) {
        Optional<User> savedUser = repository.findById(user.getId());
        User saved = savedUser.orElseThrow(() ->
                new NoSuchElementException("No user with id " + user.getId()));

        validate(user);
        saved.setName(user.getName());
        saved.setPhoneNumber(user.getPhoneNumber());
        saved.setCountry(user.getCountry());
        saved.setCity(user.getCity());
        saved.setStreet(user.getStreet());
        saved.setHouseNumber(user.getHouseNumber());

        try {
            repository.save(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid data", e);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Database unavailable", e);
        }
    }

    @Override
    public void updateLogInData(User user, String currPassword) {
        Optional<User> savedUser = repository.findById(user.getId());
        User saved = savedUser.orElseThrow(() ->
                new NoSuchElementException("No user with id " + user.getId()));
        if (!encoder.matches(currPassword, saved.getPassword())) {
            throw new AccessDeniedException("Invalid password");
        }

        validate(user);
        saved.setEmail(user.getEmail());
        saved.setPassword(user.getPassword());

        try {
            repository.save(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid data", e);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Database unavailable", e);
        }
    }

    @Override
    public void setActivated(int id, boolean isActivated) {
        try {
            Optional<User> user = repository.findById(id);
            User saved = user.orElseThrow(() -> new NoSuchElementException("No user with id " + id));
            repository.save(saved);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Database unavailable", e);
        }
    }
}
