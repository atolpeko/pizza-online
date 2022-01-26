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

import pizzaonline.service.AbstractProduct;
import pizzaonline.service.drink.Drink;
import pizzaonline.service.order.Order;
import pizzaonline.service.ingredient.Ingredient;
import pizzaonline.service.pizza.Pizza;
import pizzaonline.service.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Database-based Order object repository.
 */
@Repository
public class JdbcOrderRepository implements OrderRepository {
    private final JdbcTemplate jdbc;
    private final ResultSetExtractor<List<Order>> extractor;

    @Autowired
    public JdbcOrderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        extractor = resultSet -> {
            Map<Integer, List<Pizza>> pizzasByOrder = extractPizzas(resultSet);
            Map<Integer, List<Drink>> drinksByOrder = extractDrinks(resultSet);
            List<Order> orders = new ArrayList<>();
            resultSet.beforeFirst();
            while (resultSet.next()) {
                int orderId = resultSet.getInt("o.id");
                Optional<Order> order = orders.stream()
                        .filter(item -> item.getId() == orderId)
                        .findFirst();
                if (order.isEmpty()) {
                    Order newOrder = extractOrder(resultSet);
                    newOrder.setUser(extractUser(resultSet));
                    Optional<List<Pizza>> pizzas =
                            Optional.ofNullable(pizzasByOrder.get(newOrder.getId()));
                    pizzas.ifPresent(newOrder::setPizzas);

                    Optional<List<Drink>> drinks =
                            Optional.ofNullable(drinksByOrder.get(newOrder.getId()));
                    drinks.ifPresent(newOrder::setDrinks);

                    orders.add(newOrder);
                }
            }

            return orders;
        };
    }

    private Map<Integer, List<Drink>> extractDrinks(ResultSet resultSet) throws SQLException {
        Map<Integer, List<Drink>> drinksByOrder = new HashMap<>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            String type = resultSet.getString("type");
            if (type == null || !type.equals("drink")) {
                continue;
            }

            int orderId = resultSet.getInt("o.id");
            if (!drinksByOrder.containsKey(orderId)) {
                drinksByOrder.put(orderId, new ArrayList<>());
            }

            Drink drink = new Drink();
            drink.setId(resultSet.getInt("p.id"));
            drink.setName(resultSet.getString("p.name"));
            drink.setPrice(resultSet.getDouble("p.price"));
            drink.setImageData(resultSet.getBytes("img_data"));
            drink.setCapacity(resultSet.getDouble("size"));

            int drinksNumber = resultSet.getInt("op.count");
            for (int i = 0; i < drinksNumber; i++) {
                drinksByOrder.get(orderId).add(new Drink(drink));
            }
        }

        return drinksByOrder;
    }

    private Map<Integer, List<Pizza>> extractPizzas(ResultSet resultSet) throws SQLException {
        Map<Integer, List<Pizza>> pizzasByOrder = new HashMap<>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            String type = resultSet.getString("type");
            if (type == null || !type.equals("pizza")) {
                continue;
            }

            int orderId = resultSet.getInt("o.id");
            if (!pizzasByOrder.containsKey(orderId)) {
                pizzasByOrder.put(orderId, new ArrayList<>());
            }

            Ingredient ingredient = new Ingredient();
            ingredient.setId(resultSet.getInt("i.id"));
            ingredient.setName(resultSet.getString("i.name"));

            int pizzaId = resultSet.getInt("p.id");
            Stream<Pizza> pizzas = pizzasByOrder.get(orderId).stream()
                    .filter(pizza -> pizza.getId() == pizzaId);
            if (pizzas.anyMatch(pizza -> pizza.getId() == pizzaId)) {
                pizzasByOrder.get(orderId).stream()
                        .filter(pizza -> pizza.getId() == pizzaId)
                        .forEach(pizza -> pizza.addIngredient(ingredient));
            } else {
                Pizza newPizza = new Pizza();
                newPizza.setId(resultSet.getInt("p.id"));
                newPizza.setPrice(resultSet.getDouble("p.price"));
                newPizza.setName(resultSet.getString("p.name"));
                newPizza.setImageData(resultSet.getBytes("img_data"));
                if (ingredient.getId() != 0) {
                    newPizza.setIngredients(new ArrayList<>(List.of(ingredient)));
                }

                int pizzasNumber = resultSet.getInt("op.count");
                for (int i = 0; i < pizzasNumber; i++) {
                    pizzasByOrder.get(orderId).add(new Pizza(newPizza));
                }
            }
        }

        return pizzasByOrder;
    }

    private Order extractOrder(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("o.id"));
        order.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());
        order.setMessage(resultSet.getString("message"));

        return order;
    }

    private User extractUser(ResultSet resultSet) throws SQLException {
        if (resultSet.getString("role") == null) {
            return null;
        }

        User user = new User();
        user.setId(resultSet.getInt("u.id"));
        user.setEnabled(resultSet.getBoolean("enabled"));
        user.setRole(User.Role.valueOf(resultSet.getString("role")));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setName(resultSet.getString("u.name"));
        user.setPhoneNumber(resultSet.getString("phone"));
        user.setCountry(resultSet.getString("country"));
        user.setCity(resultSet.getString("city"));
        user.setStreet(resultSet.getString("street"));
        user.setHouseNumber(resultSet.getInt("house"));

        return user;
    }

    @Override
    public Optional<Order> findById(int id) {
        String query = "SELECT o.id, o.creation_time, o.price, o.message, p.id, p.name, p.type," +
                "op.count, p.price, p.size, p.img_data, i.id, i.name, u.id, u.email, u.password," +
                "u.enabled, u.role, u.name, u.phone, u.country, u.city, u.street, u.house " +
                "FROM orders o " +
                "LEFT JOIN order_products op ON op.order_id = o.id " +
                "LEFT JOIN products p ON op.product_id = p.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "LEFT JOIN product_ingredients pi ON p.id = pi.product_id " +
                "LEFT JOIN ingredients i ON pi.ingredient_id = i.id " +
                "WHERE o.id = ?";

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query, Types.INTEGER);
        factory.setResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
        factory.setUpdatableResults(false);
        PreparedStatementCreator statementCreator =
                factory.newPreparedStatementCreator(List.of(id));

       try {
           Order order = jdbc.query(statementCreator, extractor).get(0);
           return Optional.of(order);
       } catch (IndexOutOfBoundsException e) {
           return Optional.empty();
       }
    }

    @Override
    public List<Order> findAll() {
        String query = "SELECT o.id, o.creation_time, o.price, o.message, p.id, p.name, p.type," +
                "op.count, p.price, p.size, p.img_data, i.id, i.name, u.id, u.email, u.password," +
                "u.enabled, u.role, u.name, u.phone, u.country, u.city, u.street, u.house " +
                "FROM orders o " +
                "LEFT JOIN order_products op ON op.order_id = o.id " +
                "LEFT JOIN products p ON op.product_id = p.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "LEFT JOIN product_ingredients pi ON p.id = pi.product_id " +
                "LEFT JOIN ingredients i ON pi.ingredient_id = i.id";

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query);
        factory.setResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
        factory.setUpdatableResults(false);
        PreparedStatementCreator statementCreator
                = factory.newPreparedStatementCreator(List.of());

        return jdbc.query(statementCreator, extractor);
    }

    @Override
    public void save(Order order) {
        Optional<Order> optionalOrder = findById(order.getId());
        if (optionalOrder.isPresent()) {
            updateOrder(order);
        } else {
            createNewOrder(order);
        }
    }

    private void updateOrder(Order order) {
        String query = "UPDATE orders SET user_id = ?, creation_time = ?, price = ?, message = ? " +
                "WHERE id = ?";
        Object[] args = {order.getUser().getId(), Timestamp.valueOf(order.getCreationTime()),
                order.getPrice(), order.getMessage(), order.getId()};
        jdbc.update(query, args);

        deleteOrderProducts(order.getId());
        Map<AbstractProduct, Long> productsByNumber =
                Stream.concat(order.getPizzas().stream(), order.getDrinks().stream())
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        productsByNumber.forEach((product, number) ->
                saveOrderProduct(order.getId(), product.getId(), number.intValue()));
    }

    private void deleteOrderProducts(int orderId) {
        String query = "DELETE FROM order_products WHERE product_id = ?";
        jdbc.update(query, orderId);
    }

    private void saveOrderProduct(int orderId, int productId, int count) {
        String query = "INSERT INTO order_products(order_id, product_id, count) VALUES(?, ?, ?)";
        jdbc.update(query, orderId, productId, count);
    }

    private void createNewOrder(Order order) {
        String query = "INSERT INTO orders(user_id, creation_time, price, message) VALUES(?, ?, ?, ?)";
        PreparedStatementCreatorFactory statementFactory = new PreparedStatementCreatorFactory(
                query, Types.INTEGER, Types.TIMESTAMP, Types.DOUBLE, Types.VARCHAR
        );
        statementFactory.setReturnGeneratedKeys(true);
        PreparedStatementCreator statementCreator = statementFactory.newPreparedStatementCreator(
                new Object[] { order.getUser().getId(), Timestamp.valueOf(order.getCreationTime()),
                               order.getPrice(), order.getMessage() }
        );

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(statementCreator, keyHolder);
        int id = keyHolder.getKey().intValue();
        order.setId(id);

        Map<AbstractProduct, Long> productsByNumber =
                Stream.concat(order.getPizzas().stream(), order.getDrinks().stream())
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        productsByNumber.forEach((product, number) ->
            saveOrderProduct(id, product.getId(), number.intValue()));
    }
}
