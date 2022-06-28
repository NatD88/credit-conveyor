CREATE TYPE GENDER_TYPE AS ENUM('MALE', 'FEMALE', 'NON_BINARY');
CREATE TYPE MARITAL_STATUS_TYPE AS ENUM('MARRIED', 'DIVORCED', 'SINGLE', 'WIDOW_WIDOWER');
CREATE TYPE EMPLOYMENT_STATUS_TYPE AS ENUM('UNEMPLOYED', 'SELF_EMPLOYED', 'EMPLOYED', 'BUSINESS_OWNER');
CREATE TYPE EMPLOYMENT_POSITION_TYPE AS ENUM('WORKER', 'MID_MANAGER', 'TOP_MANAGER', 'OWNER');

CREATE TABLE IF NOT EXISTS PASSPORTS (
                                         PASSPORT_ID INTEGER PRIMARY KEY,
                                         SERIES VARCHAR(4),
                                         NUMBER VARCHAR(6),
                                         ISSUE_DATE DATE,
                                         ISSUE_BRANCH VARCHAR(200)
);
CREATE TABLE IF NOT EXISTS EMPLOYMENTS (
                                           EMPLOYMENT_ID INTEGER PRIMARY KEY,
                                           EMPLOYMENT_STATUS EMPLOYMENT_STATUS_TYPE,
                                           EMPLOYER VARCHAR(200),
                                           SALARY DECIMAL,
                                           EMPLOYMENT_POSITION EMPLOYMENT_POSITION_TYPE,
                                           WORK_EXPERIENCE_TOTAL INTEGER,
                                           WORK_EXPERIENCE_CURRENT INTEGER
);

CREATE TABLE IF NOT EXISTS CLIENTS (
                                       CLIENT_ID INTEGER PRIMARY KEY,
                                       LAST_NAME VARCHAR(150) NOT NULL,
                                       FIRST_NAME VARCHAR(150) NOT NULL,
                                       MIDDLE_NAME VARCHAR(150),
                                       BIRTH_DATE DATE,
                                       EMAIL VARCHAR(150),
                                       GENDER GENDER_TYPE,
                                       MARITAL_STATUS MARITAL_STATUS_TYPE,
                                       DEPENDENT_AMOUNT INTEGER,
                                       PASSPORT_ID INTEGER,
                                       EMPLOYMENT_ID INTEGER,
                                       ACCOUNT VARCHAR(20),
                                       FOREIGN KEY(PASSPORT_ID) REFERENCES PASSPORTS(PASSPORT_ID),
                                       FOREIGN KEY (EMPLOYMENT_ID) REFERENCES EMPLOYMENTS(EMPLOYMENT_ID));

CREATE SEQUENCE  IF NOT EXISTS hibernate_sequence START 1;

CREATE TYPE   CREDIT_STATUS_TYPE AS ENUM('CALCULATED', 'ISSUED');

CREATE TABLE IF NOT EXISTS CREDITS (
                                       CREDIT_ID INTEGER PRIMARY KEY,
                                       AMOUNT DECIMAL,
                                       TERM INTEGER,
                                       MONTHLY_PAYMENT DECIMAL,
                                       RATE DECIMAL,
                                       PSK DECIMAL,
                                       PAYMENT_SCHEDULE JSONB,
                                       IS_INSURANCE_ENABLED BOOLEAN,
                                       IS_SALARY_CLIENT BOOLEAN,
                                       CREDIT_STATUS CREDIT_STATUS_TYPE);

CREATE TYPE APPLICATION_STATUS_TYPE AS ENUM('PREAPPROVAL', 'APPROVED', 'CC_DENIED',
    'CC_APPROVED', 'PREPARE_DOCUMENTS', 'DOCUMENT_SIGNED', 'CREDIT_ISSUED');

CREATE  TABLE IF NOT EXISTS APPLICATIONS (
                                             APPLICATION_ID INTEGER PRIMARY KEY,
                                             CLIENT_ID INTEGER,
                                             CREDIT_ID INTEGER,
                                             APPLICATION_STATUS APPLICATION_STATUS_TYPE,
                                             CREATION_DATE DATE,
                                             APPLIED_OFFER JSONB,
                                             SIGN_DATE DATE,
                                             SES_CODE INTEGER,
                                             STATUS_HISTORY JSONB,
                                             FOREIGN KEY (CLIENT_ID)REFERENCES CLIENTS(CLIENT_ID),
                                             FOREIGN KEY (CREDIT_ID) REFERENCES CREDITS(CREDIT_ID));