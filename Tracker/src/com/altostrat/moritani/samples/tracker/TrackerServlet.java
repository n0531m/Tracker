package com.altostrat.moritani.samples.tracker;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class TrackerServlet extends HttpServlet {
	private static DatastoreService datastore;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		datastore = DatastoreServiceFactory.getDatastoreService();

	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPut(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		Enumeration<String> params = request.getParameterNames();
		Entity entity = new Entity("Record");

		String reporter = "anonymous";
		while (params.hasMoreElements()) {
			String key = params.nextElement();
			String value = request.getParameter(key);
			if ("reporter".equalsIgnoreCase(key)) {
				reporter = value;
			}
			entity.setProperty("param." + key, value);
		}
		entity.setProperty("reporter", reporter);
		entity.setProperty("reportDate", new Date());

		Enumeration<String> headers = request.getHeaderNames();

		while (headers.hasMoreElements()) {
			String key = headers.nextElement();
			String value = request.getHeader(key);
			entity.setProperty("header." + key, value);
		}

		datastore.put(entity);
		response.setContentType("application/json");

		// System.out.println(JSON.encode(entity));
		response.getWriter().println(JSON.encode(entity));

	}
}
