package com.cs.product.eloan.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.cs.config.client.ConfigLogException;
import com.cs.config.client.ConfigureClient;
import com.cs.log.common.logbean.LogException;
import com.cs.log.logs.LogInfoMgr;
import com.cs.log.logs.bean.ServiceLogKey;
import com.cs.product.eloan.base.consts.LC;

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
		try {
			LogInfoMgr.initByDoc("", ConfigureClient.getFileAsDom("conf/loginfo_en.xml"));
		} catch (LogException e) {
			LC.getSYS(logKey).getLogger().write(logKey, e);
		}
	}

}
