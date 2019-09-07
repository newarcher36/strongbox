package org.carlspring.strongbox.rest.common;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author sbespalov
 *
 */
public class RestAssuredTestExecutionListener extends AbstractTestExecutionListener
{

    @Override
    public void beforeTestClass(TestContext testContext)
    {
        WebApplicationContext applicationContext = (WebApplicationContext) testContext.getApplicationContext();

        RestAssuredMockMvc.reset();
        RestAssuredMockMvc.webAppContextSetup(applicationContext);

    }

    @Override
    public void afterTestClass(TestContext testContext)
    {
        // Disabled because it affects to tests concurrent execution.
        //RestAssuredMockMvc.reset();
    }

}
