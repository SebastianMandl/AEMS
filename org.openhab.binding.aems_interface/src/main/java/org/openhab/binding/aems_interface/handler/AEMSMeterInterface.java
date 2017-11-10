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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

/**
 * The {@link AEMSMeterInterface} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author smandl - Initial contribution
 */
@NonNullByDefault
public class AEMSMeterInterface extends BaseThingHandler {

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(AEMSMeterInterface.class);

    private static final String SERVER_DOMAIN = "http://localhost:8084/AEMSWebService/RestInf";

    @SuppressWarnings("null")
    // {humidity=30.0, temperature=15.0}
    private static final Pattern JSON_PATTERN = Pattern.compile(
            "humidity=(?<humidity>\\d{1,5}(?:\\.\\d{1,5})?),\\stemperature=(?<temperature>\\d{1,5}(?:\\.\\d{1,5})?)");
    @Nullable
    private AEMSInterfaceConfiguration config;

    public AEMSMeterInterface(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, @Nullable Command command) {
        if (command instanceof RefreshType) {
            updateData();
        }
    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.

        config = getConfigAs(AEMSInterfaceConfiguration.class);
        getThing().setLabel(getThing().getLabel() + " (" + config.meterId + ")");
        updateData();
        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    @SuppressWarnings("null")
    public void updateData() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(SERVER_DOMAIN).openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            StringBuilder builder = new StringBuilder("{\n");
            builder.append("weather_data(meter:\"").append(config.meterId).append("\") {\n");
            builder.append("\thumidity,\n");
            builder.append("\ttemperature\n");
            builder.append("}\n}");
            System.out.println(builder.toString());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            writer.write("operation=POST&query=" + builder.toString());
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String jsonLine = reader.readLine();

            String humidity = "not set";
            String temperature = "not set";

            Matcher matcher = JSON_PATTERN.matcher(jsonLine);
            while (matcher.find()) {
                humidity = matcher.group("humidity");
                temperature = matcher.group("temperature");
            }

            updateState(getThing().getChannel(AemsInterfaceBindingConstants.HUMIDITY_CHANNEL).getUID(),
                    new StringType(humidity + " %"));
            updateState(getThing().getChannel(AemsInterfaceBindingConstants.TEMPERATURE_CHANNEL).getUID(),
                    new StringType(temperature + " °"));

            logger.info("AEMS Interface for meter {} has been updated!", config.meterId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
