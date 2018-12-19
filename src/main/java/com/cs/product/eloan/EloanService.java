package com.cs.product.eloan;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.cs.baseapp.api.messagebroker.BusinessService;
import com.cs.baseapp.errorhandling.BaseAppException;
import com.cs.baseapp.logger.LogManager;
import com.cs.cloud.message.api.MessageHeadConsumer;
import com.cs.cloud.message.api.MessageHeadService;
import com.cs.cloud.message.api.MessageRequest;
import com.cs.cloud.message.api.MessageResponse;
import com.cs.cloud.message.api.builder.MessageResponseBodyServiceBuilder;
import com.cs.cloud.message.api.builder.MessageResponseBodyServiceStatusBuilder;
import com.cs.cloud.message.api.builder.MessageResponseBuilder;
import com.cs.cloud.message.domain.errorhandling.MessageException;
import com.cs.cloud.message.domain.factory.MessageFactory;
import com.cs.cloud.message.domain.utils.MessageConstant;
import com.cs.log.logs.LogInfoMgr;
import com.cs.log.logs.bean.Logger;
import com.cs.product.eloan.base.vo.message.MsgHeader;
import com.cs.product.eloan.base.vo.message.MsgRequest;
import com.cs.product.eloan.base.vo.message.MsgResponse;
import com.cs.product.eloan.consts.SC;
import com.cs.product.eloan.domain.base.consts.EC;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class EloanService extends BusinessService {
	private static Logger logger = LogManager.getSystemLog();

	public EloanService(MessageRequest req, Properties prop) {
		super(req, prop);
	}

	@Override
	public MessageResponse process() throws BaseAppException, MessageException {
		MessageResponseBuilder responseBuilder = MessageFactory.getResponseMessageBuilder();
		responseBuilder
				.setBase(responseBuilder.getMessageHeadBaseBuilder().setCorrelationId(this.getReq().getBase().getId())
						.setType(MessageConstant.MESSAGE_HEAD_BASE_TYPE_RESPONSE).build());
		List<MessageHeadService> services = this.getReq().getServices();
		for (MessageHeadService service : services) {
			MsgResponse<Map<String, Object>, Object> msgResponse = process(service);
			MessageResponseBodyServiceBuilder sBuilder = responseBuilder.getMessageResponseBodyServiceBuilder();
			MessageResponseBodyServiceStatusBuilder ssBuilder = responseBuilder
					.getMessageResponseBodyServiceStatusBuilder();
			if (msgResponse.getHeader().getExtendHeader().get(EC.INTF_HEAD_STATUS) == null
					|| EC.INTF_STAUTS_SUCCESSED == (int) msgResponse.getHeader().getExtendHeader()
							.get(EC.INTF_HEAD_STATUS)) {
				ssBuilder.setSuccess(true);
			} else {
				ssBuilder.setSuccess(false);
				ssBuilder.setErrCode(String.valueOf(msgResponse.getHeader().getExtendHeader().get(EC.INTF_HEAD_STATUS)));
			}
			sBuilder.setApplicationId(service.getApplicationId()).setSecret(this.getReq().getConsumer().getSecret())
					.setServiceStatus(ssBuilder.build()).setToken(this.getReq().getConsumer().getToken())
					.setUserName(this.getReq().getConsumer().getUserName());
			sBuilder.setResponseData(msgResponse.getBody());
			responseBuilder.addResponseBodyService(sBuilder.build());
		}
		return responseBuilder.build();
	}

	protected MsgHeader<Map<String, Object>> getMsgHeader() {
		MsgHeader<Map<String, Object>> msgHeader = new MsgHeader<Map<String, Object>>();
		MessageHeadConsumer consumer = this.getReq().getConsumer();
		msgHeader.setServiceName(consumer.getId());
		msgHeader.setReferenceNo(this.getReq().getBase().getId());
		msgHeader.setRelationNo(this.getReq().getBase().getCorrelationId());
		msgHeader.setDateTime(this.getReq().getBase().getTimeStamp());
		Map<String, Object> extendHeader = new HashMap<String, Object>();
		extendHeader.put(EC.INTF_HEAD_USER, consumer.getUserName());
		extendHeader.put(EC.INTF_HEAD_DATE, new Date(this.getReq().getBase().getTimeStamp()));
		extendHeader.put(EC.INTF_HEAD_UNIT, this.getReq().getTransaction().getUnitCode());
		msgHeader.setExtendHeader(extendHeader);
		return msgHeader;

	}

	protected String getServiceType(MessageHeadService service) {
		return service.getProperties().get(SC.SERVICE_TYPE);
	}

	protected Integer getServiceBodyType(MessageHeadService service) {
		if (service.getProperties().get(SC.SERVICE_TYPE) == null) {
			return SC.SERVICE_TYPE_MAP;
		} else {
			switch (service.getProperties().get(SC.SERVICE_TYPE)) {
				case SC.ST_ACC_OEMT:
				case SC.ST_ACC_OTME:
				case SC.ST_AMZ_OEMT:
				case SC.ST_AMZ_OTME:
				case SC.ST_HK_ACCONLINE:
				case SC.ST_HK_AMZONLINE:
					return SC.SERVICE_TYPE_LIST;
				case SC.ST_SECURITY_LOGIN:
				case SC.ST_SECURITY_LOGOUT:
					return SC.SERVICE_TYPE_MAP;
				default:
					return SC.SERVICE_TYPE_ERROR;
			}
		}
	}

	public MsgResponse<Map<String, Object>, Object> process(MessageHeadService service) {
		String bodyString = this.getReq().getBodyService(service.getId()).getRequestData().toString();
		MsgResponse<Map<String, Object>, Object> msgResponse = null;
		MsgHeader<Map<String, Object>> msgHeader = getMsgHeader();
		try {
			if (SC.SERVICE_TYPE_LIST == getServiceBodyType(service)) {
				List<Map<String, Object>> body= new ObjectMapper().readValue(bodyString, new LinkedList<Map<String, Object>>().getClass());
				MsgRequest<Map<String, Object>, List<Map<String, Object>>> msgRequest = new MsgRequest<>();
				msgRequest.setHeader(msgHeader);
				msgRequest.setBody(body);
				msgResponse = callServiceList(service, msgRequest);
			} else if (SC.SERVICE_TYPE_MAP == getServiceBodyType(service)) {
				Map<String, Object> body = new ObjectMapper().readValue(bodyString,
						new HashMap<String, Object>().getClass());
				MsgRequest<Map<String, Object>, Map<String, Object>> msgRequest = new MsgRequest<>();
				msgRequest.setHeader(msgHeader);
				msgRequest.setBody(body);
				msgResponse = callServiceMap(service, msgRequest);
			}
		} catch (IOException e) {
			BaseAppException ex = new BaseAppException(new MessageException("The JsonMessage is invalid!", e),
					LogInfoMgr.getErrorInfo("ERR_0036"));
			logger.write(LogManager.getServiceLogKey(this.getReq()), ex);
		}
		return msgResponse;
	}

	protected abstract MsgResponse<Map<String, Object>, Object> callServiceMap(MessageHeadService service,
			MsgRequest<Map<String, Object>, Map<String, Object>> msgRequest);

	protected abstract MsgResponse<Map<String, Object>, Object> callServiceList(MessageHeadService service,
			MsgRequest<Map<String, Object>, List<Map<String, Object>>> msgRequest);
	protected abstract String getServiceId();

	protected Logger getLogger() {
		return EloanService.logger;
	}

}
