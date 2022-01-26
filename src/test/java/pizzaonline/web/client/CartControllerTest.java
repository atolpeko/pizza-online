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

package pizzaonline.web.client;

import pizzaonline.service.drink.Drink;
import pizzaonline.service.order.Order;
import pizzaonline.service.ingredient.Ingredient;
import pizzaonline.service.pizza.Pizza;
import pizzaonline.service.user.User;

import category.IntegrationTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Category(IntegrationTest.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class CartControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    private Order invalidOrder;
    private Order validOrder;
    private User blockedUser;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Before
    public void createValidOrder() {
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setPassword("some-password");
        user.setName("user");
        user.setPhoneNumber("33445566");
        user.setCountry("some-country");
        user.setCity("some-city");
        user.setStreet("some-street");
        user.setHouseNumber(1);

        List<Ingredient> ingredients = List.of(
                new Ingredient(1, "1"),
                new Ingredient(2, "2"),
                new Ingredient(3, "3")
        );
        Pizza pizza = new Pizza(1, 100, "Pizza", new byte[0], ingredients);
        Drink drink = new Drink(5, 100, "Drink", new byte[0], 250);

        validOrder = new Order();
        validOrder.setUser(user);
        validOrder.addPizza(pizza);
        validOrder.addDrink(drink);
    }

    @Before
    public void createInvalidOrder() {
        User user = new User();
        Pizza pizza = new Pizza();
        Drink drink = new Drink();

        invalidOrder = new Order();
        invalidOrder.setUser(user);
        invalidOrder.addPizza(pizza);
        invalidOrder.addDrink(drink);
    }

    @Before
    public void createBlockedUser() {
        blockedUser = new User();
        blockedUser.setEnabled(false);
        blockedUser.setEmail("user_blocked@gmail.com");
        blockedUser.setPassword("some-password");
        blockedUser.setName("user");
        blockedUser.setPhoneNumber("33445566");
        blockedUser.setCountry("some-country");
        blockedUser.setCity("some-city");
        blockedUser.setStreet("some-street");
        blockedUser.setHouseNumber(1);
    }

    @Test
    public void shouldReturnCartPageOnCartGetRequest() throws Exception {
        mvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cart"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    public void shouldReturnSubmissionPageOnSubmitGetRequestWhenCartIsNotEmpty() throws Exception {
        mvc.perform(get("/cart/submit").flashAttr("order", validOrder))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/order-details"));
    }

    @Test
    public void shouldRedirectToCartOnSubmitGetRequestWhenCartIsEmpty() throws Exception {
        mvc.perform(get("/cart/submit").flashAttr("order", new Order()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    public void shouldReturnSubmittedPageOnSubmitPostRequestWhenOrderIsValid() throws Exception {
        mvc.perform(post("/cart/submit").with(csrf()).flashAttr("order", validOrder))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/submitted"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void shouldRedirectToCartOnSubmitPostRequestWhenCartIsEmpty() throws Exception {
        mvc.perform(post("/cart/submit").with(csrf()).flashAttr("order", new Order()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    public void shouldReturnOrderDetailsPageOnSubmitPostRequestWhenOrderIsInvalid() throws Exception {
        mvc.perform(post("/cart/submit").with(csrf()).flashAttr("order", invalidOrder))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/order-details"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void shouldReturnDeniedPageOnSubmitPostRequestWhenUserIsBlocked() throws Exception {
        Order order = validOrder;
        order.setUser(blockedUser);

        mvc.perform(post("/cart/submit").with(csrf()).flashAttr("order", order))
                .andExpect(status().isOk())
                .andExpect(view().name("error/denied"));
    }

    @Test
    public void shouldRedirectToPizzasOnAddPizzaPostRequest() throws Exception {
        Pizza validPizza = validOrder.getPizzas().get(0);
        mvc.perform(post("/cart/add-pizza").with(csrf()).sessionAttr("pizza", validPizza))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pizzas"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void shouldRedirectToDrinksOnAddDrinkPostRequest() throws Exception {
        Drink validDrink = validOrder.getDrinks().get(0);
        mvc.perform(post("/cart/add-drink").with(csrf()).sessionAttr("drink", validDrink))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/drinks"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void shouldReturnCartPageOnDeletePizzaDeleteRequest() throws Exception{
        mvc.perform(delete("/cart/pizza/1").with(csrf()).flashAttr("order", validOrder))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cart"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void shouldReturnCartPageOnDeleteDrinkDeleteRequest() throws Exception{
        mvc.perform(delete("/cart/drink/1").with(csrf()).flashAttr("order", validOrder))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cart"))
                .andExpect(model().hasNoErrors());
    }
 }
