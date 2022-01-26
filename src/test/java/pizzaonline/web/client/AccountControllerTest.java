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

import pizzaonline.service.user.User;
import category.IntegrationTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
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
public class AccountControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    private User registeredUser;
    private User notRegisteredUser;
    private User newUser;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Before
    public void createRegisteredUser() {
        registeredUser = createValidUser();
        registeredUser.setId(2);
        registeredUser.setEmail("altolpeko@gmail.com");
    }

    private User createValidUser() {
        User user = new User();
        user.setRole(User.Role.USER);
        user.setPassword("12345678");
        user.setName("Alexander");
        user.setPhoneNumber("375334455445");
        user.setCountry("Belarus");
        user.setCity("Minsk");
        user.setStreet("Nemiga");
        user.setHouseNumber(3);

        return user;
    }

    @Before
    public void createNotRegisteredUser() {
        notRegisteredUser = createValidUser();
        notRegisteredUser.setEmail("tolpeko@gmail.com");
    }

    @Before
    public void createNewUser() {
        newUser = createValidUser();
        newUser.setEmail(RandomStringUtils.randomAlphabetic(30) + "@gmail.com");
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldReturnAccountPageOnAccountGetRequestWhenUserIsLoggedIn() throws Exception {
        mvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/account"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithAnonymousUser
    public void shouldReturnLoginPageOnLoginGetRequestWhenUserIsNotLoggedIn() throws Exception {
        mvc.perform(get("/account/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldRedirectToAccountOnLoginGetRequestWhenUserIsLoggedIn() throws Exception {
        mvc.perform(get("/account/login").flashAttr("user", registeredUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));
    }

    @Test
    @WithAnonymousUser
    public void shouldReturnRegistrationPageOnRegisterGetRequest() throws Exception {
        mvc.perform(get("/account/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithAnonymousUser
    public void shouldRedirectToAccountOnRegisterPostRequestWhenRegistrationIsSuccessful() throws Exception {
        mvc.perform(post("/account/register").with(csrf()).flashAttr("user", newUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));
    }

    @Test
    @WithAnonymousUser
    public void shouldReturnRegistrationPageOnRegisterPostRequestWhenUserIsInvalid() throws Exception {
        mvc.perform(post("/account/register").with(csrf()).flashAttr("user", new User()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithAnonymousUser
    public void shouldReturnRegistrationPageOnRegisterPostRequestWhenUserAlreadyExists() throws Exception {
        mvc.perform(post("/account/register").with(csrf()).flashAttr("user", registeredUser))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"));
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldReturnEditPageOnEditGetRequest() throws Exception {
        mvc.perform(get("/account/edit").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/edit-account"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldRedirectToAccountOnEditPatchRequestWhenUserIsValidAndExists() throws Exception {
        mvc.perform(patch("/account/edit").with(csrf()).flashAttr("user", registeredUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldReturnEditPageOnEditPatchRequestWhenUserIsInvalid() throws Exception {
        mvc.perform(patch("/account/edit").with(csrf()).flashAttr("user", new User()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/edit-account"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldReturn404PageOnEditPatchRequestWhenUserDoesNotExist() throws Exception {
        mvc.perform(patch("/account/edit").with(csrf()).flashAttr("user", notRegisteredUser))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldReturnEditLoginPageOnLoginEditGetRequest() throws Exception {
        mvc.perform(get("/account/edit-login").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/edit-login"))
                .andExpect(model().attributeExists("user", "currPassword"));
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldReturnEditLoginPageOnEditLoginPatchRequestWhenPasswordIsInvalid() throws Exception {
        mvc.perform(patch("/account/edit-login")
                        .with(csrf())
                        .flashAttr("user", registeredUser)
                        .param("currPassword", registeredUser.getPassword() + "-"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/edit-login"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldReturnEditLoginPageOnEditLoginPatchRequestWhenUserIsInvalid() throws Exception {
        mvc.perform(patch("/account/edit-login").with(csrf()).flashAttr("user", new User()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/edit-login"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(username = "altolpeko@gmail.com", authorities = "USER")
    public void shouldReturn404PageOnEditLoginPatchRequestWhenUserDoesNotExist() throws Exception {
        mvc.perform(patch("/account/edit-login").with(csrf()).flashAttr("user", notRegisteredUser))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }
}
