CREATE TABLE Person (
	PersonPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	CPR CHAR(10) NOT NULL,

	Koen CHAR(1) NOT NULL,
	Fornavn VARCHAR(50),
	Mellemnavn VARCHAR(40),
	Efternavn VARCHAR(40),
	CoNavn VARCHAR(34),
	Lokalitet VARCHAR(34),
	Vejnavn VARCHAR(30),
	Bygningsnummer VARCHAR(10),
	Husnummer VARCHAR(4),
	Etage VARCHAR(2),
	SideDoerNummer VARCHAR(4),
	Bynavn VARCHAR(34),
	Postnummer INT(4),
	PostDistrikt VARCHAR(20),
	Status CHAR(2),
	NavneBeskyttelseStartDato DATETIME,
	NavneBeskyttelseSletteDato DATETIME,
	GaeldendeCPR CHAR(10),
	Foedselsdato DATE NOT NULL,
	Stilling VARCHAR(50),
	VejKode INT(4),
	KommuneKode INT(4),

	# Additions for CPR Service

	NavnTilAdressering VARCHAR(34),
	VejnavnTilAdressering VARCHAR(20),
	FoedselsdatoMarkering CHAR,
	StatusDato DATETIME,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (PersonPID, ModifiedDate),
	INDEX (CPR, ValidTo, ValidFrom, Fornavn, Mellemnavn, Efternavn, Foedselsdato)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE PersonIkraft (
	PersonIkraftPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY	,

	IkraftDato DATETIME NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE UmyndiggoerelseVaergeRelation (
	UmyndiggoerelseVaergeRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Id VARCHAR(21) NOT NULL,
	CPR VARCHAR(10) NOT NULL,
	TypeKode VARCHAR(4) NOT NULL,
	TypeTekst VARCHAR(50) NOT NULL,
	RelationCpr VARCHAR(10),
	RelationCprStartDato DATETIME,
	VaergesNavn VARCHAR(50),
	VaergesNavnStartDato DATETIME,
	RelationsTekst1 VARCHAR(50),
	RelationsTekst2 VARCHAR(50),
	RelationsTekst3 VARCHAR(50),
	RelationsTekst4 VARCHAR(50),
	RelationsTekst5 VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (UmyndiggoerelseVaergeRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE ForaeldreMyndighedRelation (
	ForaeldreMyndighedRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Id VARCHAR(21) NOT NULL,

	CPR VARCHAR(10) NOT NULL,
	TypeKode VARCHAR(4) NOT NULL,
	TypeTekst VARCHAR(50) NOT NULL,
	RelationCpr VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (ForaeldreMyndighedRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE BarnRelation (
	BarnRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Id CHAR(21) NOT NULL,

	CPR CHAR(10) NOT NULL,
	BarnCPR CHAR(10) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (BarnRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE ChangesToCPR (
	CPR CHAR(10) PRIMARY KEY,
	ModifiedDate TIMESTAMP NOT NULL,
	INDEX (ModifiedDate)
) ENGINE=InnoDB COLLATE=utf8_bin;
