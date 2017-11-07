
/** ---------------------------------------
*	STATISTICS CREATION SCRIPT VERSION 2.0 |
*   ---------------------------------------
*   Changes: Model was simplified, everything regarding configurations and
*   warnings has been relocated to the WARNINGS model.
*   Updated period system, one can now specify a start and end value to a period. This allows,
*   for example, to recieve statistics only regarding weekdays or weekends-
**/


/**
* Creates the Statistics Table.
* STATISTIC_ID (PK), USER_ID (FK [Users]), STATISTIC_NAME, OPTIONS_ID (FK [StatisticOptions])
**/
DROP TABLE IF EXISTS aems."Statistics" CASCADE;
CREATE TABLE aems."Statistics"
(
  statistic_id numeric(10,0) NOT NULL,
  user_id numeric(10,0) NOT NULL,
  statistic_name character varying(64),
  annotation character varying(256),

  CONSTRAINT pk_transfer_infos PRIMARY KEY (statistic_id),
  CONSTRAINT fk_statistics_user FOREIGN KEY (user_id)
    REFERENCES aems."Users" (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
)

/**
* Creates the StatisticTimes table.
* TIME_ID (PK), OPTIONS_ID (FK [StatisticOptions]), PERIOD_ID (FK [StatisticPeriods])
* FROM_DATE, TO_DATE, PERIOD_VALUE_START, PERIOD_VALUE_END
*
* The meaning of the period_value fields depends on the value of the period_id
**/
DROP TABLE IF EXISTS aems."StatisticTimes" CASCADE;
CREATE TABLE aems."StatisticTimes"
(
  time_id numeric(10, 0) NOT NULL,
  statistic_id numeric(10, 0) NOT NULL,
  period_id NUMERIC(2, 0) NOT NULL,
  
  period_value_start NUMERIC(10, 0),
  period_value_end NUMERIC(10, 0),
  
  from_date DATE,
  to_date DATE,

  CONSTRAINT pk_statistic_times PRIMARY KEY (time_id),
  CONSTRAINT fk_times_statisticid FOREIGN KEY (statistic_id)
    REFERENCES aems."Statistics" (statistic_id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
  CONSTRAINT fk_times_periodid FOREIGN KEY (period_id)
    REFERENCES aems."Periods" (period_id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
)


/**
* Creates the StatisticMeters Table. It's a "Many-to-Many" resolution table.
* (A Statistic contains several meters, but a meter can also be present in many statistics)
* METER_ID (FK [Meters]), STATISTIC_ID (FK [Statistics])
**/
DROP TABLE IF EXISTS aems."StatisticMeters" CASCADE;
CREATE TABLE aems."StatisticMeters"
(
  meter_id numeric(10,0) NOT NULL,
  statistic_id numeric(10, 0) NOT NULL,
  CONSTRAINT fk_statisticmeters_meterid FOREIGN KEY(id)
    REFERENCES aems."Meters" (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_statisticmeters_statisticid FOREIGN KEY(statistic_id)
    REFERENCES aems."Statistics" (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
)