/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
 * %%
 * This file is part of PortfolioEffect Quant Client.
 * 
 * PortfolioEffect Quant Client is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * PortfolioEffect Quant Client is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with PortfolioEffect Quant Client. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.portfolioeffect.quant.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.SerializationUtils;
import org.iq80.snappy.Snappy;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.MessageOutputStream;
import org.openfast.error.FastException;
import org.openfast.examples.MessageBlockReaderFactory;
import org.openfast.examples.MessageBlockWriterFactory;
import org.openfast.examples.OpenFastExample.Variant;
import org.openfast.session.Connection;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.session.tcp.TcpEndpoint;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolioeffect.quant.client.message.CalculationStatusMessage;
import com.portfolioeffect.quant.client.message.ClientMessage;
import com.portfolioeffect.quant.client.message.LogoutResponse;
import com.portfolioeffect.quant.client.message.Reject;
import com.portfolioeffect.quant.client.message.ServiceMessage;
import com.portfolioeffect.quant.client.message.TestRequest;
import com.portfolioeffect.quant.client.message.TransmitDataListMessage;
import com.portfolioeffect.quant.client.message.TransmitDataRequest;
import com.portfolioeffect.quant.client.message.TransmitDataResponse;
import com.portfolioeffect.quant.client.message.ValidationResponse;
import com.portfolioeffect.quant.client.message.type.EncryptMethodType;
import com.portfolioeffect.quant.client.message.type.EncryptedPasswordMethodType;
import com.portfolioeffect.quant.client.message.type.FastMessageType;
import com.portfolioeffect.quant.client.message.util.ClientRequestMessageFactory;
import com.portfolioeffect.quant.client.message.util.CryptograhicUtils;
import com.portfolioeffect.quant.client.message.util.ServerResponseMessageFactory;
import com.portfolioeffect.quant.client.message.util.ServerResponseMessageParser;
import com.portfolioeffect.quant.client.model.ConnectFailedException;
import com.portfolioeffect.quant.client.model.PriceDataSet;
import com.portfolioeffect.quant.client.portfolio.ArrayCache;
import com.portfolioeffect.quant.client.portfolio.ArrayCacheType;
import com.portfolioeffect.quant.client.result.Metric;
import com.portfolioeffect.quant.client.util.Console;
import com.portfolioeffect.quant.client.util.DateTimeUtil;
import com.portfolioeffect.quant.client.util.MessageStrings;
import com.portfolioeffect.quant.client.util.MetricRefreshValue;
import com.portfolioeffect.quant.client.util.ProgressBar;
import com.portfolioeffect.quant.client.util.StopWatch;
import com.portfolioeffect.quant.client.util.MetricUpdateCallback;
import com.portfolioeffect.quant.client.util.SimpleMetricUpdateCallback;

public class ClientConnection {

	private static final int MILLISEC_IN_SECOND = 1000;
	public static final String STREAM_IS_ALREADY_RUNNING = "Stream is already running";
	private static final int TIME_WAIT_TOPRINT = 20;
	private static final String SUPPORTED_CHARSET = "US-ASCII";
	private static final int TEST_PORT_NUMBER = 3443;
	private static final int MAX_BLOCK_DIMENSION = 100000;
	private static final int USER_LAYER_TIMEOUT_SECONDS_ESTIMATE =  60 * 5;
	private static final int DATA_TRANSMIT_TIMEOUT_SECONDS_ESTIMATE = 60 * 5;
	private static final int LOGON_TIMEOUT_SECONDS = 30;
	private static final int SERVICE_TIMEOUT_SEC = 30;
	private static final int PORT_NUMBER = 443;
	private static final String TEMPLATES_FILE = "config/template-quant.xml";
	private static final int LOGON_ATTEMPT_COUNT = 9;
	private static final int DEFAULT_LOGON_TIMEOUT_SEC = 30;
	private static final int HEARTBEAT_INTERVAL = 30;
	private static final EncryptMethodType ENCRYPT_METHOD_TYPE = EncryptMethodType.NONE;
	
	private int restarTimeWait =  60*3;
	
	private AtomicBoolean isStreamEnabled = new AtomicBoolean(false);
	private AtomicBoolean isStreamRuning = new AtomicBoolean(false);
	
	private String apiKey;
	private String username;
	private String password;
	private String templatesFileName;
	private String host;
	private int port;
	private Connection connection;
	private MessageOutputStream out;
	private MessageInputStream in;
	private MessageBlockWriterFactory messageBlockWriterFactory;
	private MessageBlockReaderFactory messageBlockReaderFactory;
	private volatile boolean isLoggedOn = false;
	private volatile boolean isConnected = false;
	private boolean isMessageLoggingEnabled = true;
	private Thread inboundMessageRouter;
	private Endpoint endpoint;
	private TemplateRegistry templateRegistry;
	private int outboundMsgSeqNum;
	private int indicatorDefRequestNum;
	private volatile LinkedBlockingDeque<ClientMessage> clientMessageQueue;
	private volatile LinkedBlockingDeque<ServiceMessage> serviceMessageQueue;
	private StopWatch timeDataFast = new StopWatch();
	private StopWatch timeDataTransmit = new StopWatch();
	private boolean debugModeEnabled = false;
	private StringBuffer callStatus = new StringBuffer();
	private int groupSize = 1;
	private int progressBarI = 0;
	private int progressBarMax = 0;
	private Thread heartbeatMonitor;
	private ProgressBar progressBar = new ProgressBar();

	private MetricUpdateCallback streamRefreshCallback = null;
	private SimpleMetricUpdateCallback streamRefreshCallbackPureData = null;

	private long idClient;

	private static AtomicLong idClientGenerator;

	private static AtomicLong id;

	static {
		id = new AtomicLong();
		idClientGenerator = new AtomicLong();

	}

	public static long getNewId() {
		return id.incrementAndGet();
	}

	public long getIdClient() {
		return idClient;
	}

