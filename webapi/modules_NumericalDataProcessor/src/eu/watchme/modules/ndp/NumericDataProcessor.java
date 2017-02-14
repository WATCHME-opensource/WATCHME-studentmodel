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

package eu.watchme.modules.ndp;

import eu.watchme.modules.commons.staticdata.StaticModelException;
import eu.watchme.modules.commons.staticdata.model.form.json.MappedField;
import eu.watchme.modules.commons.staticdata.model.form.json.RankedMappedField;
import eu.watchme.modules.domainmodel.exceptions.NoMappingAvailableException;
import eu.watchme.modules.ndp.model.NumericOutputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalDouble;

import static eu.watchme.modules.commons.staticdata.StaticModelManager.getManager;

public final class NumericDataProcessor {

	private Logger logger = LoggerFactory.getLogger(NumericDataProcessor.class);
	private static HashMap<String, NumericDataProcessor> sNumericalDataProcessorsMap = new HashMap<>();

	private String domainId;

	private NumericDataProcessor(String domainId) {
		this.domainId = domainId;
	}

	public static NumericDataProcessor getProcessor(String domainId) throws FileNotFoundException {
		if (!sNumericalDataProcessorsMap.containsKey(domainId)) {
			sNumericalDataProcessorsMap.put(domainId, new NumericDataProcessor(domainId));
		}
		return sNumericalDataProcessorsMap.get(domainId);
	}

	public final NumericOutputData process(String answer) {
		RankedMappedField level = getRankPerformanceIndicatorLevelId(answer);
		return new NumericOutputData(level.getUnbId(), level.getRank());
	}

	private Integer computeAverageLevel(List<Integer> values) {
		OptionalDouble result = values.stream().mapToInt(Integer::intValue).average();
		if (result.isPresent()) {
			return (int) Math.round(result.getAsDouble());
		} else {
			return null;
		}
	}

	private void reportInvalidAverage(String epassPI, List<Integer> values) {
		logger.info("Thread-{} : Average could not be computed properly for PI/EPA = {}, values = {}", Thread.currentThread().getId(), epassPI, values);
	}

	protected String getUnbPerformanceIndicatorLevelId(Integer rank) {
		try {
			MappedField unbPerformanceIndicatorLevelId =
					getManager(domainId).getFormMapper().getMappedPerformanceIndicatorLevelByRank(rank);

			if (unbPerformanceIndicatorLevelId != null && unbPerformanceIndicatorLevelId.getUnbId() != null) {
				return unbPerformanceIndicatorLevelId.getUnbId();
			} else {
				throw new NoMappingAvailableException("performance indicator level rank ", rank.toString());
			}
		} catch (StaticModelException e) {
			throw new NoMappingAvailableException("performance indicator level id", rank.toString(), e);
		}
	}

	protected RankedMappedField getRankPerformanceIndicatorLevelId(String epassPiLevelId) {
		try {
			RankedMappedField unbPerformanceIndicatorLevelId =
					getManager(domainId).getFormMapper().getMappedPerformanceIndicatorLevelByEpass(epassPiLevelId);

			if (unbPerformanceIndicatorLevelId != null && unbPerformanceIndicatorLevelId.getUnbId() != null) {
				return unbPerformanceIndicatorLevelId;
			} else {
				throw new NoMappingAvailableException("performance indicator level id", epassPiLevelId);
			}
		} catch (StaticModelException e) {
			throw new NoMappingAvailableException("performance indicator level id", epassPiLevelId, e);
		}
	}

	public NumericOutputData getMeanRank(String epaUnbId, List<Integer> piRanks) {
		Integer epaAvgLevel = computeAverageLevel(piRanks);

		if (epaAvgLevel != null) {
			String unbEpaLevelId = getUnbPerformanceIndicatorLevelId(epaAvgLevel);
			return new NumericOutputData(unbEpaLevelId, epaAvgLevel);
		}
		reportInvalidAverage(epaUnbId, piRanks);
		return null;
	}
}
