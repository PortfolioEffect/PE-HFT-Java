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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
import com.portfolioeffect.quant.client.message.NonparametricComputeResponse;
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
import com.portfolioeffect.quant.client.result.MethodResult;
import com.portfolioeffect.quant.client.util.Console;
import com.portfolioeffect.quant.client.util.MessageStrings;
import com.portfolioeffect.quant.client.util.ProgressBar;
import com.portfolioeffect.quant.client.util.StopWatch;

public class ClientConnection  {

	private static final int RESTART_ATTEMPTS_COUNT = 5;
	private static final int TIME_WAIT_TOPRINT = 30;
	private static final String SUPPORTED_CHARSET = "US-ASCII";
	private static final int TEST_PORT_NUMBER = 3443;
	private static final int MAX_BLOCK_DIMENSION = 100000;
	private static final int BLOCK_DIMENSION_DECREASE_STEP = 10;
	private static final int USER_LAYER_TIMEOUT_SECONDS_ESTIMATE = 60 * 5;
	private static final int DATA_TRANSMIT_TIMEOUT_SECONDS_ESTIMATE = 60 * 5;
	private static final int LOGON_TIMEOUT_SECONDS = 30;
	private static final int SERVICE_TIMEOUT_SEC = 30;
	private static final int PORT_NUMBER = 443;
	private static final String TEMPLATES_FILE = "config/template-quant.xml";
	private static final int LOGON_ATTEMPT_COUNT = 9;
	private static final int DEFAULT_LOGON_TIMEOUT_SEC = 30;
	private static final int HEARTBEAT_INTERVAL = 30;
	private static final EncryptMethodType ENCRYPT_METHOD_TYPE = EncryptMethodType.NONE;

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
	private boolean isConnected = false;
	private boolean isMessageLoggingEnabled = true;
	private Logger logger = Logger.getLogger(this.getClass());
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

		logger.setLevel(Level.ERROR);

		// load connection properties from persistent storage
		endpoint = new TcpEndpoint(host, port);

		//create XML message loader template and populate its parameters
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

		logger.info("Client connected to endpoint " + endpoint);

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

	public MethodResult start(String username, String password, String apiKey, String remoteHostName) {

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
			start();
		} catch (Exception e) {
			this.stop();
			return new MethodResult(MessageStrings.ERROR_CONNECT);
		}
		try {
			logon(LOGON_TIMEOUT_SECONDS);
		} catch (Exception e) {
			stop();
			if (e.getMessage().contains(":"))
				return new MethodResult(e.getMessage().split(":")[1]);
			else
				return new MethodResult(e.getMessage());
		}

