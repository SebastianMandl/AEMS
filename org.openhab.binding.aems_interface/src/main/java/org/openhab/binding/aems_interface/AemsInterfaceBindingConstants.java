/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.aems_interface;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link AemsInterfaceBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author smandl - Initial contribution
 */
@NonNullByDefault
public class AemsInterfaceBindingConstants {

    private static final String BINDING_ID = "aems_interface"; // must coincide with package name

    // List of all Thing Type UIDs
    public static final ThingTypeUID AEMS_METER_THING = new ThingTypeUID(BINDING_ID, "aems-meter-interface");

    // List of all Channel ids
    public static final String TEMPERATURE_CHANNEL = "aems-temperature-info-channel";
    public static final String HUMIDITY_CHANNEL = "aems-humidity-info-channel";
    public static final String CURRENT_CONSUMPTION_CHANNEL = "aems-current-consumption-channel";

}
