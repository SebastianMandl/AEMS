/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.aems_interface.internal;

import static org.openhab.binding.aems_interface.AemsInterfaceBindingConstants.AEMS_METER_THING;

import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.aems_interface.handler.AEMSMeterInterface;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link AemsInterfaceHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author smandl - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.aems_interface")
@NonNullByDefault
public class AemsInterfaceHandlerFactory extends BaseThingHandlerFactory {

    @SuppressWarnings("null")
    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(AEMS_METER_THING);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(@Nullable Thing thing) {
        @SuppressWarnings("null")
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(AEMS_METER_THING)) {
            return new AEMSMeterInterface(thing);
        }

        return null;
    }
}
