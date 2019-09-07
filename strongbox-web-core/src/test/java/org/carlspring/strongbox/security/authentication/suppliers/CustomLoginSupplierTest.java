package org.carlspring.strongbox.security.authentication.suppliers;

import org.carlspring.strongbox.config.IntegrationTest;
import org.carlspring.strongbox.controllers.login.LoginInput;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Przemyslaw Fusik
 */
@IntegrationTest
public class CustomLoginSupplierTest
{

    private static final String REQUEST_URI = "/api/login";

    @Inject
    private JsonFormLoginSupplier customLoginSupplier;

    @Inject
    private ObjectMapper objectMapper;

    @Test
    public void shouldSupportExpectedRequest()
    {
        MockHttpServletRequest request = new MockHttpServletRequest(Method.POST.name(), REQUEST_URI);
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);

        assertTrue(customLoginSupplier.supports(request));
    }

    @Test
    public void shouldNotSupportGetRequest()
    {
        MockHttpServletRequest request = new MockHttpServletRequest(Method.GET.name(), REQUEST_URI);
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);

        assertFalse(customLoginSupplier.supports(request));
    }

    @Test
    public void shouldNotSupportXmlRequest()
    {
        MockHttpServletRequest request = new MockHttpServletRequest(Method.POST.name(), REQUEST_URI);
        request.setContentType(MediaType.APPLICATION_XML_VALUE);

        assertFalse(customLoginSupplier.supports(request));
    }

    @Test
    public void shouldSupply()
            throws Exception
    {
        LoginInput loginInput = new LoginInput();
        loginInput.setUsername("przemyslaw");
        loginInput.setPassword("fusik");

        MockHttpServletRequest request = new MockHttpServletRequest(Method.POST.name(), REQUEST_URI);
        request.setContent(objectMapper.writeValueAsBytes(loginInput));

        Authentication authentication = customLoginSupplier.supply(request);

        assertThat(authentication, CoreMatchers.notNullValue());
        assertThat(authentication, CoreMatchers.instanceOf(UsernamePasswordAuthenticationToken.class));
        assertThat(authentication.getPrincipal(), CoreMatchers.equalTo("przemyslaw"));
        assertThat(authentication.getCredentials(), CoreMatchers.equalTo("fusik"));
    }

}
