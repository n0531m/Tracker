package com.altostrat.moritani.samples.tracker;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CrumbsServlet extends HttpServlet {

	private static final long serialVersionUID = -6598729888731002791L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String reporter = request.getParameter("reporter");
		assert reporter != null;

		String json = CrumbsService.getCrumbsByReporterAsGeoJson(reporter);

		if (request.getParameter("callback") == null) {
			response.setContentType("application/json");
			// System.out.println(JSON.encode(entity));
			response.getWriter().println(json);
		} else {
			response.setContentType("application/javascript");
			response.getWriter().println(
					request.getParameter("callback") + "(" + json + ")");
		}
	}
}
