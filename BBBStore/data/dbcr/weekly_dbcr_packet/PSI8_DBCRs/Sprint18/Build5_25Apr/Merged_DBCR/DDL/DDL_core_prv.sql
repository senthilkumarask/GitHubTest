-- Build Version : PSI8 Release Sprint 18 build 5
SET ECHO ON;
SET DEFINE OFF;

 CREATE TABLE BBB_CORE_PRV.BBB_USER_CHALLENGE_QUESTION (
	PROFILE_ID		varchar2(40)	NOT NULL,
	CHALLENGE_QUESTION1 	varchar2(254)	NULL,
	CHALLENGE_QUESTION2 	varchar2(254)	NULL,
	CHALLENGE_ANSWER1 	varchar2(254)	NULL,
	CHALLENGE_ANSWER2 	varchar2(254)	NULL,
	CONSTRAINT PK_BBB_USER_CHALLENGE_QUESTION PRIMARY KEY(PROFILE_ID)
);
COMMIT;



 