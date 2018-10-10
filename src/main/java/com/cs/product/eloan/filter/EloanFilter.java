package com.cs.product.eloan.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.cs.log.logs.bean.ServiceLogKey;
import com.cs.product.eloan.base.consts.LC;
import com.cs.product.eloan.base.dao.DSMgr;

@WebFilter("/*")
public class EloanFilter implements Filter {

 
    public EloanFilter() {
    }

	public void destroy() {
	}


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
		ServiceLogKey logKey = new ServiceLogKey();
		logKey.setServiceName("eLoan");
		DSMgr.initConfigure(LC.getSYS(logKey));
	}

}
