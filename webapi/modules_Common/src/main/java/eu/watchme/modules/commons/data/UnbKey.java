/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 1/8/2015
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

package eu.watchme.modules.commons.data;

import com.google.gson.annotations.SerializedName;

public class UnbKey {
    @SerializedName("EpaUnbId")
    private String epaId;
    @SerializedName("PiUnbId")
    private String unbId;

    public UnbKey() {
    }

    public UnbKey(String epaId, String unbId) {
        this.epaId = epaId;
        this.unbId = unbId;
    }

    public String getEpaId() {
        return epaId.toUpperCase();
    }

    public String getUnbId() {
        return unbId.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnbKey epassKey = (UnbKey) o;

        return (epaId != null ? epaId.equals(epassKey.epaId) : epassKey.epaId == null) &&
                (unbId != null ? unbId.equals(epassKey.unbId) : epassKey.unbId == null);
    }

    @Override
    public int hashCode() {
        int result = epaId != null ? epaId.hashCode() : 0;
        result = 31 * result + (unbId != null ? unbId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UnbKey{" + "epaId='" + epaId + '\'' + ", unbId='" + unbId + '\'' + '}';
    }
}
