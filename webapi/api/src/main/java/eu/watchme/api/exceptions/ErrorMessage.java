/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 31/7/2015
 * Copyright: Copyright (C) 2014-2017 WATCHME Consortium
 * License: The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

package eu.watchme.api.exceptions;

import eu.watchme.modules.domainmodel.exceptions.ValidationException;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.ws.rs.core.Response;
import java.util.Arrays;

public class ErrorMessage {

    @JsonProperty("status")
    int mStatus;

    @JsonProperty("message")
    String mMessage;

    @JsonProperty("developerMessage")
    String mDeveloperMessage;

    public ErrorMessage(ValidationException e) {
        this.mStatus = Response.Status.BAD_REQUEST.getStatusCode();
        this.mMessage = e.getMessage();
        this.mDeveloperMessage = e.getCause() == null ? null : Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n");
    }

    public ErrorMessage(Exception ex) {
        this.mStatus = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        this.mMessage = ex.getMessage();
        this.mDeveloperMessage = Arrays.toString(ex.getStackTrace()).replace(",", "\n");
    }

    public ErrorMessage(Throwable ex) {
        this.mStatus = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        this.mMessage = ex.getMessage();
        this.mDeveloperMessage = Arrays.toString(ex.getStackTrace()).replace(",", "\n");
    }

    public int getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getDeveloperMessage() {
        return mDeveloperMessage;
    }

}
