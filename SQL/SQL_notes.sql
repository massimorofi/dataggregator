TRUNCATE TABLE DATA;

INSERT INTO DATA (DATEREP,DAY,MONTH,YEAR,CASES,DEATHS,COUNTRIESANDTERRITORIES,GEOID,COUNTRYTERRITORYCODE,POPDATA2018,CONTINENTEXP) 
SELECT parsedatetime("DATEREP",'d/M/y'),DAY,MONTH,YEAR,CASES,DEATHS,COUNTRIESANDTERRITORIES,GEOID,COUNTRYTERRITORYCODE,POPDATA2018,CONTINENTEXP 
FROM CSVREAD('c:\Users\rofi\Downloads\world.csv',null,'fieldSeparator=;');

select COUNTRIESANDTERRITORIES,SUM(cases) as CASES, SUM(DEATHS) as DEATHS, max(DATEREP) as DATEREP,POPDATA2018 from data group by COUNTRIESANDTERRITORIES;

select * from data where DATEREP= select MAX(DATEREP) from DATA;

CREATE TABLE "PUBLIC"."DATA"
(
   DATEREP date NOT NULL,
   DAY integer NOT NULL,
   MONTH integer NOT NULL,
   YEAR integer NOT NULL,
   CASES integer NOT NULL,
   DEATHS integer NOT NULL,
   COUNTRIESANDTERRITORIES varchar(42) NOT NULL,
   GEOID varchar(8) NOT NULL,
   COUNTRYTERRITORYCODE varchar(3),
   POPDATA2018 integer,
   CONTINENTEXP varchar(7) NOT NULL
);

-- COVID WORLD

CREATE TABLE covid(
   Ord				INTEGER 
  ,Country          VARCHAR(30)
  ,Total_Cases       INTEGER 
  ,New_Cases         INTEGER 
  ,Total_Deaths      INTEGER 
  ,New_Deaths        INTEGER 
  ,Total_Recovered   INTEGER 
  ,Active_Cases      INTEGER 
  ,Serious_Critical  INTEGER 
  ,Tot_Cases_1M_pop  VARCHAR(30) 
  ,Deaths_1M_pop     VARCHAR(30) 
  ,Total_Tests       INTEGER 
  ,Tests_1M_pop      INTEGER 
  ,Population        INTEGER
  ,Continent         VARCHAR(30) 
);

INSERT INTO "PUBLIC"."COVID" (ORD,COUNTRY,TOTAL_CASES,NEW_CASES,TOTAL_DEATHS,NEW_DEATHS,TOTAL_RECOVERED,ACTIVE_CASES,SERIOUS_CRITICAL,TOT_CASES_1M_POP,DEATHS_1M_POP,TOTAL_TESTS,TESTS_1M_POP,POPULATION,CONTINENT) 
SELECT ORD,COUNTRY,TOTAL_CASES,NEW_CASES,TOTAL_DEATHS,NEW_DEATHS,TOTAL_RECOVERED,ACTIVE_CASES,SERIOUS_CRITICAL,TOT_CASES_1M_POP,DEATHS_1M_POP,TOTAL_TESTS,TESTS_1M_POP,POPULATION,CONTINENT 
FROM CSVREAD('C:\Users\rofi\DEV\projects\BACKEND\dataggregator\tmp\covid_world_today.csv','ORD;COUNTRY_OTHER;TOTAL_CASES;NEW_CASES;TOTAL_DEATHS;NEW_DEATHS;TOTAL_RECOVERED;ACTIVE_CASES;SERIOUS_CRITICAL;TOT_CASES_1M_POP;DEATHS_1M_POP;TOTAL_TESTS;TESTS_1M_POP;POPULATION;CONTINENT','fieldSeparator=;');

SELECT COUNTRY_OTHER,TESTS_1M_POP 
FROM COVID 
WHERE COUNTRY_OTHER IN ('Italy','Germany','Spain','France', 'UK','USA','Switzerland','Austria','Japan','S. Korea') 
ORDER BY TESTS_1M_POP DESC;