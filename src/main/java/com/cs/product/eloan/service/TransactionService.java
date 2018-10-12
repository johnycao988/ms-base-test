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
import com.cs.product.eloan.domain.service.AccrualProcess;
import com.cs.product.eloan.domain.service.AmortizationProcess;

public class TransactionService extends EloanService {

	public TransactionService(MessageRequest req, Properties prop) {
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
		if(SC.ST_ACC_OEMT.equals(getServiceType(service))) {
			return new AccrualProcess().processOneEventMutliTrx(msgRequest);
		}else if (SC.ST_ACC_OTME.equals(getServiceType(service))) {
			 return new AccrualProcess().processOneTrxMutliEvent(msgRequest);
		}else if(SC.ST_AMZ_OEMT.equals(getServiceType(service))) {
			return new AmortizationProcess().processOneEventMutliTrx(msgRequest);
		}else {
			return new AmortizationProcess().processOneTrxMutliEvent(msgRequest);
		}
	}

	@Override
	protected String getServiceId() {
		return SC.SID_TRANSACTION;
	}

}
