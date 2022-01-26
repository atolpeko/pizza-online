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

package pizzaonline.service.order;

import pizzaonline.persistence.order.OrderRepository;
import pizzaonline.persistence.user.UserRepository;
import pizzaonline.service.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class OrderService implements ClientOrderService, AdminOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final Validator validator;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        PasswordEncoder encoder,
                        Validator validator) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.validator = validator;
    }

    @Override
    public void submit(Order order) {
        validate(order);
        try {
            User user = order.getUser();
            if (!user.isEnabled()) {
                throw new AccessDeniedException("User account is locked");
            }

            Optional<User> savedUser = userRepository.findByEmail(user.getEmail());
            if (savedUser.isPresent()) {
                user = savedUser.get();
                order.setUser(user);
            } else {
                user.setPassword(encoder.encode(user.getPassword()));
                userRepository.save(user);
            }

            order.setCreationTime(LocalDateTime.now());
            orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid data", e);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Database unavailable", e);
        }
    }

    private void validate(Order order) {
        if (order.getUser() == null) {
            throw new IllegalArgumentException("Invalid data: no user information");
        }

        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<Order> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);

            throw new IllegalArgumentException("Invalid data: " + msg);
        }
    }

    @Override
    public Optional<Order> findById(int id) {
        try {
            return orderRepository.findById(id);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Database unavailable", e);
        }
    }

    @Override
    public List<Order> findAll() {
        try {
            return orderRepository.findAll();
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Database unavailable", e);
        }
    }
}