		return new MethodResult();
	}

	private void clearStatus() {
		callStatus.setLength(0);
	}

	public MethodResult restart() {
		int totalTime = 0;

		for (int i = 1; i <= RESTART_ATTEMPTS_COUNT; i++) {
			stop();
			try {
				if (i > 1) {
					int waitTime = (int) (i * (5 + Math.random() * 2));
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
				return new MethodResult(e.getMessage());
			}
			if (isLoggedOn()) {
				if (totalTime > TIME_WAIT_TOPRINT)
					Console.writeln(MessageStrings.OK);

				return new MethodResult();
			}

		}
		if (!isLoggedOn()) {
			return new MethodResult(MessageStrings.ERROR_CONNECT);
		}
		return new MethodResult();

	}

	private void waitAndDots(int sec, int totalTime) throws InterruptedException {
		if (totalTime > TIME_WAIT_TOPRINT) {
			Console.write(".");
		}
		Thread.sleep(sec);
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

	public MethodResult validateStringRequest(String requestString) throws Exception {

		if (!isPureAscii(requestString))
			return new MethodResult(MessageStrings.NON_ASCII);

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
			return new MethodResult(response.getMsgBody());
		}

		MethodResult result = new MethodResult();
		result.setStringArray("positions", paramList);
		return result;
	}

	public MethodResult callEstimator(String estimatorType, double[] price, int timeStep) throws Exception {
		int[] timeSec = new int[price.length];
		for (int i = 0; i < price.length; i++)
			timeSec[i] = i * timeStep + 1;
		return callEstimator(estimatorType, price, timeSec);
	}

	public MethodResult transmitHistoryPrice(String assetName) throws Exception {

		String requestType = "HISTORY_PRICE";
		String request = assetName;

		Message msg = ServerResponseMessageFactory.createTransmitDataRequest(getTemplateRegistry(), requestType, request,
				getOutboundMsgSequenceNumber(), System.currentTimeMillis());
		Message responseMsg = sendAndAwaitResponse(msg, DATA_TRANSMIT_TIMEOUT_SECONDS_ESTIMATE);
		TransmitDataResponse response = ServerResponseMessageParser.parseTransmitDataResponse(responseMsg);

		if (response.getMsgType().equals("OK")) {

			MethodResult result = new MethodResult();
			result.setMessage(response.getMsgBody());

			return result;

		} else {
			throw new Exception(response.getMsgBody());
		}

	}

	public MethodResult transmitQuantity(String assetName, int[] dataInt, long[] time) throws Exception {

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
			System.arraycopy(dataInt, position, dataTransmit, 0, time.length % MAX_BLOCK_DIMENSION);

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

		MethodResult result = new MethodResult();
		result.setMessage("NON");

		return result;
	}

	public MethodResult transmitUserPrice(String assetName, float[] dataFloat, long[] time) throws Exception {

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

		MethodResult result = new MethodResult();
		result.setMessage("NON");

		return result;

	}

	public MethodResult transmitDataList(String fromTime, String toTime, ArrayList<String> dataList, String windowLength, String priceSamplingInterval, String momentsModel )
			throws Exception {
		timeDataFast.reset();
		timeDataTransmit.reset();

		String requestType = "CHECK_DATA";

		TransmitDataListMessage dataListMessage = new TransmitDataListMessage(dataList, windowLength, fromTime, toTime, priceSamplingInterval, momentsModel);

		Gson gson = new Gson();
		Type mapType = new TypeToken<TransmitDataListMessage>() {
		}.getType();
		String request = gson.toJson(dataListMessage, mapType);


		timeDataFast.start();
		Message msg = ServerResponseMessageFactory.createTransmitDataRequest(getTemplateRegistry(), requestType, request,
				getOutboundMsgSequenceNumber(), System.currentTimeMillis());
		timeDataFast.stop();
		timeDataTransmit.start();
		Message responseMsg = sendAndAwaitResponse(msg, DATA_TRANSMIT_TIMEOUT_SECONDS_ESTIMATE);

		timeDataTransmit.stop();
		timeDataFast.start();
		TransmitDataResponse response = ServerResponseMessageParser.parseTransmitDataResponse(responseMsg);
		timeDataFast.stop();

		if (response.getMsgType().equals("OK")) {

			MethodResult result = new MethodResult();
			result.setMessage(response.getMsgBody());

			return result;

		} else {
			return new MethodResult(response.getMsgBody());
		}
	}

	public MethodResult callEstimator(String estimatorType, double[] price, int[] timeSec) {
		double[] result = null;

		estimatorType = "[" + estimatorType + "]";

		if (progressBarMax == 0) {
			progressBarMax = price.length * groupSize;
			progressBarI = 0;
			progressBar.reset();
		}
		if (progressBarMax != 0 && progressBarI == 0) {
			progressBar.reset();
		}

		int curentBlockDimension = MAX_BLOCK_DIMENSION * BLOCK_DIMENSION_DECREASE_STEP;
		if (!isLoggedOn()) {
			curentBlockDimension *= BLOCK_DIMENSION_DECREASE_STEP;
		}
		while (true) {
			curentBlockDimension /= BLOCK_DIMENSION_DECREASE_STEP;
			if (curentBlockDimension < 2)
				return new MethodResult(MessageStrings.SERVER_TIME_OUT);

			result = null;
			progressBar.printCompletionStatus(progressBarI, progressBarMax);

			if (price.length == 0) {
				progressBarMax = 0;
				return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE);
			}

			if (timeSec.length != price.length && timeSec.length != 0) {
				progressBarMax = 0;
				return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);
			}

			if (timeSec.length == 0) {
				timeSec = new int[price.length];
				for (int i = 0; i < price.length; i++)
					timeSec[i] = i + 1;
			}

			int maxBlockDimension = curentBlockDimension;// / price.length;
			double ttt = price.length;
			ttt = ttt / maxBlockDimension + 0.5;
			int numberBlocks = (int) ttt;
			int pointer = 0;

			for (int i = 0; i < numberBlocks - 1; i++) {
				double[] priceBlock = new double[maxBlockDimension];
				int[] timeSecBlock;
				timeSecBlock = new int[maxBlockDimension];
				PriceDataSet data;
				{
					if (timeSec.length != price.length) {
						progressBarMax = 0;
						return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);
					}
					for (int j = 0, localPointer = pointer; j < maxBlockDimension; j++, localPointer++) {
						priceBlock[j] = price[localPointer];
						timeSecBlock[j] = timeSec[localPointer];
					}

					try {
						data = new PriceDataSet(priceBlock, timeSecBlock);
					} catch (Exception e) {
						return new MethodResult(e.getMessage());
					}
				}

				double[] resultBlock = new double[] {};

				try {
					clearStatus();
					Message msg = ClientRequestMessageFactory.createNonparametricComputeRequest(getTemplateRegistry(),  estimatorType, "false",
							priceBlock, timeSecBlock, getOutboundMsgSequenceNumber(), System.currentTimeMillis());

					Message responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

					NonparametricComputeResponse response = ServerResponseMessageParser.parseNonparametricComputeResponse(responseMsg);

					if (response.getMsgType().equals("OK")) {
						resultBlock = response.getData();
					} else {
						throw new Exception(response.getMsgBody());
					}

				}

				catch (ConnectFailedException e) {

					MethodResult isRestarted = restart();
					if (isRestarted.hasError()) {
						return new MethodResult(isRestarted.getErrorMessage());
					}
					numberBlocks = -1;
					continue;

				}

				catch (Exception e) {

					return new MethodResult(e.getMessage());

				}

				if (result == null) {
					result = new double[price.length];
				}

				{

					for (int j = 0, localPointer = pointer; j < resultBlock.length; j++, localPointer++)
						result[localPointer] = resultBlock[j];

				}

				pointer += maxBlockDimension;

				progressBarI += resultBlock.length;
				progressBar.printCompletionStatus(progressBarI, progressBarMax);

			}

			if (numberBlocks == -1)
				continue;

			// This is last Block !!!!!!!!!!!!!!!!!

			double[] priceBlock = new double[price.length - pointer];
			int[] timeSecBlock;
			timeSecBlock = new int[price.length - pointer];

			PriceDataSet data;

			{

				if (timeSec.length != price.length) {
					progressBarMax = 0;
					return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);

				}

				for (int j = 0, localPointer = pointer; j < price.length - pointer; j++, localPointer++) {
					priceBlock[j] = price[localPointer];
					timeSecBlock[j] = timeSec[localPointer];
				}

				try {
					 data = new PriceDataSet(priceBlock, timeSecBlock);
				} catch (Exception e) {

					return new MethodResult(e.getMessage());

				}
			}

			double[] resultBlock = new double[] {};

			try {

				Message msg = ClientRequestMessageFactory.createNonparametricComputeRequest(getTemplateRegistry(),  estimatorType, "true", priceBlock,
						timeSecBlock, getOutboundMsgSequenceNumber(), System.currentTimeMillis());

				Message responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

				NonparametricComputeResponse response = ServerResponseMessageParser.parseNonparametricComputeResponse(responseMsg);

				if (response.getMsgType().equals("OK")) {

					resultBlock = response.getData();
				}

				else {

					throw new Exception(response.getMsgBody());
				}

			} catch (ConnectFailedException e) {

				MethodResult isRestarted = restart();
				if (isRestarted.hasError()) {
					return new MethodResult(isRestarted.getErrorMessage());
				}
				numberBlocks = -1;
				continue;

			}

			catch (Exception e) {

				return new MethodResult(e.getMessage());

			}

			if (result == null) {
				result = new double[price.length];
			}

			{

				for (int j = 0, localPointer = pointer; j < resultBlock.length; j++, localPointer++)
					result[localPointer] = resultBlock[j];

			}

			progressBarI += resultBlock.length;
			progressBar.printCompletionStatus(progressBarI, progressBarMax);

			if (progressBarI == progressBarMax) {
				createCallGroup(1);
			}

			break;
		}

		ArrayCache resultCache;
		ArrayCache resultTime;
		try {
			resultCache = new ArrayCache(result);

			resultTime = new ArrayCache(timeSec);

		} catch (IOException e) {
			return new MethodResult(e.getMessage());

		}

		MethodResult resultA = new MethodResult();
		resultA.setData("value", resultCache);
		resultA.setData("time", resultTime);

		return resultA;
	}


	public MethodResult estimateEstimator(String metricType) throws Exception {

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

				
				//Gson gson = new Gson();

								
				Message msg = ClientRequestMessageFactory.createNonparametricComputeRequest(getTemplateRegistry(),  metricType, "",
						new double[1], new int[1], getOutboundMsgSequenceNumber(), System.currentTimeMillis());



				responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);
				
				
						

				

				isFirstBlock = false;
			} else {

				
				Message msg = ClientRequestMessageFactory.createNonparametricComputeRequest(getTemplateRegistry(),  metricType, "#NEXT#",
						new double[1], new int[1], getOutboundMsgSequenceNumber(), System.currentTimeMillis());


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

		MethodResult result = new MethodResult();
		result.setData("value", resultValueList);
		result.setData("time", resultTimeList);
		result.setInfo(info);

		return result;
	}

	
	public MethodResult estimateTransactional(String metricType, String indexPosition, ArrayList<String> positionList, String params) throws Exception {

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

				if (indexPosition.length() != 0)
					positionList.add(0, indexPosition);

				Gson gson = new Gson();

				String request = gson.toJson(positionList);


				Message msg = ClientRequestMessageFactory.createTransactionalPortfolioComputeRequest(getTemplateRegistry(), metricType, request,
						params, getOutboundMsgSequenceNumber(), System.currentTimeMillis());

				responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

				isFirstBlock = false;
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

		MethodResult result = new MethodResult();
		result.setData("value", resultValueList);
		result.setData("time", resultTimeList);
		result.setInfo(info);

		return result;
	}

	public MethodResult getAllSymbolsList() throws Exception {

		Message responseMsg;
		Message msg = ClientRequestMessageFactory.createTransactionalPortfolioComputeRequest(getTemplateRegistry(), "ALL_SYMBOLS", "", "",
				getOutboundMsgSequenceNumber(), System.currentTimeMillis());

		responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

		TransmitDataRequest response = ServerResponseMessageParser.parseTransmitDataRequest(responseMsg);
		MethodResult result = new MethodResult();

		if (response.getMsgType().contains("OK")) {

			byte data[] = response.getDataFloatByte();

			data = Snappy.uncompress(data, 0, data.length);
			Map<String, String[]> map = (Map<String, String[]>) SerializationUtils.deserialize(data);
			result.setStringArray("id", map.get("id"));
			result.setStringArray("description", map.get("description"));
			result.setStringArray("exchange", map.get("exchange"));

		} else {

			throw new Exception(response.getMsgBody());
		}

		return result;
	}

	public MethodResult getComputeTimeLeft() {

		for (int i = 0; i < 3; i++) {
			Message responseMsg;

			try {
				Message msg = ClientRequestMessageFactory.createTransactionalPortfolioComputeRequest(getTemplateRegistry(), "TIME_LEFT", "", "",
						getOutboundMsgSequenceNumber(), System.currentTimeMillis());

				responseMsg = sendAndAwaitResponse(msg, USER_LAYER_TIMEOUT_SECONDS_ESTIMATE);

				TransmitDataRequest response = ServerResponseMessageParser.parseTransmitDataRequest(responseMsg);
				MethodResult result = new MethodResult();

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

				MethodResult result = processException(e);
				if (result == null)
					continue;
				return result;
			}

		}
		return new MethodResult(MessageStrings.FAILED_SERVER_TIME_OUT);

	}

	private MethodResult processException(Exception e) {

		if (e instanceof ConnectFailedException) {

			MethodResult isRestarted = restart();
			if (isRestarted.hasError()) {
				resetProgressBar();
				return new MethodResult(isRestarted.getErrorMessage());
			}

			return null;
		}

		if (e.getMessage() == null || e.getMessage().contains("No data in cache") || e.getMessage().contains("null")) {

			MethodResult isRestarted = restart();
			if (isRestarted.hasError()) {
				resetProgressBar();
				return new MethodResult(isRestarted.getErrorMessage());
			}

			return null;

		}

		if (e.getMessage() == null) {
			Console.writeStackTrace(e);
			return new MethodResult(MessageStrings.ERROR_101);
		}

		resetProgressBar();
		return new MethodResult(e.getMessage());

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

	private void sendHeartbeat(String testReqId) {
		Message logoutMsg = ClientRequestMessageFactory.createHeartbeat(templateRegistry, nextOutboundMsgSequenceNumber(), testReqId);
		out.writeMessage(logoutMsg);
	}
	
	private TemplateRegistry getTemplateRegistry() throws Exception {
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

	private class InboundMessageWorker implements Runnable {
		public void run() {
			try {
				logger.debug("Started inbound message logger thread");
				while (true) {

					Message msg = in.readMessage();
					if (msg == null) {
						logger.debug("End of input stream. Exiting from inbound message worker thread");
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
					case GET_REMAINING_TRAFFIC_RESPONSE:
						clientMessageQueue.offer(new ClientMessage(msg));
						break;
					case NON_PARAM_METRIC_RESPONSE:
						clientMessageQueue.offer(new ClientMessage(msg));
						break;
					case PORTFOLIO_ESTIMATION_RESPONSE:
						clientMessageQueue.offer(new ClientMessage(msg));
						break;
					default:
						clientMessageQueue.offer(new ClientMessage(msg));
						break;
					}

					if (isMessageLoggingEnabled)
						logger.info("Recieved message: " + msg.toString());
				}
			} catch (FastException e) {
				isLoggedOn = false;
				isConnected = false;
				logger.info("Connection was terminated");
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

		
		
	


}
