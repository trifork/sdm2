CREATE TABLE OplysningerOmDosisdispensering (
	OplysningerOmDosisdispenseringPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Varenummer BIGINT(12) NOT NULL,

	DrugID BIGINT(12),
	LaegemidletsSubstitutionsgruppe VARCHAR(4),
	MindsteAIPPrEnhed BIGINT(12),
	MindsteRegisterprisEnh BIGINT(12),
	TSPPrEnhed BIGINT(12),
	KodeForBilligsteDrugid VARCHAR(1),
	BilligsteDrugid BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (OplysningerOmDosisdispenseringPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Udleveringsbestemmelser (
	UdleveringsbestemmelserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Kode VARCHAR(5) NOT NULL,

	Udleveringsgruppe VARCHAR(1),
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (UdleveringsbestemmelserPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Pakningsstoerrelsesenhed (
	PakningsstoerrelsesenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	PakningsstoerrelsesenhedKode VARCHAR(10) NOT NULL,
	PakningsstoerrelsesenhedTekst VARCHAR(50) NOT NULL,
	PakningsstoerrelsesenhedKortTekst VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (PakningsstoerrelsesenhedPID, ModifiedDate),
	INDEX (PakningsstoerrelsesenhedKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE TakstVersion (
	TakstVersionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	TakstUge CHAR(7) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (TakstVersionPID, ModifiedDate),
	INDEX (TakstUge, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Beregningsregler (
	BeregningsreglerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Kode CHAR(1) NOT NULL,
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (BeregningsreglerPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Priser (
	PriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Varenummer BIGINT(12) NOT NULL,

	apoteketsIndkoebspris BIGINT(12),
	Registerpris BIGINT(12),
	ekspeditionensSamledePris BIGINT(12),
	tilskudspris BIGINT(12),
	LeveranceprisTilHospitaler BIGINT(12),
	IkkeTilskudsberettigetDel BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (PriserPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE TilskudsprisgrupperPakningsniveau (
	TilskudsprisgrupperPakningsniveauPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Varenummer BIGINT(12) NOT NULL,

	TilskudsprisGruppe BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (TilskudsprisgrupperPakningsniveauPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Substitution (
	SubstitutionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	ReceptensVarenummer BIGINT(12) NOT NULL,
	Substitutionsgruppenummer BIGINT(12),
	NumeriskPakningsstoerrelse BIGINT(12),
	ProdAlfabetiskeSekvensplads VARCHAR(9),
	SubstitutionskodeForPakning VARCHAR(1),
	BilligsteVarenummer BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (SubstitutionPID, ModifiedDate),
	INDEX (ReceptensVarenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE SubstitutionAfLaegemidlerUdenFastPris (
	SubstitutionAfLaegemidlerUdenFastPrisPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Varenummer BIGINT(12) NOT NULL,
	Substitutionsgruppenummer BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (SubstitutionAfLaegemidlerUdenFastPrisPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


CREATE TABLE Opbevaringsbetingelser (
	OpbevaringsbetingelserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Kode CHAR(1) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (OpbevaringsbetingelserPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IndikationATCRef (
	IndikationATCRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	CID VARCHAR(22) NOT NULL,

	IndikationKode BIGINT(15) NOT NULL,
	ATC VARCHAR(10) NOT NULL,
	DrugID BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (IndikationATCRefPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE SpecialeForNBS (
	SpecialeForNBSPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Kode VARCHAR(5) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (SpecialeForNBSPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Styrkeenhed (
	StyrkeenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	StyrkeenhedKode VARCHAR(10) NOT NULL,
	StyrkeenhedTekst VARCHAR(50) NOT NULL,
	StyrkeenhedKortTekst VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (StyrkeenhedPID, ModifiedDate),
	INDEX (StyrkeenhedKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Klausulering (
	KlausuleringPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Kode VARCHAR(10) NOT NULL,
	KortTekst VARCHAR(60),
	Tekst VARCHAR(600),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (KlausuleringPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Medicintilskud (
	MedicintilskudPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Kode CHAR(2) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (MedicintilskudPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Indikation (
	IndikationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	IndikationKode BIGINT(15) NOT NULL,

	IndikationTekst VARCHAR(100),
	IndikationstekstLinie1 VARCHAR(26),
	IndikationstekstLinie2 VARCHAR(26),
	IndikationstekstLinie3 VARCHAR(26),
	aktiv BOOLEAN,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (IndikationPID, ModifiedDate),
	INDEX (IndikationKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Tilskudsintervaller (
	TilskudsintervallerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	CID CHAR(4) NOT NULL,
	Type INT(2) NOT NULL,
	Niveau INT(1) NOT NULL,
	NedreGraense BIGINT(12),
	OevreGraense BIGINT(12),
	Procent DECIMAL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (TilskudsintervallerPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Administrationsvej (
	AdministrationsvejPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	AdministrationsvejKode CHAR(2) NOT NULL,
	AdministrationsvejTekst VARCHAR(50) NOT NULL,
	AdministrationsvejKortTekst VARCHAR(10),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,

	INDEX (AdministrationsvejPID, ModifiedDate),
	INDEX (AdministrationsvejKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Tidsenhed (
	TidsenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	TidsenhedKode VARCHAR(10) NOT NULL,
	TidsenhedTekst VARCHAR(50) NOT NULL,
	TidsenhedKortTekst VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (TidsenhedPID, ModifiedDate),
	INDEX (TidsenhedKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE EmballagetypeKoder (
	EmballagetypeKoderPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Kode VARCHAR(4) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (EmballagetypeKoderPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Dosering (
	DoseringPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	DoseringKode BIGINT(12) NOT NULL,

	DoseringTekst VARCHAR(100) NOT NULL,
	AntalEnhederPrDoegn FLOAT(10) NOT NULL,
	Aktiv BOOLEAN,
	DoseringKortTekst VARCHAR(10),
	DoseringstekstLinie1 VARCHAR(26),
	DoseringstekstLinie2 VARCHAR(26),
	DoseringstekstLinie3 VARCHAR(26),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (DoseringPID, ModifiedDate),
	INDEX (DoseringKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Firma (
	FirmaPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Firmanummer BIGINT(12) NOT NULL,
	FirmamaerkeKort VARCHAR(20),
	FirmamaerkeLangtNavn VARCHAR(32),
	ParallelimportoerKode VARCHAR(2),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (FirmaPID, ModifiedDate),
	INDEX (Firmanummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE ATC (
	ATCPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	ATC CHAR(8) NOT NULL,
	ATCTekst VARCHAR(72) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (ATCPID, ModifiedDate),
	INDEX (ATC, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE LaegemiddelAdministrationsvejRef (
	LaegemiddelAdministrationsvejRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	CID VARCHAR(22) NOT NULL,

	DrugID BIGINT(12) NOT NULL,
	AdministrationsvejKode CHAR(2) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (LaegemiddelAdministrationsvejRefPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE LaegemiddelDoseringRef (
	LaegemiddelDoseringRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	CID VARCHAR(22) NOT NULL,

	DrugID BIGINT(12) NOT NULL,
	DoseringKode BIGINT(12) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (LaegemiddelDoseringRefPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE UdgaaedeNavne (
	UdgaaedeNavnePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	CID VARCHAR(71) NOT NULL,
	Drugid BIGINT(12),
	DatoForAendringen DATE,
	TidligereNavn VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (UdgaaedeNavnePID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Pakning (
	PakningPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Varenummer BIGINT(12) NOT NULL,

	VarenummerDelpakning BIGINT(12),
	DrugID DECIMAL(12) NOT NULL,
	PakningsstoerrelseNumerisk DECIMAL(10,2),
	Pakningsstoerrelsesenhed VARCHAR(10),
	PakningsstoerrelseTekst VARCHAR(30),
	EmballageTypeKode VARCHAR(10),
	Dosisdispenserbar BOOLEAN,
	MedicintilskudsKode VARCHAR(10),
	KlausuleringsKode VARCHAR(10),
	AlfabetSekvensnr BIGINT(12),
	AntalDelpakninger BIGINT(12),
	Udleveringsbestemmelse VARCHAR(5),
	UdleveringSpeciale VARCHAR(5),
	AntalDDDPrPakning DECIMAL,
	OpbevaringstidNumerisk BIGINT(12),
	Opbevaringstid BIGINT(12),
	Opbevaringsbetingelser VARCHAR(1),
	Oprettelsesdato DATE,
	DatoForSenestePrisaendring DATE,
	UdgaaetDato DATE,
	BeregningskodeAIRegpris CHAR(1),
	PakningOptagetITilskudsgruppe BOOLEAN,
	Faerdigfremstillingsgebyr BOOLEAN,
	Pakningsdistributoer BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (PakningPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Laegemiddel (
	LaegemiddelPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	DrugID BIGINT(12) NOT NULL,

	DrugName VARCHAR(30), /* Some drugs are not named. */
	FormKode VARCHAR(10),
	FormTekst VARCHAR(150),
	ATCKode VARCHAR(10),
	ATCTekst VARCHAR(100),
	StyrkeNumerisk DECIMAL(10,3),
	StyrkeEnhed VARCHAR(100),
	StyrkeTekst VARCHAR(30),
	Dosisdispenserbar BOOLEAN,
	Varetype VARCHAR(2),
	Varedeltype VARCHAR(2),
	AlfabetSekvensplads VARCHAR(9),
	SpecNummer BIGINT(12),
	LaegemiddelformTekst VARCHAR(20),
	KodeForYderligereFormOplysn VARCHAR(7),
	Trafikadvarsel BOOLEAN,
	Substitution VARCHAR(1),
	LaegemidletsSubstitutionsgruppe VARCHAR(4),
	DatoForAfregistrAfLaegemiddel DATE,
	Karantaenedato DATE,
	AdministrationsvejKode VARCHAR(8),
	MTIndehaverKode BIGINT(12),
	RepraesentantDistributoerKode BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (LaegemiddelPID, ModifiedDate),
	INDEX (DrugID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
CREATE TABLE Formbetegnelse (
	FormbetegnelsePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Kode VARCHAR(10) NOT NULL,

	Tekst VARCHAR(150) NOT NULL,
	Aktiv BOOLEAN,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (FormbetegnelsePID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Rekommandationer (
	RekommandationerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Varenummer BIGINT(12) NOT NULL,

	Rekommandationsgruppe BIGINT(12),
	DrugID BIGINT(12),
	Rekommandationsniveau VARCHAR(25),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (RekommandationerPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Enhedspriser (
	EnhedspriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Varenummer BIGINT(12) NOT NULL,

	DrugID BIGINT(12),
	PrisPrEnhed BIGINT(12),
	PrisPrDDD BIGINT(12),
	BilligstePakning VARCHAR(1),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (EnhedspriserPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Pakningskombinationer (
	PakningskombinationerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	CID VARCHAR(23) NOT NULL,

	VarenummerOrdineret BIGINT(12),
	VarenummerSubstitueret BIGINT(12),
	VarenummerAlternativt BIGINT(12),
	AntalPakninger BIGINT(12),
	EkspeditionensSamledePris BIGINT(12),
	InformationspligtMarkering VARCHAR(1),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (PakningskombinationerPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Laegemiddelnavn (
	LaegemiddelnavnPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	DrugID BIGINT(12) NOT NULL,
	LaegemidletsUforkortedeNavn VARCHAR(60),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (LaegemiddelnavnPID, ModifiedDate),
	INDEX (DrugID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Indholdsstoffer (
	IndholdsstofferPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	CID VARCHAR(364) NOT NULL,

	DrugID BIGINT(12),
	Varenummer BIGINT(12),
	Stofklasse VARCHAR(100),
	Substansgruppe VARCHAR(100),
	Substans VARCHAR(150),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (IndholdsstofferPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

