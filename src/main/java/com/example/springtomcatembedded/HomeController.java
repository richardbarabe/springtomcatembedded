package com.example.springtomcatembedded;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.deploy.ApplicationListener;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
	    tomcat.setPort(8080);
	    
	    Context ctx = tomcat.addContext("/", new File("src/main/webapp/").getAbsolutePath());
	    ApplicationParameter rootContextConfiguration = new ApplicationParameter();
	    rootContextConfiguration.setName("contextConfigLocation");
	    rootContextConfiguration.setValue("/WEB-INF/spring/root-context.xml");
	    ctx.addApplicationParameter(rootContextConfiguration);
	    ApplicationListener springApplicationListener = new ApplicationListener("org.springframework.web.context.ContextLoaderListener", false);
	    ctx.addApplicationListener(springApplicationListener);
	    
	    // Configure dispatcher servlet
	    Wrapper wrapper = ctx.createWrapper();
	    String servletName = "appServlet";
	    wrapper.setName(servletName);
	    wrapper.setLoadOnStartup(1);
	    wrapper.setServletClass(DispatcherServlet.class.getName());
	    wrapper.addInitParameter("contextConfigLocation", "/WEB-INF/spring/appServlet/servlet-context.xml");
	    wrapper.setLoadOnStartup(1);
	    
	    // Define JspServlet.
	    Wrapper jspServlet = ctx.createWrapper();
	    jspServlet.setName("jsp");
	    jspServlet.setServletClass("org.apache.jasper.servlet.JspServlet");
	    jspServlet.addInitParameter("fork", "false");
	    jspServlet.addInitParameter("xpoweredBy", "false");
	    jspServlet.setLoadOnStartup(2);
	    ctx.addChild(jspServlet);
	    ctx.addServletMapping("*.jsp", "jsp");
	    
	    ctx.addChild(wrapper);
	    ctx.addServletMapping("/", servletName);
	    tomcat.setSilent(false);
	    tomcat.start();
	    tomcat.getServer().await();
	    
	}
}
