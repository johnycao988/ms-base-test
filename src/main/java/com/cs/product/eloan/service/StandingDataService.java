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
import com.cs.product.eloan.domain.service.StandingDataProcess;

public class StandingDataService extends EloanService {

	public StandingDataService(MessageRequest req, Properties prop) {
		super(req, prop);
	}

	@Override
	protected MsgResponse<Map<String, Object>, Object> callServiceMap(MessageHeadService service,
			MsgRequest<Map<String, Object>, Map<String, Object>> msgRequest) {
		return null;
	}

	@Override
	protected MsgResponse<Map<String, Object>, Object> callServiceList(MessageHeadService service,
			MsgRequest<Map<String, Object>, List<Map<String, Object>>> msgRequest) {
		if(SC.ST_STANDING_ADD.equals(getServiceType(service))) {
			return new StandingDataProcess().addStandingData(msgRequest);
		}else if(SC.ST_STANDING_UPDATE.equals(getServiceType(service))) {
			return new StandingDataProcess().updateStandingData(msgRequest);
		}else {
			return new StandingDataProcess().deleteStandingData(msgRequest);
		}
	}

	@Override
	protected String getServiceId() {
		return SC.SID_STANDINGDATA;
	}
}
