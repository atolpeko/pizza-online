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

import category.IntegrationTest;
import pizzaonline.service.drink.Drink;
import pizzaonline.service.ingredient.Ingredient;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import pizzaonline.service.order.Order;
import pizzaonline.service.pizza.Pizza;
import pizzaonline.service.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@Category(IntegrationTest.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcOrderRepositoryTest {
    private JdbcOrderRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Order validOrder;

    @Before
    public void setUp() {
        repository = new JdbcOrderRepository(jdbcTemplate);
    }

    @Before
    public void createOrder() {
        User user = new User();
        user.setId(2);
        user.setRole(User.Role.USER);
        user.setEmail("altolpeko@gmail.com");
        user.setPassword("12345678");
        user.setName("Alexander");
        user.setPhoneNumber("375334455445");
        user.setCountry("Belarus");
        user.setCity("Minsk");
        user.setStreet("Nemiga");
        user.setHouseNumber(3);

        List<Ingredient> ingredients = List.of(
                new Ingredient(1, "White garlic sauce"),
                new Ingredient(2, "Mozzarella")
        );
        Pizza pizza = new Pizza(1, 14, "BBQ CHICKEN BACON FEAST", new byte[0], ingredients);
        Drink drink = new Drink(5, 2, "PEPSI", new byte[0], 250);

        validOrder = new Order();
        LocalDateTime creationTime = LocalDateTime.parse(
                "2021-01-25 13:10", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );
        validOrder.setCreationTime(creationTime);
        validOrder.setUser(user);
        validOrder.addPizza(pizza);
        validOrder.addDrink(drink);
    }

    @Test
    public void shouldReturnOrderWhenContainsIt() {
        Optional<Order> order = repository.findById(1);
        assertThat(order, is(not(Optional.empty())));
    }

    @Test
    public void shouldReturnListOfOrdersWhenContainsMultipleOrders() {
        List<Order> orders = repository.findAll();
        assertThat(orders.size(), is(not(0)));
    }

    @Test
    public void shouldSaveOrderWhenOrderIsValid() {
        repository.save(validOrder);

        Optional<Order> saved = repository.findById(validOrder.getId());
        assertThat(saved, is(Optional.of(validOrder)));
    }
}
