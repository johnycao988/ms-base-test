package com.cs.test.filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.flywaydb.core.Flyway;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

@WebFilter("/*")
public class TestWebFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		// initDBByFlyway();
		initDBByLiquibase();

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		System.out.println("TEst");
	}

	@Override
	public void destroy() {

	}

	private void initDBByFlyway() {

		Flyway flyway = Flyway.configure().schemas("test")
				.dataSource("jdbc:mysql://192.168.3.102:30306", "root", "mysql").load();

		flyway.migrate();
		// flyway.info();
		// flyway.validate();
		// flyway.clean();

	}

	private void initDBByLiquibase() {

		Liquibase liquibase = null;
		Connection c = null;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			
			c = DriverManager.getConnection("jdbc:mysql://192.168.3.102:30306", "root", "mysql");

			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
			database.setDefaultSchemaName("test");

			liquibase = new Liquibase("db/changelog/master.xml", new ClassLoaderResourceAccessor(), database);
			liquibase.update("");
		 
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (c != null) {
				try {
					c.rollback();
					c.close();
				} catch (SQLException e) {
					// nothing to do
				}
			}
		}

	}

}
