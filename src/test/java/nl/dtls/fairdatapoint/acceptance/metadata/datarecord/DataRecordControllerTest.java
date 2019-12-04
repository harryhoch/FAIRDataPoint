/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.dtls.fairdatapoint.acceptance.metadata.datarecord;

import nl.dtls.fairdatapoint.acceptance.metadata.common.MetadataControllerTest;
import nl.dtls.shared.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.utils.MetadataFixtureFilesHelper;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;

import javax.servlet.http.HttpServletResponse;

import static nl.dtls.fairdatapoint.utils.MetadataFixtureLoader.TEST_DATARECORD_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataRecordControllerTest extends MetadataControllerTest {

    /**
     * Store datarecord.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void storeDataRecord() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        String metadata = MetadataFixtureFilesHelper.getFileContentAsString(
                MetadataFixtureFilesHelper.DATARECORD_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/datarecord");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    /**
     * Store datarecord twice.
     *
     * @throws Exception
     */
    @Disabled
    @DirtiesContext
    @Test
    public void storeDatarecordTwice() {
        assertThrows(IllegalStateException.class, () -> {
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockHttpServletRequest request = new MockHttpServletRequest();

            String metadata = MetadataFixtureFilesHelper.getFileContentAsString(
                    MetadataFixtureFilesHelper.DATARECORD_METADATA_FILE);
            request.setMethod("POST");
            request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
            request.setContent(metadata.getBytes());
            request.setRequestURI("/datarecord");

            Object handler = handlerMapping.getHandler(request).getHandler();
            handlerAdapter.handle(request, response, handler);
            assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());

            response = new MockHttpServletResponse();
            request = new MockHttpServletRequest();

            request.setServerName("localhost");
            request.setContextPath("fdp");
            request.setMethod("POST");
            request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
            request.setContent(metadata.getBytes());
            request.setRequestURI("/datarecord");

            handler = handlerMapping.getHandler(request).getHandler();
            handlerAdapter.handle(request, response, handler);
        });
    }

    /**
     * Check non existing Content.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void nonExistingContentDatarecord() {
        assertThrows(ResourceNotFoundException.class, () -> {
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockHttpServletRequest request = new MockHttpServletRequest();

            request.setMethod("GET");
            request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
            request.setRequestURI("/datarecord/dummy");

            Object handler = handlerMapping.getHandler(request).getHandler();
            handlerAdapter.handle(request, response, handler);
        });
    }

    /**
     * Check existing Content.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void existingContentDatarecord() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_DATARECORD_PATH);

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

}