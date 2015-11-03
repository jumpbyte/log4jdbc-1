CREATE SEQUENCE SEQ_PERSONNE START WITH 1;

CREATE TABLE PERSONNE(
	ID INT DEFAULT SEQ_PERSONNE.NEXTVAL PRIMARY KEY,
	PRENOM VARCHAR NOT NULL,
	NOM VARCHAR NOT NULL,
	DATE_NAISSANCE DATE NOT NULL
);