	public ClientConnection() {
		setMessageLoggingEnabled(false);
		setTemplatesFileName(TEMPLATES_FILE);
		setPort(PORT_NUMBER);
		idClient = idClientGenerator.getAndIncrement();

	}

	public long getIdC() {
		return idClient;
	}

	/**
	 * Establishes connection to the server. Should be followed by the a call to
	 * logon(), otherwise client will be disconneted shortly.
	 * 
	 * @throws IOException
	 * @throws FastConnectionException
	 */ 
	public void start() throws IOException, FastConnectionException {

		// load connection properties from persistent storage
		endpoint = new TcpEndpoint(host, port);

		// create XML message loader template and populate its parameters
		XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
		loader.setLoadTemplateIdFromAuxId(true);

		loader.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(templatesFileName));

		// get template registry from message loader and create opefast context
		templateRegistry = loader.getTemplateRegistry();
		Context context = new Context();
		context.setTemplateRegistry(templateRegistry);

		// create message block reader & writer
		messageBlockWriterFactory = new MessageBlockWriterFactory(Variant.DEFAULT, 0, false);
		messageBlockReaderFactory = new MessageBlockReaderFactory(Variant.DEFAULT, 0, false);

		// create connection
		connection = endpoint.connect();

		// create input stream
		in = new MessageInputStream(connection.getInputStream(), context);
		in.setBlockReader(messageBlockReaderFactory.create());

		// create output stream
		out = new MessageOutputStream(connection.getOutputStream(), context);
		out.setBlockWriter(messageBlockWriterFactory.create());

		isConnected = true;

		// Client connected to endpoint

		clientMessageQueue = new LinkedBlockingDeque<ClientMessage>();
		serviceMessageQueue = new LinkedBlockingDeque<ServiceMessage>();

		inboundMessageRouter = new Thread(new InboundMessageWorker());
		inboundMessageRouter.start();

