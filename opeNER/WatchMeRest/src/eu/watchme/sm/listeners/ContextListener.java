/**
 *
 */
package eu.watchme.sm.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import eu.watchme.sm.helpers.GeneralConfigs;
import eu.watchme.sm.nlp.LanguageManager;

/**
 * @author isrlab
 *
 */
public class ContextListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext cntx = event.getServletContext();
		String parent = cntx.getRealPath("/WEB-INF");
		GeneralConfigs configs = new GeneralConfigs(parent);
		System.out.println(configs.getContextVar());
		LanguageManager lng_mgr = new LanguageManager(parent);
		cntx.setAttribute(configs.getContextVar(), lng_mgr);
		System.out.println("initialised");
	}

}
