package it.polimi.tiw.handlers;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.ServletContextTemplateResource;

/**
 * 
 * The MultiPathMessageResolver class extends StandardMessageResolver in order
 * to customize the place where we can search for the property files for
 * customization by overriding the method resolveMessagesForTemplate. You need
 * to set this MessageResolver for your template engine as follows:
 * this.templateEngine.setMessageResolver(new MultiPathMessageResolver(servletContext, "NAME OF YOUR FOLDER INSIDE WEB-INF"));
 * 
 * @author Samuele Pasini
 * @author Francesco Azzoni
 *
 */

public class MultiPathMessageResolver extends StandardMessageResolver {

	private ServletContext context;
	private String directory;

	public MultiPathMessageResolver(ServletContext context, String path) {
		super();
		this.context = context;
		this.directory = path;
	}

	@Override
	protected Map<String, String> resolveMessagesForTemplate(String template, ITemplateResource templateResource,
			Locale locale) {
		System.out.println(template);
		String regex = "(.*)(/[^/]*$)";
		String file = template.replaceFirst(regex, "$2");
		System.out.println(file);
		String finalpath = "/WEB-INF/" + directory + file;
		//String finalpath = "/WEB-INF/" + directory + "/messages.html";
		System.out.println(finalpath);
		templateResource = new ServletContextTemplateResource(context, finalpath, null);
		return super.resolveMessagesForTemplate(finalpath, templateResource, locale);
	}

}
