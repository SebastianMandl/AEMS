DROP TABLE IF EXISTS aems."Notifications" CASCADE;
CREATE TABLE aems."Notifications"
(
  id NUMERIC(10,0) NOT NULL,
  "user" NUMERIC(10, 0) NOT NULL,
  name character varying(100) NOT NULL,
  
  type INTEGER, -- Warning or Info or Error Message
  max_deviation numeric(3,0) NOT NULL, -- 0 to 100 Percent
  -- Base Value to calculate deviation ? Or will that value be dynamically created by inspecting past values
  
  CONSTRAINT pk_notifications PRIMARY KEY(id),
  CONSTRAINT fk_notifications_users FOREIGN KEY ("user")
    REFERENCES aems."Users" (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE aems."Notifications" OWNER TO aems;

DROP TABLE IF EXISTS aems."NotificationMeters" CASCADE;
CREATE TABLE aems."NotificationMeters"
(
  meter character varying(150) NOT NULL,
  notification NUMERIC(10,0) NOT NULL,

    CONSTRAINT fk_notificationmeters_meters FOREIGN KEY (meter)
    REFERENCES aems."Meters" (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_otificationmeters_notifications FOREIGN KEY (notification)
    REFERENCES aems."Notifications" (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE aems."NotificationMeters" OWNER TO aems;

DROP TABLE IF EXISTS aems."NotificationExceptions" CASCADE;
CREATE TABLE aems."NotificationExceptions"
(
  id NUMERIC(10,0) NOT NULL,
  notification NUMERIC(10, 0) NOT NULL,
  
  max_deviation NUMERIC(10, 0) NOT NULL,
  
  from_date DATE,
  to_date DATE,
  
  -- From and To Date, OR:
  period NUMERIC(2, 0),
  period_value_from NUMERIC(2, 0),
  period_value_to NUMERIC(2, 0),
  
  CONSTRAINT pk_notification_exceptions PRIMARY KEY(id),
  CONSTRAINT fk_notification_exceptions_notifications FOREIGN KEY (notification)
    REFERENCES aems."Notifications" (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_notification_exceptions_periods FOREIGN KEY (period)
    REFERENCES aems."Periods" (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE aems."NotificationExceptions" OWNER TO aems;