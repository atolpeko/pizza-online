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

package pizzaonline.web.admin;

import category.IntegrationTest;
import pizzaonline.service.ingredient.Ingredient;

import org.apache.commons.lang3.RandomStringUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Category(IntegrationTest.class)
@RunWith(SpringRunner.class)
@SpringBootTest
@WithMockUser(username = "atolpeko@gmail.com", authorities = "ADMIN")
public class IngredientAdminControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void shouldReturnManagePageOnManageIngredientsGetRequest() throws Exception {
        mvc.perform(get("/ingredients/manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient/manage"))
                .andExpect(model().attributeExists("ingredients"));
    }

    @Test
    public void shouldReturnNewIngredientPageOnNewIngredientGetRequest() throws Exception {
        mvc.perform(get("/ingredients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient/new"))
                .andExpect(model().attributeExists("ingredient"));
    }

    @Test
    public void shouldRedirectToManageOnNewIngredientPostRequestWhenIngredientIsValid() throws Exception {
        Ingredient ingredient = new Ingredient(0, RandomStringUtils.randomAlphabetic(20));
        mvc.perform(post("/ingredients/new").with(csrf()).flashAttr("ingredient", ingredient))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients/manage"));
    }

    @Test
    public void shouldReturnNewIngredientPageOnNewIngredientPostRequestWhenIngredientIsNotValid() throws Exception {
        mvc.perform(post("/ingredients/new").with(csrf()).flashAttr("ingredient", new Ingredient()))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient/new"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void shouldReturnEditIngredientPageOnEditIngredientGetRequest() throws Exception {
        mvc.perform(get("/ingredients/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient/edit"))
                .andExpect(model().attributeExists("ingredient"));
    }

    @Test
    public void shouldRedirectToManageOnIngredientPatchRequestWhenIngredientIsValid() throws Exception {
        Ingredient ingredient = new Ingredient(0, RandomStringUtils.randomAlphabetic(20));
        mvc.perform(patch("/ingredients/3").with(csrf()).flashAttr("ingredient", ingredient))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients/manage"));
    }

    @Test
    public void shouldReturnEditIngredientPageOnIngredientPatchRequestWhenIngredientIsNotValid() throws Exception {
        mvc.perform(patch("/ingredients/3").with(csrf()).flashAttr("ingredient", new Ingredient()))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient/edit"))
                .andExpect(model().hasErrors());
    }
}
