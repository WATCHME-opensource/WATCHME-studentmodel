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

package eu.watchme.modules.commons.enums;

import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Language codes in ISO 639-1 standard
 */
public enum LanguageCode {
    //for Default language settings, see ApplicationSettings.java
    @SerializedName("en")
    EN(0, "en",null, "english"),
    @SerializedName("nl")
    NL(1,"nl",null, "dutch"),
    @SerializedName("de")
    DE(2, "de",null, "german"),
    @SerializedName("hu")
    HU(3, "hu",null, "hungarian"),
    @SerializedName("et")
    EE(4, "et",new HashSet<>(Arrays.asList("et","ee")), "estonian");


    private int index;
    private String isoLabel;
    private Set<String> AltIsoLabel;
    private String name;
    LanguageCode(int index, String isoLabel,Set<String> AltIsoLabel, String name) {
        this.index = index;
        this.name = name;
        this.isoLabel = isoLabel;
        this.AltIsoLabel = AltIsoLabel;
    }

    public static int count() {
        return values().length;
    }

    @JsonCreator()
    public static LanguageCode fromValue(String value) {
        for (LanguageCode item : values()) {
            if (item.isoLabel.equalsIgnoreCase(value) || item.name.equalsIgnoreCase(value)) {
                return item;
            }
            if(item.AltIsoLabel!=null){
                if(item.AltIsoLabel.contains(value.toLowerCase())){
                    return item;
                }
            }


        }
        return EN;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return name;
    }

    @JsonValue()
    public String toValue() {
        return isoLabel;
    }
}
