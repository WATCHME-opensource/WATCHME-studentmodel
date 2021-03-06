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

package eu.watchme.modules.commons.staticdata.model.form.mapper;

import eu.watchme.modules.commons.data.EpassKey;
import eu.watchme.modules.commons.data.UnbKey;
import eu.watchme.modules.commons.staticdata.model.form.json.AbstractPIMappedField;

import java.util.*;

public class PIFieldMapper<T extends AbstractPIMappedField> {
    private Map<EpassKey, List<T>> epassMapping = new HashMap<>();
    private Map<UnbKey, List<T>> unbMapping = new HashMap<>();

    public PIFieldMapper() {
    }

    public void buildMapper(List<T> jsonModel) {
        for (T item : jsonModel) {
            if (item.getEpassKey() != null) {
                for (EpassKey key : item.getEpassKey()) {
                    List<T> list = epassMapping.get(key);
                    if (list == null) {
                        list = new LinkedList<>();
                        epassMapping.put(key, list);
                    }
                    list.add(item);
                }
            }

            List<T> list = unbMapping.get(item.getUnbKey());
            if (list == null) {
                list = new LinkedList<>();
                unbMapping.put(item.getUnbKey(), list);
            }
            list.add(item);
        }
    }

    public List<T> getMappedFieldFromUnb(UnbKey unbKey) {
        List<T> result = unbMapping.get(unbKey);
        if (result == null) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }

    public List<T> getMappedFieldFromEpass(EpassKey epassKey) {
        List<T> result = epassMapping.get(epassKey);
        if (result == null) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }

    public Collection<T> getValues() {
        List<T> result = new LinkedList<>();
        epassMapping.values().stream().filter(values -> values != null).forEach(result::addAll);
        return result;
    }

    public Set<UnbKey> getKeysUnb() {
        return unbMapping.keySet();
    }
}
