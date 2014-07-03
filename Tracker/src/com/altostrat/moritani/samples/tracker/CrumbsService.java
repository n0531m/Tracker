package com.altostrat.moritani.samples.tracker;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;

public class CrumbsService {

	public static String getCrumbsByReporterAsGeoJson(String reporterid) {
		QueryResultList<Entity> results = getCrumbsByReporter(reporterid);
		return toGeoJson(results);
	}

	private static QueryResultList<Entity> getCrumbsByReporter(String reporterid) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Filter reporterFilter = new FilterPredicate("param.reporter",
				FilterOperator.EQUAL, reporterid);

		Query q = new Query("Record").setFilter(reporterFilter).addSort(
				"reportDate", SortDirection.DESCENDING);
		// q.addProjection(new PropertyProjection("longitude", null));
		// q.addProjection(new PropertyProjection("latitude", null));
		PreparedQuery pq = datastore.prepare(q);

		int pageSize = 250;
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);

		QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
		return results;
	}

	private static String toGeoJson(QueryResultList<Entity> results) {
		StringBuffer buff = new StringBuffer();
		buff.append("{\"type\": \"FeatureCollection\",\"features\": [");
		boolean first = true;
		for (int i = 0; i < results.size(); i++) {
			Entity entity = results.get(i);
			if (entity.getProperty("param.latitude") == null
					|| "false".equals(entity.getProperty("param.latitude")))
				continue;
			if (first) {
				first = false;
			} else {
				buff.append(",");
			}
			buff.append("{");
			buff.append("\"type\": \"Feature\"");
			buff.append(",\"geometry\": {\"type\":\"Point\",\"coordinates\":["
					+ entity.getProperty("param.longitude") + ","
					+ entity.getProperty("param.latitude") + "]}");
			buff.append(",\"properties\":{");
			if (entity.getProperty("reportDate") instanceof Date) {
				Date date = (Date) entity.getProperty("reportDate");
				DateFormat formatter = DateFormat.getDateTimeInstance();
				formatter.setTimeZone(TimeZone.getTimeZone("JST"));
				buff.append("\"reportDate\":\"" + formatter.format(date) + "\"");
				buff.append(",\"reportDateNumber\":" + date.getTime());
			} else {
				buff.append("\"reportDate\":\""
						+ entity.getProperty("reportDate") + "\"");

			}
			buff.append(",\"accuracy\":" + entity.getProperty("param.accuracy"));
			buff.append(",\"providername\":\""
					+ entity.getProperty("param.providername") + "\"");
			buff.append(",\"User-Agent\":\""
					+ entity.getProperty("header.User-Agent") + "\"");

			buff.append("}");
			buff.append("}");
		}
		buff.append("]}");

		return buff.toString();
	}

	private static String toCsv(QueryResultList<Entity> results) {
		StringBuffer buff = new StringBuffer();
		DateFormat formatter = DateFormat.getDateTimeInstance();
		formatter.setTimeZone(TimeZone.getTimeZone("JST"));
		buff.append("latitude,longitude,reportDate,reportDate2,accuracy,providername,User-Agent\n");
		for (Entity entity : results) {
			buff.append(entity.getProperty("param.latitude"));
			buff.append("," + entity.getProperty("param.longitude"));
			Date date = (Date) entity.getProperty("reportDate");
			if (entity.getProperty("reportDate") instanceof Date) {
				buff.append("," + formatter.format(date));
				buff.append("," + date.getTime());
			} else {
				buff.append("," + formatter.format(date));
				buff.append("," + date.getTime());
			}
			buff.append("," + entity.getProperty("param.accuracy"));
			buff.append("," + entity.getProperty("param.providername"));
			buff.append("," + entity.getProperty("header.User-Agent"));
			buff.append("\n");
		}
		return buff.toString();
	}

	public static void main(String[] args) {
		System.out.println(CrumbsService.toCsv(CrumbsService
				.getCrumbsByReporter("aaa")));
	}

}
