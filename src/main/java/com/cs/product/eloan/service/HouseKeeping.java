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
import com.cs.product.eloan.domain.service.component.housekeeping.AccOnlineAccrual;
import com.cs.product.eloan.domain.service.component.housekeeping.AmzOnlineAccrual;
import com.cs.product.eloan.domain.service.component.housekeeping.InterestAccrual;
import com.cs.product.eloan.domain.service.component.housekeeping.InterestPosting;

public class HouseKeeping extends EloanService{

	public HouseKeeping(MessageRequest req, Properties prop) {
		super(req, prop);
	}

	@Override
	protected MsgResponse<Map<String, Object>, Object> callServiceMap(MessageHeadService service,
			MsgRequest<Map<String, Object>, Map<String, Object>> msgRequest) {
		if(SC.AP_HK_ACCRUE.equals(getServiceType(service))) {
			return new InterestAccrual().processInterestAccrue(msgRequest);
		}else {
			return new InterestPosting().processInterestPosting(msgRequest);
		}
	}

	@Override
	protected MsgResponse<Map<String, Object>, Object> callServiceList(MessageHeadService service,
			MsgRequest<Map<String, Object>, List<Map<String, Object>>> msgRequest) {
		if(SC.AP_HK_ACCONLINE.equals(getServiceType(service))) {
			return new AccOnlineAccrual().process(msgRequest); 
		}else {
			return new AmzOnlineAccrual().process(msgRequest);
		}
	}

}
