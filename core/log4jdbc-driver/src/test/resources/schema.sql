CREATE SEQUENCE SEQ_PERSONNE START WITH 1;

CREATE TABLE PERSONNE(
	ID INT DEFAULT SEQ_PERSONNE.NEXTVAL PRIMARY KEY,
	PRENOM VARCHAR NOT NULL,
	NOM VARCHAR NOT NULL,
	DATE_NAISSANCE DATE NOT NULL
);

CREATE SEQUENCE SEQ_ETABLISSEMENT START WITH 1;

CREATE TABLE ETABLISSEMENT(
	ID INT DEFAULT SEQ_ETABLISSEMENT.NEXTVAL PRIMARY KEY,
	NOM VARCHAR NOT NULL
);

CREATE TABLE PERSONNE_ETABLISSEMENT(
	ID_PERSONNE INT NOT NULL,
	ID_ETABLISSEMENT INT NOT NULL,
);