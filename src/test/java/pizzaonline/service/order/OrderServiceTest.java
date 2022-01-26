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

import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    private OrderService orderService;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private Validator validator;
    private PasswordEncoder encoder;

    private Order order;

    @Before
    public void setUp() {
        encoder = mock(PasswordEncoder.class);
        when(encoder.encode(anyString())).then(returnsFirstArg());

        orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);
        validator = mock(Validator.class);
        orderService = new OrderService(orderRepository, userRepository, encoder, validator);
    }

    @Before
    public void createOrder() {
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setPassword("some-password");
        user.setName("user");
        user.setPhoneNumber("33445566");
        user.setCountry("some-country");
        user.setCity("some-city");
        user.setStreet("some-street");
        user.setHouseNumber(1);

        order = new Order();
        order.setUser(user);
        order.setId(0);
        order.setPrice(100);
        order.setCreationTime(LocalDateTime.now());
    }

    @Test
    public void shouldReturnOrderWhenContainsIt() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Order saved = orderService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(saved)));
    }

    @Test
    public void shouldReturnListOfOrdersWhenContainsMultipleOrders() {
        List<Order> orders = List.of(order, order, order);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> saved = orderService.findAll();
        assertThat(saved.size(), is(3));
    }

    @Test
    public void shouldSubmitsOrderWhenOrderIsValid() {
        when(validator.validate(order)).thenReturn(Collections.emptySet());
        doAnswer(invocation -> when(orderRepository.findById(1)).thenReturn(Optional.of(order)))
                .when(orderRepository).save(order);

        orderService.submit(order);

        Order saved = orderService.findById(1).orElseThrow();
        assertThat(saved, equalTo(order));
    }

    @Test
    public void shouldSaveCustomerWhenSubmitsOrder() {
        when(validator.validate(order)).thenReturn(Collections.emptySet());
        UserRepository spy = spy(userRepository);
        OrderService service = new OrderService(orderRepository, spy, encoder, validator);

        service.submit(order);

        verify(spy).save(order.getUser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenOrderIsInvalid() {
        Order invalidOrder = new Order();
        when(validator.validate(invalidOrder)).thenThrow(IllegalArgumentException.class);

        orderService.submit(invalidOrder);
    }
}
