package com.cs.product.eloan.service;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.cs.cloud.message.api.MessageHeadService;
import com.cs.cloud.message.api.MessageRequest;
import com.cs.product.eloan.EloanService;
import com.cs.product.eloan.base.vo.message.MsgRequest;
import com.cs.product.eloan.base.vo.message.MsgResponse;
import com.cs.product.eloan.consts.SC;
import com.cs.product.eloan.domain.service.GetDataProcess;

public  class GetDataService extends EloanService {

	public GetDataService(MessageRequest req, Properties prop) {
		super(req, prop);
	}

	@Override
	protected MsgResponse<Map<String, Object>, Object> callServiceMap(MessageHeadService service,
			MsgRequest<Map<String, Object>, Map<String, Object>> msgRequest) {
		return new GetDataProcess().processGetDataEvent(msgRequest);
	}

	@Override
	protected MsgResponse<Map<String, Object>, Object> callServiceList(MessageHeadService service,
			MsgRequest<Map<String, Object>, List<Map<String, Object>>> msgRequest) {
		return null;
	}

	@Override
	protected String getServiceId() {
		return SC.SID_GETDATA;
	}

}