		heartbeatMonitor = new Thread(new HeartbeatMonitor());
		heartbeatMonitor.start();

	}

	/**
	 * Closes existing open connection to the server.
	 */
	/*
	 * public void stop() {
	 * 
	 * if (isConnected) {
	 * 
	 * try { if (isLoggedOn()) { logout(30); }
	 * 
	 * } catch (Exception e) { throw new
	 * RuntimeException(MessageStrings.ERROR_STOP_CLIENT, e); }
	 * 
	 * isConnected = false;
	 * 
	 * }
	 * 
	 * }
	 */

	public void stop() {

		if (isConnected) {
			try {
				if (isLoggedOn()) {
					logout(LOGON_TIMEOUT_SECONDS);
				}
				endpoint.close();
				connection.close();

				inboundMessageRouter.join();

				serviceMessageQueue.offer(new ServiceMessage(System.currentTimeMillis(), true));
				// heartbeatMonitor.join();//it dont stop
				heartbeatMonitor.interrupt();

			} catch (Exception e) {
				throw new RuntimeException(MessageStrings.ERROR_STOP_CLIENT, e);
			}

			isConnected = false;
		}

	}

	public void logout() {
		if (isConnected && isLoggedOn()) {
			Message logoutMsg = ClientRequestMessageFactory.createLogoutRequest(templateRegistry, getOutboundMsgSequenceNumber(), System.currentTimeMillis());
			out.writeMessage(logoutMsg);
		}
	};

	/**
	 * Request client logon with credentials provided using setter methods.
	 * 
	 * @throws Exception
	 */

	public void logon() {
		try {
			logon(DEFAULT_LOGON_TIMEOUT_SEC);
		} catch (Exception e) {
			stop();
			// Console.writeStackTrace(e);
		}
	};

	public void logon(int timeoutSec) throws Exception {
		logon(timeoutSec, 0);
	}

	public boolean logon(int timeoutSec, int attemptCount) throws Exception {
		if (attemptCount > LOGON_ATTEMPT_COUNT)
			return false;

		String encryptedPassword = CryptograhicUtils.encrypt(password, apiKey);
		Message loginMsg = ClientRequestMessageFactory.createLogonRequest(templateRegistry, HEARTBEAT_INTERVAL, ENCRYPT_METHOD_TYPE, username,
				encryptedPassword, EncryptedPasswordMethodType.AES, encryptedPassword.length(), getOutboundMsgSequenceNumber(), System.currentTimeMillis());

		Message msg = sendAndAwaitResponse(loginMsg, timeoutSec);
		FastMessageType responseMessageType = getMessageType(msg);

		String curentHost = getHost();
		int curentPort = getPort();

		if (responseMessageType == FastMessageType.LOGOUT) {
			LogoutResponse logoutReponse = ServerResponseMessageParser.parseLogoutResponse(msg);
			throw new Exception(logoutReponse.getText());
		}

		setHost(curentHost);
		setPort(curentPort);

		if (attemptCount == 0 && !isLoggedOn()) {
			throw new ConnectFailedException();

		}

		return isLoggedOn;
	}

	/**
	 * Request client logout
	 */
	public void logout(int timeoutSec) {
		if (!isLoggedOn())
			return;
		Message logoutMsg = ClientRequestMessageFactory.createLogoutRequest(templateRegistry, getOutboundMsgSequenceNumber(), System.currentTimeMillis());
		try {
			sendAndAwaitResponse(logoutMsg, timeoutSec);
		} catch (Exception e) {
			isLoggedOn = false;
		}
	}

	public void progressBarIAdd(int value) {
		if (progressBarMax == 0) {
			progressBarMax = value * groupSize;
			progressBarI = 0;
		}
		progressBar.printCompletionStatus(progressBarI, progressBarMax);
		progressBarI += value;
		progressBar.printCompletionStatus(progressBarI, progressBarMax);
		if (progressBarI == progressBarMax) {
			createCallGroup(1);
		}
	}

	public void createCallGroup(int groupSize) {
		this.groupSize = groupSize;
		this.progressBarI = 0;
		this.progressBarMax = 0;
		progressBar.reset();
		progressBar.setScale(groupSize);

	}

	public void printProgressBar(double percent) {
		progressBar.printCompletionStatus(percent);
	}

	public void proggressBarOn() {
		progressBar.setON(true);
	}

	public void proggressBarOff() {
		progressBar.setON(false);
	}

	public void resetProgressBar() {
		progressBar.reset();
	}

	public void setHost(String host) {
		this.host = host;
		if (host.equals("localhost"))
			setPort(TEST_PORT_NUMBER);
		else
			setPort(PORT_NUMBER);
	}

	public Metric start(String username, String password, String apiKey, String remoteHostName) {

		clearStatus();
		stop();

		setMessageLoggingEnabled(false);
		setTemplatesFileName(TEMPLATES_FILE);

		if (remoteHostName.equals("localhost"))
			setPort(TEST_PORT_NUMBER);
		else
			setPort(PORT_NUMBER);

		setUsername(username);
		setPassword(password);
		setApiKey(apiKey);
		setHost(remoteHostName);

		try {
			return restart();
		} catch (Exception e) {
			this.stop();
			if (e.getMessage().contains(":"))
				return new Metric(e.getMessage().split(":")[1]);
			else
				return new Metric(e.getMessage());
			//return new Metric(MessageStrings.ERROR_CONNECT);
		}
//		try {
//			logon(LOGON_TIMEOUT_SECONDS);
//		} catch (Exception e) {
//			stop();
//			
//		}

		//return new Metric();
	}

	private void clearStatus() {
		callStatus.setLength(0);
	}

	public Metric restart() {
		int totalTime = 0;
		 
		int waitTime=-1;
		while(totalTime<restarTimeWait){
			waitTime++;
			stop();
			try {
					if(waitTime>=1){
						totalTime += waitTime;
						if (totalTime > TIME_WAIT_TOPRINT)
							Console.write(MessageStrings.CONNECTING);
	
						waitAndDots(waitTime, totalTime);
					}
			} catch (InterruptedException e) {
			}

			clearStatus();

			try {
				start();
				logon(LOGON_TIMEOUT_SECONDS);
			} catch (IOException e) {
				continue;
			} catch (FastConnectionException e) {
				continue;
			} catch (ConnectFailedException e) {
				continue;
			} catch (Exception e) {
				stop();
				return new Metric(e.getMessage());
			}
			if (isLoggedOn()) {
				if (totalTime > TIME_WAIT_TOPRINT)
					Console.writeln(MessageStrings.OK);

				return new Metric();
			}

		}
		if (!isLoggedOn()) {
			return new Metric(MessageStrings.ERROR_CONNECT);
		}
		return new Metric();

	}

	private void waitAndDots(int sec, int totalTime) throws InterruptedException {
		if (totalTime > TIME_WAIT_TOPRINT) {
			Console.write(".");
		}
		Thread.sleep(sec*MILLISEC_IN_SECOND);
	}

	public double getDataVolume(double[] price, int[] timeSec) {
		PriceDataSet data;
		try {
			data = new PriceDataSet(price, timeSec);
		} catch (Exception e) {
			return 0;
		}
		double a = data.toBinaryZipCompress().length;
		return a / 1024 / 1024;
	}

	public double getDataVolume(double[] price) {
		return getDataVolume(price, new int[0]);
	}

	public static boolean isPureAscii(String v) {
		CharsetEncoder asciiEncoder = Charset.forName(SUPPORTED_CHARSET).newEncoder();
		return asciiEncoder.canEncode(v);
	}

	public Metric validateStringRequest(String requestString) throws Exception {
		
		if (isStreamEnabled.get()) {
			return new Metric(STREAM_IS_ALREADY_RUNNING);
		}

		if (!isPureAscii(requestString))
			return new Metric("Request " + MessageStrings.NON_ASCII);

		String[] paramList = new String[0];

		Message msg = ClientRequestMessageFactory.createValidationRequest(getTemplateRegistry(), requestString, getOutboundMsgSequenceNumber(),
				System.currentTimeMillis());
		Message responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

		ValidationResponse response = ServerResponseMessageParser.parseValidationResponse(responseMsg);
		if (response.getMsgType().equals("OK")) {

			Type mapType = new TypeToken<String[]>() {
			}.getType();
			Gson gson = new Gson();

			paramList = gson.fromJson(response.getMsgBody(), mapType);
		} else {
			return new Metric(response.getMsgBody());
		}

		Metric result = new Metric();
		ArrayCache pL = new ArrayCache(paramList);
		result.setData("positions", pL);
		return result;
	}

	public Metric transmitQuantity(String assetName, int[] dataInt, long[] time) throws Exception {

		if (isStreamEnabled.get()) {

			return new Metric(STREAM_IS_ALREADY_RUNNING);
		}
		boolean isFirstBlock = true;
		int position = 0;

		for (int i = 0; i < time.length / MAX_BLOCK_DIMENSION; i++) {
			int[] dataTransmit = new int[MAX_BLOCK_DIMENSION];
			long[] timeTransmit = new long[MAX_BLOCK_DIMENSION];

			System.arraycopy(time, position, timeTransmit, 0, MAX_BLOCK_DIMENSION);
			System.arraycopy(dataInt, position, dataTransmit, 0, MAX_BLOCK_DIMENSION);

			String type = "QUANTITY";
			if (!isFirstBlock) {
				type += ":+";
			} else
				isFirstBlock = false;

			String request = assetName;
			Message msg = ServerResponseMessageFactory.createTransmitDataRequest(getTemplateRegistry(), type, request, dataTransmit, timeTransmit,
					getOutboundMsgSequenceNumber(), System.currentTimeMillis());

			Message responseMsg = sendAndAwaitResponse(msg, DATA_TRANSMIT_TIMEOUT_SECONDS_ESTIMATE);

			TransmitDataResponse response = ServerResponseMessageParser.parseTransmitDataResponse(responseMsg);

			position += MAX_BLOCK_DIMENSION;

			if (!response.getMsgType().equals("OK"))
				throw new Exception(response.getMsgBody());

		}

		if (time.length % MAX_BLOCK_DIMENSION != 0) {
			int[] dataTransmit = new int[time.length % MAX_BLOCK_DIMENSION];
			long[] timeTransmit = new long[time.length % MAX_BLOCK_DIMENSION];

			System.arraycopy(time, position, timeTransmit, 0, time.length % MAX_BLOCK_DIMENSION);
			System.arraycopy(dataInt, position, dataTransmit, 0, dataInt.length % MAX_BLOCK_DIMENSION);

			String type = "QUANTITY";
			if (!isFirstBlock) {
				type += ":+";
			} else
				isFirstBlock = false;

			String request = assetName;
			Message msg = ServerResponseMessageFactory.createTransmitDataRequest(getTemplateRegistry(), type, request, dataTransmit, timeTransmit,
					getOutboundMsgSequenceNumber(), System.currentTimeMillis());

			Message responseMsg = sendAndAwaitResponse(msg, DATA_TRANSMIT_TIMEOUT_SECONDS_ESTIMATE);

			TransmitDataResponse response = ServerResponseMessageParser.parseTransmitDataResponse(responseMsg);

			if (!response.getMsgType().equals("OK"))
				throw new Exception(response.getMsgBody());

		}

		Metric result = new Metric();
		result.setMessage("NON");

		return result;
	}

	public boolean transmitStreamQuantity(String assetName, int quantity, long time) throws Exception {

		if(!isStreamRuning.get())
			return false;
		
	
		
		int[] dataTransmit = new int[] { quantity };
		long[] timeTransmit = new long[] { time };

		String type = "QUANTITY:stream";

		String request = assetName;
		Message msg = ServerResponseMessageFactory.createTransmitDataRequest(getTemplateRegistry(), type, request, dataTransmit, timeTransmit,
				getOutboundMsgSequenceNumber(), System.currentTimeMillis());

		send(msg);
		return true;
	}

	public Metric transmitUserPrice(String assetName, float[] dataFloat, long[] time) throws Exception {
		if (isStreamEnabled.get()) {
			return new Metric(STREAM_IS_ALREADY_RUNNING);
		}
		boolean isFirstBlock = true;
		int position = 0;

		for (int i = 0; i < time.length / MAX_BLOCK_DIMENSION; i++) {
			float[] dataTransmit = new float[MAX_BLOCK_DIMENSION];
			long[] timeTransmit = new long[MAX_BLOCK_DIMENSION];

			System.arraycopy(time, position, timeTransmit, 0, MAX_BLOCK_DIMENSION);
			System.arraycopy(dataFloat, position, dataTransmit, 0, MAX_BLOCK_DIMENSION);

			String type = "USER_PRICE";
			if (!isFirstBlock) {
				type += ":+";
			} else {
				isFirstBlock = false;
			}

			String request = assetName;
			Message msg = ServerResponseMessageFactory.createTransmitDataRequest(getTemplateRegistry(), type, request, dataTransmit, timeTransmit,
					getOutboundMsgSequenceNumber(), System.currentTimeMillis());

			Message responseMsg = sendAndAwaitResponse(msg, DATA_TRANSMIT_TIMEOUT_SECONDS_ESTIMATE);

			TransmitDataResponse response = ServerResponseMessageParser.parseTransmitDataResponse(responseMsg);

			position += MAX_BLOCK_DIMENSION;

			if (!response.getMsgType().equals("OK"))
				throw new Exception(response.getMsgBody());

		}

		if (time.length % MAX_BLOCK_DIMENSION != 0) {

			float[] dataTransmit = new float[time.length % MAX_BLOCK_DIMENSION];
			long[] timeTransmit = new long[time.length % MAX_BLOCK_DIMENSION];

			System.arraycopy(time, position, timeTransmit, 0, time.length % MAX_BLOCK_DIMENSION);
			System.arraycopy(dataFloat, position, dataTransmit, 0, time.length % MAX_BLOCK_DIMENSION);

			String type = "USER_PRICE";
			if (!isFirstBlock) {
				type += ":+";
			} else
				isFirstBlock = false;

			String request = assetName;
			Message msg = ServerResponseMessageFactory.createTransmitDataRequest(getTemplateRegistry(), type, request, dataTransmit, timeTransmit,
					getOutboundMsgSequenceNumber(), System.currentTimeMillis());

			Message responseMsg = sendAndAwaitResponse(msg, DATA_TRANSMIT_TIMEOUT_SECONDS_ESTIMATE);

			TransmitDataResponse response = ServerResponseMessageParser.parseTransmitDataResponse(responseMsg);

			if (!response.getMsgType().equals("OK"))
				throw new Exception(response.getMsgBody());

		}

		Metric result = new Metric();
		result.setMessage("NON");

		return result;

	}

	public Metric transmitDataList(String fromTime, String toTime, ArrayList<String> dataList, String windowLength, String priceSamplingInterval,
				String momentsModel, String trainingPeriodEnabled) throws Exception {
		
		if (isStreamEnabled.get()) {
			return new Metric(STREAM_IS_ALREADY_RUNNING);
		}

		for (String e : dataList){
			if (!isPureAscii(e))
				return new Metric("Position name " + MessageStrings.NON_ASCII);
			
			boolean isHystoryPrice = e.contains("h-") || e.contains("hI-");
			
			if(isHystoryPrice && fromTime.contains("#"))
				return new Metric("fromTime is not set");
			
			if(isHystoryPrice && toTime.contains("#"))
				return new Metric("toTime is not set");
		}

		timeDataFast.reset();
		timeDataTransmit.reset();

		String requestType = "CHECK_DATA";

		TransmitDataListMessage dataListMessage = new TransmitDataListMessage(dataList, windowLength, fromTime, toTime, priceSamplingInterval, momentsModel, trainingPeriodEnabled);

		Gson gson = new Gson();
		Type mapType = new TypeToken<TransmitDataListMessage>() {
		}.getType();
		String request = gson.toJson(dataListMessage, mapType);

		timeDataFast.start();
		Message msg = ServerResponseMessageFactory.createTransmitDataRequest(getTemplateRegistry(), requestType, request, getOutboundMsgSequenceNumber(),
				System.currentTimeMillis());
		timeDataFast.stop();
		timeDataTransmit.start();
		Message responseMsg = sendAndAwaitResponse(msg, DATA_TRANSMIT_TIMEOUT_SECONDS_ESTIMATE);

		timeDataTransmit.stop();
		timeDataFast.start();
		TransmitDataResponse response = ServerResponseMessageParser.parseTransmitDataResponse(responseMsg);
		timeDataFast.stop();

		if (response.getMsgType().equals("OK")) {

			Metric result = new Metric();
			result.setMessage(response.getMsgBody());

			return result;

		} else {
			return new Metric(response.getMsgBody());
		}
	}

	public Metric estimateEstimator(String metricType) throws Exception {

		if (isStreamEnabled.get()) {
			return new Metric(STREAM_IS_ALREADY_RUNNING);
		}
		
		HashMap<String, String> info = new HashMap<String, String>();
		ArrayCache resultValueList = null;
		ArrayCache resultTimeList = null;

		boolean isRun = true;
		boolean isFirstBlock = true;
		double percent = 0;

		int[] dimensions = null;
		progressBar.printCompletionStatus(percent);

		while (isRun) {

			clearStatus();
			Message responseMsg;
			if (isFirstBlock) {

				resultValueList = new ArrayCache(ArrayCacheType.DOUBLE_VECTOR);
				resultTimeList = new ArrayCache(ArrayCacheType.LONG_VECTOR);

				// Gson gson = new Gson();

				Message msg = ClientRequestMessageFactory.createNonparametricComputeRequest(getTemplateRegistry(), metricType, "", new double[1], new int[1],
						getOutboundMsgSequenceNumber(), System.currentTimeMillis());

				responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

				isFirstBlock = false;
			} else {

				Message msg = ClientRequestMessageFactory.createNonparametricComputeRequest(getTemplateRegistry(), metricType, "#NEXT#", new double[1],
						new int[1], getOutboundMsgSequenceNumber(), System.currentTimeMillis());

				responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

			}

			TransmitDataRequest response = ServerResponseMessageParser.parseTransmitDataRequest(responseMsg);

			if (response.getMsgType().contains("OK")) {

				Gson gson = new Gson();
				Type mapType = new TypeToken<CalculationStatusMessage>() {
				}.getType();

				CalculationStatusMessage statusMessg = gson.fromJson(response.getMsgType(), mapType);

				dimensions = statusMessg.getDimension();

				float[] data = response.getDataFloat();
				long[] time = response.getTime();

				resultValueList.writeAsDouble(data);

				resultTimeList.write(time);

				percent = Double.valueOf(response.getMsgBody());
				progressBar.printCompletionStatus(percent);

				if (response.getMsgType().contains("STOP")) {

					info = statusMessg.getResultInfo();

					isRun = false;
				}
			} else {

				throw new Exception(response.getMsgBody());
			}
		}

		if (dimensions != null)
			resultValueList.setDimensions(dimensions);

		Metric result = new Metric();
		result.setData("value", resultValueList);
		result.setData("time", resultTimeList);
		result.setInfo(info);

		return result;
	}

	
	

	public void stopStream() {
		isStreamEnabled.set(false);
		stop();
		batchMetricKeys = null;
		//streamRefreshCallback = null;
		//streamRefreshCallbackPureData =null;
		
	}

	private List<String> batchMetricKeys = null;

	public List<String> getBatchMetricKeys() {
		return batchMetricKeys;
	}

	public void setBatchMetricKeys(List<String> batchMetricKeys, long portfolioID) {
		this.batchMetricKeys = new ArrayList<String>();

		for (String e : batchMetricKeys) {
			if (e.charAt(0) == '{' && e.length() > 1) {
				this.batchMetricKeys.add("{" + "portfolioID:" + portfolioID + ", request:" + e);
			} else
				this.batchMetricKeys.add(e);
		}

		// this.batchMetricKeys = batchMetricKeys;
	}

	public Metric estimateTransactional(String metricType, String indexPosition, ArrayList<String> positionList, String params) throws Exception {

		if (metricType.contains("stream")) {
			if (isStreamEnabled.get()) {
				return new Metric(STREAM_IS_ALREADY_RUNNING);
			}
			isStreamEnabled.set(true);
		}

		HashMap<String, String> info = new HashMap<String, String>();

		boolean isRun = true;
		boolean isFirstBlock = true;
		double percent = 0;

		int[] dimensions = null;
		progressBar.printCompletionStatus(percent);

		ArrayCache batchValues[] = null;
		ArrayCache batchValuesTime[] = null;

		// ArrayCache resultValueList = new
		// ArrayCache(ArrayCacheType.DOUBLE_VECTOR);
		// ArrayCache resultTimeList = new
		// ArrayCache(ArrayCacheType.LONG_VECTOR);

		while (isRun) {

			clearStatus();
			Message responseMsg;
			if (isFirstBlock) {

				if (indexPosition.length() != 0)
					positionList.add(0, indexPosition);

				Gson gson = new Gson();

				String request = gson.toJson(positionList);
				
				if(debugModeEnabled){
					Console.writeln("Request--->");
					Console.writeln(metricType);
					Console.writeln(request);
					Console.writeln(params);
					Console.writeln(">---");
				}
				
				
				Message msg = ClientRequestMessageFactory.createTransactionalPortfolioComputeRequest(getTemplateRegistry(), metricType, request, params,
						getOutboundMsgSequenceNumber(), System.currentTimeMillis());

				responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

			} else {

				Message msg = ClientRequestMessageFactory.createTransactionalPortfolioComputeRequest(getTemplateRegistry(), metricType, "#NEXT#", "",
						getOutboundMsgSequenceNumber(), System.currentTimeMillis());

				responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

			}

			TransmitDataRequest response = ServerResponseMessageParser.parseTransmitDataRequest(responseMsg);

			if (response.getMsgType().contains("OK")) {

				Gson gson = new Gson();
				Type mapType = new TypeToken<CalculationStatusMessage>() {
				}.getType();

				CalculationStatusMessage statusMessg = gson.fromJson(response.getMsgType(), mapType);

				dimensions = statusMessg.getDimension();
				if (dimensions.length == 0)
					dimensions = new int[] { 1 };

				float[] data = response.getDataFloat();
				long[] time = response.getTime();
				
				if(debugModeEnabled){
					
					if(data.length>0)
						Console.writeln("RECEIVED DATA BLOCK("+ data.length+"): "+data[0]+"\t"+data[data.length-1]);
				
					
					if(time.length>0)
						Console.writeln("RECEIVED TIME BLOCK("+ time.length+"): "+ 
					  (new Timestamp(time[0] +DateTimeUtil.CLIENT_TIME_DELTA))
							+"\t"+
							(new Timestamp(time[time.length-1] +DateTimeUtil.CLIENT_TIME_DELTA)));
					
				
					
				}

				if (isFirstBlock) {

					// resultValueList = new
					// ArrayCache(ArrayCacheType.DOUBLE_VECTOR);
					// resultTimeList = new
					// ArrayCache(ArrayCacheType.LONG_VECTOR);

					// resultValueList.writeAsDouble(data);
					// resultTimeList.write(time);

					// resultValueList.setDimensions(dimensions);

					// batchValues =
					// ArrayCache.splitBatchDouble(resultValueList);
					// -----------------

					batchValues = new ArrayCache[dimensions.length];
					for (int i = 0; i < dimensions.length; i++) {
						if (dimensions[i] == 1)
							batchValues[i] = new ArrayCache(ArrayCacheType.DOUBLE_VECTOR);
						else
							batchValues[i] = new ArrayCache(ArrayCacheType.DOUBLE_MATRIX);

						batchValues[i].setDimensions(new int[] { dimensions[i] });
					}

					batchValuesTime = new ArrayCache[batchValues.length];
					for (int k = 0; k < batchValues.length; k++)
						batchValuesTime[k] = new ArrayCache(ArrayCacheType.LONG_VECTOR);

					for (int k = 0; k < batchValues.length; k++) {
						batchValuesTime[k].lockToWrite();
						batchValues[k].lockToWrite();
					}

					int len = 0;
					while (len < data.length) {
						for (int k = 0; k < dimensions.length; k++) {
							for (int m = 0; m < dimensions[k]; m++) {
								batchValues[k].writeNextDouble(data[len]);
								len++;
							}
						}
					}

					for (int k = 0; k < batchValues.length; k++)
						batchValuesTime[k].writeNextLong(time);

					for (int k = 0; k < batchValues.length; k++) {
						batchValuesTime[k].unlockToWrite();
						batchValues[k].unlockToWrite();
					}

					isFirstBlock = false;

				} else {

					for (int k = 0; k < batchValues.length; k++) {
						batchValuesTime[k].lockToWrite();
						batchValues[k].lockToWrite();
					}

					for (int k = 0; k < batchValues.length; k++)
						batchValuesTime[k].writeNextLong(time);

					int len = 0;
					while (len < data.length) {
						for (int k = 0; k < dimensions.length; k++) {
							for (int m = 0; m < dimensions[k]; m++) {
								batchValues[k].writeNextDouble(data[len]);
								len++;
							}
						}
					}

					for (int k = 0; k < batchValues.length; k++) {
						batchValuesTime[k].unlockToWrite();
						batchValues[k].unlockToWrite();
					}

				}

				percent = Double.valueOf(response.getMsgBody());
				progressBar.printCompletionStatus(percent);

				if (isStreamEnabled.get() && percent >= 1) {

					StreamWoker streamWoker = new StreamWoker(batchValues, batchValuesTime, dimensions, streamRefreshCallback, streamRefreshCallbackPureData);
					new Thread(streamWoker).start();
					// streamWoker.run();

					info = statusMessg.getResultInfo();
					isRun = false;

				}

				if (response.getMsgType().contains("STOP")) {

					info = statusMessg.getResultInfo();

					isRun = false;
				}
			} else {
				isStreamEnabled.set(false);
				throw new Exception(response.getMsgBody());
			}
		}

		Metric result = new Metric();
		result.setData("values", batchValues);
		result.setData("times", batchValuesTime);
		result.setInfo(info);
		
		
		return result;

	}

	public Metric getAllSymbolsList() throws Exception {

		Message responseMsg;
		Message msg = ClientRequestMessageFactory.createTransactionalPortfolioComputeRequest(getTemplateRegistry(), "ALL_SYMBOLS", "", "",
				getOutboundMsgSequenceNumber(), System.currentTimeMillis());

		responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

		TransmitDataRequest response = ServerResponseMessageParser.parseTransmitDataRequest(responseMsg);
		Metric result = new Metric();

		if (response.getMsgType().contains("OK")) {

			byte data[] = response.getDataFloatByte();

			data = Snappy.uncompress(data, 0, data.length);
			Map<String, String[]> map = (Map<String, String[]>) SerializationUtils.deserialize(data);

			ArrayCache id = new ArrayCache(map.get("id"));
			ArrayCache description = new ArrayCache(map.get("description"));
			ArrayCache exchange = new ArrayCache(map.get("exchange"));

			result.setData("id", id);
			result.setData("description", description);
			result.setData("exchange", exchange);

		} else {

			throw new Exception(response.getMsgBody());
		}

		return result;
	}

	public Metric getComputeTimeLeft() {

		for (int i = 0; i < 3; i++) {
			Message responseMsg;

			try {
				Message msg = ClientRequestMessageFactory.createTransactionalPortfolioComputeRequest(getTemplateRegistry(), "TIME_LEFT", "", "",
						getOutboundMsgSequenceNumber(), System.currentTimeMillis());

				responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

				TransmitDataRequest response = ServerResponseMessageParser.parseTransmitDataRequest(responseMsg);
				Metric result = new Metric();

				if (response.getMsgType().contains("OK")) {

					String data[] = response.getMsgBody().split("#");
					HashMap<String, String> info = new HashMap<String, String>();
					info.put("timeLeft", data[0]);
					info.put("timeMax", data[1]);
					result.setInfo(info);

				} else {

					throw new Exception(response.getMsgBody());
				}

				return result;

			} catch (Exception e) {

				Metric result = processException(e);
				if (result == null)
					continue;
				return result;
			}

		}
		return new Metric(MessageStrings.FAILED_SERVER_TIME_OUT);

	}

	private Metric processException(Exception e) {

		if (e instanceof ConnectFailedException) {

			Metric isRestarted = restart();
			if (isRestarted.hasError()) {
				resetProgressBar();
				return new Metric(isRestarted.getErrorMessage());
			}

			return null;
		}

		if (e.getMessage() == null || e.getMessage().contains("No data in cache") || e.getMessage().contains("null")) {

			Metric isRestarted = restart();
			if (isRestarted.hasError()) {
				resetProgressBar();
				return new Metric(isRestarted.getErrorMessage());
			}

			return null;

		}

		if (e.getMessage() == null) {
			Console.writeStackTrace(e);
			return new Metric(MessageStrings.ERROR_101);
		}

		resetProgressBar();
		return new Metric(e.getMessage());

	}

	private Message sendAndAwaitResponse(Message request, int timeoutSec) throws Exception {

		if (!isConnected)
			throw new ConnectFailedException();

		try {
			out.writeMessage(request);
		} catch (Exception e) {
			throw new ConnectFailedException();
		}

		ClientMessage clientMessage = clientMessageQueue.poll(timeoutSec, TimeUnit.SECONDS);
		clientMessageQueue.clear();

		if (clientMessage == null) {
			throw new ConnectFailedException();
		}
		if (clientMessage == null || clientMessage.isEmpty()) {

			throw new ConnectFailedException();
		} else if (clientMessage.isRejected()) {

			Reject reject = ServerResponseMessageParser.parseReject(clientMessage.getMessage());
			throw new Exception(reject.getText());
		}

		return clientMessage.getMessage();

	}

	public void send(Message request) throws Exception {

		if (!isConnected)
			throw new ConnectFailedException();

		try {
			out.writeMessage(request);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConnectFailedException("Error writing to stream");
		}

	}

	public Message awaitResponse() throws Exception {

		if (!isConnected)
			throw new ConnectFailedException();

		ClientMessage clientMessage = clientMessageQueue.poll(200, TimeUnit.DAYS);
		clientMessageQueue.clear();

		if (clientMessage == null) {
			throw new ConnectFailedException();
		}
		if (clientMessage == null || clientMessage.isEmpty()) {

			throw new ConnectFailedException();
		} else if (clientMessage.isRejected()) {

			Reject reject = ServerResponseMessageParser.parseReject(clientMessage.getMessage());
			throw new Exception(reject.getText());
		}

		return clientMessage.getMessage();

	}

	private void sendHeartbeat(String testReqId) {
		Message logoutMsg = ClientRequestMessageFactory.createHeartbeat(templateRegistry, nextOutboundMsgSequenceNumber(), testReqId);
		out.writeMessage(logoutMsg);
	}

	public TemplateRegistry getTemplateRegistry() throws Exception {
		if (templateRegistry == null) {
			throw new ConnectFailedException();
		}
		return templateRegistry;
	}

	private FastMessageType getMessageType(Message msg) {
		String msgTypeCode = msg.getString("MessageType");
		FastMessageType fastMsgType = FastMessageType.getFastMessageType(msgTypeCode);
		return fastMsgType;
	}

	private class StreamWoker implements Runnable {

		private ArrayCache batchValues[];
		private ArrayCache batchValuesTime[];
		private int[] dimensions;
		private MetricUpdateCallback streamRefreshCallback = null;
		private SimpleMetricUpdateCallback streamRefreshCallbackPureData = null;

		public StreamWoker(ArrayCache[] batchValues, ArrayCache[] batchValuesTime, int dimensions[], MetricUpdateCallback streamRefreshCallback,
				SimpleMetricUpdateCallback streamRefreshCallbackPureData) {
			this.batchValues = batchValues;
			this.batchValuesTime = batchValuesTime;
			this.dimensions = dimensions;
			this.streamRefreshCallback = streamRefreshCallback;
			this.streamRefreshCallbackPureData = streamRefreshCallbackPureData;
		}

		@Override
		public void run() {

			Console.writeln("Start stream data");
			isStreamRuning.set(true);
			String stopReason = "terminated by user";
			while (isStreamEnabled.get() && !Thread.interrupted()) {
				try {

					Message responseMsg = awaitResponse();

					FastMessageType responseMessageType = getMessageType(responseMsg);
					if (responseMessageType == FastMessageType.LOGOUT) {
						clientMessageQueue.offer(new ClientMessage(responseMsg));

						break;
					}

					TransmitDataRequest response = ServerResponseMessageParser.parseTransmitDataRequest(responseMsg);

					if (response.getMsgType().contains("OK")) {

						// Gson gson = new Gson();
						// Type mapType = new
						// TypeToken<CalculationStatusMessage>() {
						// }.getType();

						// CalculationStatusMessage statusMessg =
						// gson.fromJson(response.getMsgType(), mapType);

						float[] data = response.getDataFloat();
						long[] time = response.getTime();

						if (data.length == 0)
							continue;

						for (int k = 0; k < batchValues.length; k++) {
							batchValuesTime[k].lockToWrite();
							batchValues[k].lockToWrite();
						}

						int len = 0;

						// while (len < data.length)
						for (int t = 0; t < time.length; t++) {
							for (int k = 0; k < dimensions.length; k++) {

//								boolean flag = true;
//								for (int m = 0; m < dimensions[k]; m++) {
//									flag = flag && Double.isNaN(data[len + m]);
//								}
//
//								if (flag){
//									len+=dimensions[k];
//									continue;
//								}

								for (int m = 0; m < dimensions[k]; m++) {

									batchValues[k].writeNextDouble(data[len]);

									len++;
								}
								batchValuesTime[k].writeNextLong(time[t]);

							}

						}

						// for (int k = 0; k < batchValues.length; k++)
						// batchValuesTime[k].writeNextLong(time);

						for (int k = 0; k < batchValues.length; k++) {
							batchValuesTime[k].unlockToWrite();
							batchValues[k].unlockToWrite();
						}

						if (streamRefreshCallback != null) {

							List<MetricRefreshValue> refreshValue = new ArrayList<MetricRefreshValue>();
							len = 0;
							for (int i = 0; i < time.length; i++) {

								for (int j = 0; j < dimensions.length; j++)
									for (int k = 0; k < dimensions[j]; k++) {
										if (!Double.isNaN(data[len]) && batchMetricKeys != null)
											refreshValue.add(new MetricRefreshValue(batchMetricKeys.get(j), k, data[len], time[i]));
										len++;
									}

							}
							
							if(refreshValue.size()>0)
								streamRefreshCallback.onDataRefresh(refreshValue);
						}

						if (streamRefreshCallbackPureData != null && time.length>0)
							streamRefreshCallbackPureData.onDataRefresh(data, time);

						// {
						// System.out.println("=====new  data block========="+(new
						// Timestamp(System.currentTimeMillis()))+"=======================================");
						//
						// //System.out.println("data size=" + data.length +
						// "\t" + data[0] + "\t" + data[data.length - 1]);
						// System.out.println("time size=" + time.length + "\t"
						// + (new Timestamp(time[0])) + "\t" + (new
						// Timestamp(time[time.length - 1])));
						//
						// //System.out.println("=============================");
						// }

						if (response.getMsgType().contains("STOP")) {
							stopReason = "terminated by server";
							break;
						}
					} else {
						stopReason = response.getMsgBody();
						break;

					}

				} catch (IOException e) {
					stopReason = "Error - " + e.getMessage();
					break;
				} catch (Exception e) {
					e.printStackTrace();
					stopReason = "Error - " + e.getMessage();
					break;
				}
			}

			Console.writeln("Stop stream data: " + stopReason);
			isStreamEnabled.set(false);
			isStreamRuning.set(false);

		}

	}

	private class InboundMessageWorker implements Runnable {
		public void run() {
			try {
				// Started inbound message logger thread
				while (true) {

					Message msg = in.readMessage();

					if (msg == null) {
						// End of input stream. Exiting from inbound message
						// worker thread
						break;
					}

					FastMessageType responseMessageType = getMessageType(msg);

					serviceMessageQueue.offer(new ServiceMessage(System.currentTimeMillis()));

					switch (responseMessageType) {
					case TEST_REQUEST:
						TestRequest testRequest = ServerResponseMessageParser.parseTestRequest(msg);
						sendHeartbeat(testRequest.getTestReqID());
						break;
					case HEARTBEAT:
						break;
					case LOGON:
						isLoggedOn = true;
						clientMessageQueue.offer(new ClientMessage(msg));
						break;
					case LOGOUT:
						isLoggedOn = false;
						clientMessageQueue.offer(new ClientMessage(msg));
						break;
					case REJECT:
						clientMessageQueue.offer(new ClientMessage(msg, true, false));
						break;
					default:
						clientMessageQueue.offer(new ClientMessage(msg));
						break;
					}

					if (isMessageLoggingEnabled)
						System.out.println("Recieved message: " + msg.toString());
				}
			} catch (FastException e) {
				isLoggedOn = false;
				isConnected = false;
				// Connection was terminated
			}
		}
	}

	private class HeartbeatMonitor implements Runnable {

		@Override
		public void run() {

			while (!Thread.interrupted()) {
				try {
					ServiceMessage serviceMessage = serviceMessageQueue.poll(SERVICE_TIMEOUT_SEC, TimeUnit.SECONDS);

					if (serviceMessage == null) {
						clientMessageQueue.offer(new ClientMessage(null, false, true));

					} else if (serviceMessage.isTerminated()) {
						break;
					} else {
					}

				} catch (Exception e) {

					break;
				}
			}
		}

	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTemplatesFileName() {
		return templatesFileName;
	}

	public void setTemplatesFileName(String templatesFileName) {
		this.templatesFileName = templatesFileName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setMessageLoggingEnabled(boolean isMessageLoggingEnabled) {
		this.isMessageLoggingEnabled = isMessageLoggingEnabled;
	}

	public boolean isMessageLoggingEnabled() {
		return isMessageLoggingEnabled;
	}

	public boolean isLoggedOn() {
		return isLoggedOn;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public int nextOutboundMsgSequenceNumber() {
		return outboundMsgSeqNum++;
	}

	public int getOutboundMsgSequenceNumber() {
		return outboundMsgSeqNum++;
	}

	public boolean isDebugModeEnabled() {
		return debugModeEnabled;
	}

	public void setDebugModeEnabled(boolean debugModeEnabled) {
		this.debugModeEnabled = debugModeEnabled;
	}

	public String getStatus() {
		return callStatus.toString();
	}

	@Override
	protected void finalize() throws Throwable {

		this.stop();

		super.finalize();
	}

	public void setStreamRefreshCallback(MetricUpdateCallback streamRefreshCallback) {
		this.streamRefreshCallback = streamRefreshCallback;
	}

	public SimpleMetricUpdateCallback getStreamRefreshCallbackPureData() {
		return streamRefreshCallbackPureData;
	}

	public void setStreamRefreshCallbackPureData(SimpleMetricUpdateCallback streamRefreshCallbackPureData) {
		this.streamRefreshCallbackPureData = streamRefreshCallbackPureData;
	}
	
	public AtomicBoolean isStreamEnabled() {
		return isStreamEnabled;
	}

	public int getRestarTimeWait() {
		return restarTimeWait;
	}

	public void setRestarTimeWait(int restarTimeWait) {
		this.restarTimeWait = restarTimeWait;
	}

}
