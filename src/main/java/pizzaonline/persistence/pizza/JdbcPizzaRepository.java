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

package pizzaonline.persistence.pizza;

import pizzaonline.service.ingredient.Ingredient;
import pizzaonline.service.pizza.Pizza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database-based Pizza object repository.
 */
@Repository
public class JdbcPizzaRepository implements PizzaRepository {
    private final JdbcTemplate jdbc;
    private final ResultSetExtractor<List<Pizza>> extractor;

    @Autowired
    public JdbcPizzaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        extractor = (resultSet -> {
            List<Pizza> pizzas = new ArrayList<>();
            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("i.id"));
                ingredient.setName(resultSet.getString("i.name"));

                int id = resultSet.getInt("p.id");
                Optional<Pizza> pizza = pizzas.stream()
                        .filter(item -> item.getId() == id)
                        .findFirst();
                if (pizza.isPresent() && ingredient.getId() != 0) {
                    pizza.get().addIngredient(ingredient);
                } else {
                    Pizza newPizza = new Pizza();
                    newPizza.setId(id);
                    newPizza.setPrice(resultSet.getDouble("price"));
                    newPizza.setName(resultSet.getString("p.name"));
                    newPizza.setImageData(resultSet.getBytes("img_data"));
                    if (ingredient.getId() != 0) {
                        newPizza.setIngredients(new ArrayList<>(List.of(ingredient)));
                    }

                    pizzas.add(newPizza);
                }
            }

            return pizzas;
        });
    }

    @Override
    public Optional<Pizza> findById(int id) {
        String query = "SELECT p.id, p.name, p.price, p.img_data, i.id, i.name " +
                "FROM products p " +
                "LEFT JOIN product_ingredients pi ON p.id = pi.product_id " +
                "LEFT JOIN ingredients i ON pi.ingredient_id = i.id " +
                "WHERE p.id = ?";

        try {
            Pizza pizza = jdbc.query(query, extractor, id).get(0);
            return Optional.of(pizza);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Pizza> findAll() {
        String query = "SELECT p.id, p.name, p.price, p.img_data, i.id, i.name " +
                "FROM products p " +
                "LEFT JOIN product_ingredients pi ON p.id = pi.product_id " +
                "LEFT JOIN ingredients i ON pi.ingredient_id = i.id " +
                "WHERE p.type = 'pizza'";
        return jdbc.query(query, extractor);
    }

    @Override
    public List<Pizza> findAllByName(String name) {
        String query = "SELECT p.id, p.name, p.price, p.img_data, i.id, i.name " +
                "FROM products p " +
                "LEFT JOIN product_ingredients pi ON p.id = pi.product_id " +
                "LEFT JOIN ingredients i ON pi.ingredient_id = i.id " +
                "WHERE p.type = 'pizza' and p.name = ?";
        return jdbc.query(query, extractor, name);
    }

    @Override
    public void save(Pizza pizza) {
        Optional<Pizza> optionalPizza = findById(pizza.getId());
        if (optionalPizza.isPresent()) {
            updatePizza(pizza);
        } else {
            createNewPizza(pizza);
        }
    }

    private void updatePizza(Pizza data) {
        String query = "UPDATE products " +
                "SET name = ?, type = 'pizza', price = ?, img_data = ? " +
                "WHERE id = ?";
        Object[] args = { data.getName(), data.getPrice(), data.getImageData(), data.getId() };
        jdbc.update(query, args);

        deleteIngredients(data.getId());
        data.getIngredients().forEach(item -> saveIngredient(data.getId(), item.getId()));
    }

    private void deleteIngredients(int productId) {
        String query = "DELETE FROM product_ingredients WHERE product_id = ?";
        jdbc.update(query, productId);
    }

    private void saveIngredient(int productId, int ingredientId) {
        String query = "INSERT INTO product_ingredients(product_id, ingredient_id) VALUES (?, ?)";
        jdbc.update(query, productId, ingredientId);
    }

    private void createNewPizza(Pizza pizza) {
        String query = "INSERT INTO products(name, type, price, img_data) " +
                "VALUES (?, 'pizza', ?, ?)";
        PreparedStatementCreatorFactory statementFactory = new PreparedStatementCreatorFactory(
                query, Types.VARCHAR, Types.DOUBLE, Types.BLOB
        );
        statementFactory.setReturnGeneratedKeys(true);
        PreparedStatementCreator statementCreator = statementFactory.newPreparedStatementCreator(
                List.of(pizza.getName(), pizza.getPrice(), pizza.getImageData())
        );

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(statementCreator, keyHolder);
        int id = keyHolder.getKey().intValue();
        pizza.setId(id);

        pizza.getIngredients().forEach(item -> saveIngredient(id, item.getId()));
    }

    @Override
    public void deleteById(int id) {
        String query = "DELETE FROM products WHERE id = ?";
        jdbc.update(query, id);
    }
}
