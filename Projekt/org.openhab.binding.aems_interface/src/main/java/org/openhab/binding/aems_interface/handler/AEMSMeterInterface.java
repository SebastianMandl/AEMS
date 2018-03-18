/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.aems_interface.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.aems_interface.AemsInterfaceBindingConstants;
import org.openhab.binding.aems_interface.internal.AEMSInterfaceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.htlgkr.aems.util.crypto.Decrypter;
import at.htlgkr.aems.util.crypto.Encrypter;
import at.htlgkr.aems.util.crypto.KeyUtils;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;

/**
 * The {@link AEMSMeterInterface} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author smandl - Initial contribution
 */
@NonNullByDefault
public class AEMSMeterInterface extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(AEMSMeterInterface.class);

    private static final String SERVER_DOMAIN = "http://aemsserver.ddns.net:8084/AEMSWebService/RestInf";

    private static String queryWeather = "{ weather_data (meter:?, start:?) { id, temperature, humidity } }";
    private static String queryMeter = "{ meter_data (meter:?, start:?) { id, measured_value, unit } }";

    // {humidity=30.0, temperature=15.0}
    private static final Pattern JSON_PATTERN = Pattern.compile(
            "(?:.*\"temperature\"\\:(?<temperature>\\d{1,15}(?:\\.\\d{1,15})))?(?:.*\"humidity\"\\:(?<humidity>\\d{1,15}(?:\\.\\d{1,15})))?(?:.*\"unit\"\\:\"(?<unit>\\w+)\")?(?:.*\"measured_value\"\\:(?<measuredValue>\\d{1,15}(?:\\.\\d{1,15})?))?");
    // "(?:\"temperature\":(?<temperature>\\d{1,15}(?:\\.\\d{1,15})?),\\s\"humidity\":(?<humidity>\\d{1,15}(?:\\.\\d{1,15})?))?.*(?:\"measured_value\":(?<measuredvalue>\\d{1,15}(?:\\.\\d{1,15})?),\\s(?<unit>\"unit\":\\w))?");

    @Nullable
    private AEMSInterfaceConfiguration config;

    @Nullable
    private BigDecimal key;

    @Nullable
    private Thread thread;

    private boolean shouldUpdate = true;

    public AEMSMeterInterface(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, @Nullable Command command) {
        if (command instanceof RefreshType) {
            updateData();
        }
    }

    @SuppressWarnings("null")
    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.

        config = getConfigAs(AEMSInterfaceConfiguration.class);

        exchangeAESKey(config.username, config.password);

        if (!getThing().getLabel().matches(".*\\(.*")) {
            getThing().setLabel(getThing().getLabel() + " (" + config.meterId + ")");
        }

        (thread = new Thread(() -> {
            while (shouldUpdate) {
                updateData();
                try {
                    Thread.sleep(5000); // update every 5 minutes
                } catch (InterruptedException e) {
                    logger.info("update thread for meter \"{}\" has stopped executing!", config.meterId);
                    return;
                }
            }

        })).start();

        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    private int userId;

    private void exchangeAESKey(String username, String password) {
        try {
            logger.info("key exchange initiated");
            DiffieHellmanProcedure.prepareKeyAcquisition("aemsserver.ddns.net");
            DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName("aemsserver.ddns.net"), 9950));
            key = KeyUtils.salt(new BigDecimal(new String(DiffieHellmanProcedure.confirmKey())), username, password);

            logger.info("key exchange concluded");
        } catch (Exception e) {
            logger.info("----------------------------------------------------------------");
            logger.info(e.getMessage() + " : " + e.toString());
            logger.info("------------------------------------------------------------------");
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    private String replaceQuestionMarks(String query, String[] fillIns) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (char c : query.toCharArray()) {
            if (c == '?') {
                builder.append("\"").append(fillIns[index]).append("\"");
                index++;
                continue;
            }
            builder.append(c);
        }
        return builder.toString();
    }

    private @Nullable String query(String query) {
        String returnValue = null;
        try {
            query = Base64.getUrlEncoder().encodeToString(Encrypter.requestEncryption(key, query.getBytes()));

            HttpURLConnection con = (HttpURLConnection) new URL(
                    SERVER_DOMAIN + "?encryption=AES&user=" + config.username + "&action=QUERY&data=" + query)
                            .openConnection();
            con.setDoInput(true);
            con.setRequestMethod("POST");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            returnValue = reader.readLine();

            returnValue = new String(
                    Decrypter.requestDecryption(key, Base64.getUrlDecoder().decode(returnValue.getBytes())));

        } catch (Exception e) {
        }
        return returnValue;
    }

    @SuppressWarnings("null")
    public void updateData() {
        if (key == null) {
            return;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String formattedDateStart = format.format(date);
            formattedDateStart = formattedDateStart.substring(0, formattedDateStart.length() - 3) + ":00";
            String formattedDateEnd = format.format(date);
            formattedDateEnd = formattedDateEnd.substring(0, formattedDateEnd.length() - 3) + ":59";

            String[] fillIns = new String[] { config.meterId, formattedDateStart };

            String jsonLineWeather = query(replaceQuestionMarks(AEMSMeterInterface.queryWeather, fillIns));

            String jsonLineMeter = query(replaceQuestionMarks(AEMSMeterInterface.queryMeter, fillIns));

            String humidity = "0";
            String temperature = "0";
            String measured_value = "0";
            String unit = "-";

            boolean foundWeather = false;
            boolean foundMeter = false;

            Matcher matcher = JSON_PATTERN.matcher(jsonLineWeather);
            logger.info("-------------- json: {}", jsonLineWeather);
            while (matcher.find()) {
                String tmp;
                int counter = 0;
                try {
                    tmp = matcher.group("humidity");
                    if (tmp == null) {
                        counter++;
                    }
                    humidity = tmp == null ? humidity : tmp;
                } catch (Exception e) {
                }
                try {
                    tmp = matcher.group("temperature");
                    if (tmp == null) {
                        counter++;
                    }
                    temperature = tmp == null ? temperature : tmp;
                } catch (Exception e) {
                }

                if (counter == 2) {
                    break;
                }

                foundWeather = true;
            }

            matcher = JSON_PATTERN.matcher(jsonLineMeter);
            logger.info("-------------- json: {}", jsonLineMeter);
            while (matcher.find()) {
                String tmp;
                int counter = 0;

                try {
                    tmp = matcher.group("measuredValue");
                    if (tmp == null) {
                        counter++;
                    }
                    measured_value = tmp == null ? measured_value : tmp;
                } catch (Exception e) {
                }
                try {
                    tmp = matcher.group("unit");
                    if (tmp == null) {
                        counter++;
                    }
                    unit = tmp == null ? unit : tmp;
                } catch (Exception e) {
                }

                if (counter == 2) {
                    break;
                }

                foundMeter = true;
            }

            NumberFormat formatNumber = NumberFormat.getInstance();
            formatNumber.setMinimumFractionDigits(2);
            formatNumber.setMaximumFractionDigits(2);
            formatNumber.setMinimumIntegerDigits(1);

            if (foundWeather) {
                updateState(getThing().getChannel(AemsInterfaceBindingConstants.HUMIDITY_CHANNEL).getUID(),
                        new StringType(formatNumber.format(Float.parseFloat(humidity)) + " %"));
                updateState(getThing().getChannel(AemsInterfaceBindingConstants.TEMPERATURE_CHANNEL).getUID(),
                        new StringType(formatNumber.format(Float.parseFloat(temperature)) + " °"));
            }
            if (foundMeter) {
                updateState(getThing().getChannel(AemsInterfaceBindingConstants.CURRENT_CONSUMPTION_CHANNEL).getUID(),
                        new StringType(formatNumber.format(Float.parseFloat(measured_value)) + " " + unit));
            }

            if (foundMeter || foundWeather) {
                updateThing(getThing());
            }

            logger.info("humidity: {}, temperature: {}, measured_value: {}, unit: {}", humidity, temperature,
                    measured_value, unit);
            logger.info("AEMS Interface for meter {} has been updated!", config.meterId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
