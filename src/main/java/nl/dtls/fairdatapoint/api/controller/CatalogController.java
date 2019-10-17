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
package nl.dtls.fairdatapoint.api.controller;

import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;
import nl.dtls.fairdatapoint.service.metadata.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@RestController
@RequestMapping("/fdp/catalog")
public class CatalogController extends MetadataController {
    @Value("${instance.url}")
    private String instanceUrl;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"text/turtle", "application/ld+json",
            "application/rdf+xml", "text/n3"})
    @ResponseStatus(HttpStatus.OK)
    public CatalogMetadata getCatalogMetaData(@PathVariable final String id,
                                              HttpServletRequest request, HttpServletResponse response) throws
            MetadataServiceException, ResourceNotFoundException {

        LOGGER.info("Request to get CATALOG metadata, request url : {}", request.getRequestURL());
        return catalogMetadataService.retrieve(getRequestURLasIRI(request));
    }

    @ApiIgnore
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlCatalogMetadata(HttpServletRequest request)
            throws MetadataServiceException, ResourceNotFoundException, MetadataException {

        ModelAndView mav = new ModelAndView("pages/catalog");
        IRI uri = getRequestURLasIRI(request);
        mav.addObject("contextPath", request.getContextPath());

        // Retrieve Catalog metadata
        CatalogMetadata metadata = catalogMetadataService.retrieve(uri);
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata, RDFFormat.JSONLD,
                MetadataUtils.SCHEMA_DOT_ORG_MODEL));

        // Retrieve parent for breadcrumbs
        FDPMetadata repository = fdpMetadataService.retrieve(metadata.getParentURI());
        mav.addObject("repository", repository);

        // Retrieve Datasets details
        mav.addObject("datasets", datasetMetadataService.retrieve(metadata.getDatasets()));

        return mav;
    }


    @RequestMapping(method = RequestMethod.POST, consumes = {"text/turtle"}, produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogMetadata storeCatalogMetaData(final HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody(required = true) CatalogMetadata metadata)
            throws MetadataServiceException {
        IRI uri = generateNewIRI(request);
        LOGGER.info("Request to store catalog metadata with IRI {}", uri.toString());

        metadata.setUri(uri);
        metadata.setParentURI(VALUEFACTORY.createIRI(instanceUrl + "/fdp"));

        // Ignore children links
        metadata.setDatasets(Collections.emptyList());

        catalogMetadataService.store(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return catalogMetadataService.retrieve(uri);
    }
}