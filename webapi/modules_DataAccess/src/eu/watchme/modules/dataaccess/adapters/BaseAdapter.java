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

package eu.watchme.modules.dataaccess.adapters;

import com.mongodb.MongoClient;
import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.dataaccess.InstantConverter;
import eu.watchme.modules.dataaccess.model.DataObject;
import org.bson.types.BSONTimestamp;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import java.util.List;

public abstract class BaseAdapter<T extends DataObject> {
	private static Datastore sDataStore;

	final Class<T> mTypeParameterClass;

	protected BaseAdapter(Class<T> typeParameterClass) {
		this.mTypeParameterClass = typeParameterClass;
	}

	protected static Datastore getDataStore() {
		if (sDataStore == null) {
			final Morphia morphia = new Morphia();

			morphia.mapPackage("eu.watchme.modules.dataaccess.model");
			morphia.getMapper().getOptions().setStoreNulls(false);
			morphia.getMapper().getOptions().setStoreEmpties(true);
			morphia.getMapper().getConverters().addConverter(InstantConverter.class);
			final Datastore datastore = morphia.createDatastore(new MongoClient(ApplicationSettings.getMongoUrl()), ApplicationSettings.getMongoDbName());
			datastore.ensureIndexes();
			sDataStore = datastore;
		}
		return sDataStore;
	}

	protected Query<T> createQuery() {
		return getDataStore().createQuery(mTypeParameterClass);
	}

	public void insertDataObject(T dataObject) {
		dataObject.setUpdatedTimestamp(new BSONTimestamp());
		Datastore datastore = getDataStore();
		datastore.save(dataObject);
	}

	public T getDataObject(String id) {
		Datastore datastore = getDataStore();
		return datastore.createQuery(mTypeParameterClass).field(DataObject.Properties.Id).equal(id).get();
	}

	public List<T> getDataObjectsList() {
		Datastore datastore = getDataStore();
		return datastore.createQuery(mTypeParameterClass).asList();
	}

	public void updateDataObject(T dataObject) {
		Datastore datastore = getDataStore();
		Query<T> query = datastore.createQuery(mTypeParameterClass).field(DataObject.Properties.Id).equal(dataObject.getId());
		dataObject.setUpdatedTimestamp(new BSONTimestamp());
		datastore.updateFirst(query, dataObject, true);
	}

	public void deleteDataObject(String id) {
		Datastore datastore = getDataStore();
		Query<T> query = datastore.createQuery(mTypeParameterClass).field(DataObject.Properties.Id).equal(id);
		datastore.delete(query);
	}

	public void deleteDataObject(T dataObject) {
		Datastore datastore = getDataStore();
		datastore.delete(dataObject);
	}
}
