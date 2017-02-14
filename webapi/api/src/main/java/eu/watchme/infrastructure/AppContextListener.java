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

package eu.watchme.infrastructure;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.staticdata.StaticModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class AppContextListener implements ServletContextListener {
	final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.info("Thread-{} : Initializing context...", Thread.currentThread().getId());

		Properties properties = new Properties();
		try {
			properties.load(servletContextEvent.getServletContext().getResourceAsStream("/WEB-INF/appsettings.properties"));
			File resourcesDir = getResourcesDirectory(servletContextEvent);

			ApplicationSettings.LoadSettings(properties, resourcesDir);
			StaticModelManager.preloadManagers(ApplicationSettings.getEnabledDomains());

			logger.info("Thread-{} : Properties loaded successfully from /WEB-INF/appsettings.properties.", Thread.currentThread().getId());
		} catch (IOException e) {
			logger.error("Thread-{} : Error while initializing context", Thread.currentThread().getId(), e);
		}
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {

	}

	private File getResourcesDirectory(ServletContextEvent servletContextEvent) {
		String resDirRelativePath = servletContextEvent.getServletContext().getInitParameter("ResourcesRelativePath");
		String resDirAbsolutePath = servletContextEvent.getServletContext().getRealPath(resDirRelativePath);
		logger.info("Resources folder path: " + resDirAbsolutePath);
		return new File(resDirAbsolutePath);
	}
}
