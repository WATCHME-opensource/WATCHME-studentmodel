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

package eu.watchme.modules.dataaccess.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.time.LocalDateTime;

@Entity("authentications")
public class AuthenticationDataObject extends DataObject {

    @Id
    private ObjectId mId;
    @Property(Properties.AccessToken)
    private String mAccessToken;
    @Property(Properties.ExpirationDate)
    private String mExpirationDate;
    @Property(Properties.TokenType)
    private String mTokenType;
    @Property(Properties.Scope)
    private String mScope;
    @Property(Properties.ModelId)
    private String mModelId;

    public AuthenticationDataObject() {
    }

    public AuthenticationDataObject(String accessToken, LocalDateTime expirationDate, String tokenType, String scope, String modelId) {
        mAccessToken = accessToken;
        mExpirationDate = expirationDate.toString();
        mTokenType = tokenType;
        mScope = scope;
        mModelId = modelId;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public LocalDateTime getExpirationDate() {
        return LocalDateTime.parse(mExpirationDate);
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        mExpirationDate = expirationDate.toString();
    }

    public String getTokenType() {
        return mTokenType;
    }

    public void setTokenType(String tokenType) {
        mTokenType = tokenType;
    }

    public String getScope() {
        return mScope;
    }

    public void setScope(String scope) {
        mScope = scope;
    }

    public String getModelId() {
        return mModelId;
    }

    public void setModelId(String modelId) {
        mModelId = modelId;
    }

    @Override
    public Object getId() {
        return mId;
    }

    public static class Properties {
        public static final String AccessToken = "access_token";
        public static final String ExpirationDate = "expiration_date";
        public static final String TokenType = "token_type";
        public static final String Scope = "scope";
        public static final String ModelId = "model_id";
    }
}
