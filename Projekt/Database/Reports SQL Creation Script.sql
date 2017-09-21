DROP TABLE IF EXISTS aems."Reports";
CREATE TABLE aems."Reports" (

    id numeric(10,0),
    "name" varchar(100),
    annotation varchar(2000),
    from_date date,
    to_date date,
    period numeric(10, 0),
    "user" numeric(10, 0),

    CONSTRAINT pk_reports PRIMARY KEY (id),
    CONSTRAINT fk_reports_period FOREIGN KEY (period)
        REFERENCES aems."Periods" (id),
    CONSTRAINT fk_reports_user FOREIGN KEY ("user")
        REFERENCES aems."Users" (id)

);

ALTER TABLE aems."Reports" OWNER TO aems;

DROP TABLE IF EXISTS aems."ReportStatistics";
CREATE TABLE aems."ReportStatistics" (

    report numeric(10, 0),
    statistic numeric(10, 0),

    CONSTRAINT pk_reportstatistics PRIMARY KEY (report, statistic),
    CONSTRAINT fk_reportstatistics_reports FOREIGN KEY (report)
        REFERENCES aems."Reports" (id),
    CONSTRAINT fk_reportstatistics_statistics FOREIGN KEY (statistic)
        REFERENCES aems."Statistics" (id)

);

ALTER TABLE aems."ReportStatistics" OWNER TO aems;