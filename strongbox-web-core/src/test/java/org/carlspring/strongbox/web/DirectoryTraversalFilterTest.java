package org.carlspring.strongbox.web;

import javax.servlet.http.HttpServletResponse;
import java.util.stream.Stream;

import io.restassured.http.Method;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Przemyslaw Fusik
 * @author Pablo Tirado
 */
public class DirectoryTraversalFilterTest
{

    private final Logger logger = LoggerFactory.getLogger(DirectoryTraversalFilterTest.class);

    private static final String MAVEN_CENTRAL_REPOSITORY = "http://localhost:48080/storages/storage-common-proxies/maven-central";

    private DirectoryTraversalFilter filter = new DirectoryTraversalFilter();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private MockFilterChain chain = new MockFilterChain();

    private static Stream<Arguments> requestUrisProvider()
    {
        return Stream.of(
                Arguments.of("shouldDisallowTraversalPaths",
                             "/../../storage-common-proxies/maven-central",
                             HttpServletResponse.SC_BAD_REQUEST),

                Arguments.of("shouldDisallowTraversalPathsWithEncodedDots",
                             "/%2e%2e/storage-common-proxies/maven-central",
                             HttpServletResponse.SC_BAD_REQUEST),

                Arguments.of("shouldDisallowTraversalPathsWithEncodedDotsAndSlash",
                             "/%2e%2e%2fstorage-common-proxies/maven-central",
                             HttpServletResponse.SC_BAD_REQUEST),

                Arguments.of("shouldDisallowTraversalPathsWithEncodedSlash",
                             "/..%2fstorage-common-proxies/maven-central",
                             HttpServletResponse.SC_BAD_REQUEST),

                Arguments.of("shouldAllowNormalizedPath",
                             "/",
                             HttpServletResponse.SC_OK)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestUrisProvider")
    void shouldAllowOrDisallowPaths(String methodName,
                                    String requestUri,
                                    int expectedStatusResponse)
            throws Exception
    {
        logger.debug(methodName);

        MockHttpServletRequest request = new MockHttpServletRequest(Method.GET.name(),
                                                                    MAVEN_CENTRAL_REPOSITORY + requestUri);

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus(), equalTo(expectedStatusResponse));
    }
}
