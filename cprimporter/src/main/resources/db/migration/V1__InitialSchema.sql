USE sdm_warehouse;

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
