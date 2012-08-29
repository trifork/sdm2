CREATE TABLE Sikrede (
    PID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    CPRnr VARCHAR(10),
    SYdernr VARCHAR(6),
    SIkraftDatoYder VARCHAR(8),
    SRegDatoYder VARCHAR(8),
    SSikrGrpKode VARCHAR(1),
    SIkraftDatoGrp VARCHAR(8),
    SRegDatoGrp VARCHAR(8),
    SSikrKomKode VARCHAR(3),
    SIkraftDatoKomKode VARCHAR(8),
    SYdernrGl VARCHAR(6),
    SIkraftDatoYderGl VARCHAR(8),
    SRegDatoYderGl VARCHAR(8),
    SSikrGrpKodeGl VARCHAR(1),
    SIkraftDatoGrpGl VARCHAR(8),
    SRegDatoGrpGl VARCHAR(8),
    SYdernrFrem VARCHAR(6),
    SIkraftDatoYderFrem VARCHAR(8),
    SRegDatoYderFrem VARCHAR(8),
    SSikrGrpKodeFrem VARCHAR(1),
    SIkraftDatoGrpFrem VARCHAR(8),
    SRegDatoGrpFrem VARCHAR(8),
    SKon VARCHAR(1),
    SAlder VARCHAR(3),
    SFolgerskabsPerson VARCHAR(10),
    SStatus VARCHAR(2),
    SBevisDato VARCHAR(8),
    PNavn VARCHAR(34),
    SBSStatsborgerskabKode VARCHAR(2),
    SBSStatsborgerskab VARCHAR(47),
    SSKAdrLinie1 VARCHAR(40),
    SSKAdrLinie2 VARCHAR(40),
    SSKBopelsLand VARCHAR(40),
    SSKBopelsLAndKode VARCHAR(2),
    SSKEmailAdr VARCHAR(50),
    SSKFamilieRelation VARCHAR(10),
    SSKFodselsdato VARCHAR(10),
    SSKGyldigFra VARCHAR(10),
    SSKGyldigTil VARCHAR(10),
    SSKMobilNr VARCHAR(20),
    SSKPostNrBy VARCHAR(40),
    SSLForsikringsinstans VARCHAR(21),
    SSLForsikringsinstansKode VARCHAR(10),
    SSLForsikringsnr VARCHAR(15),
    SSLGyldigFra VARCHAR(10),
    SSLGyldigTil VARCHAR(10),
    SSLSocSikretLand VARCHAR(47),
    SSLSocSikretLandKode VARCHAR(2),
    ValidFrom DateTime NOT NULL,
    ValidTo DateTime,
    ModifiedDate DateTime NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;


-- This table is to be replicated to the BRS db schema.
-- It is populated by stamdata, but not read by stamdata.
--
CREATE TABLE AssignedDoctor ( -- Sikrede
  pk bigint AUTO_INCREMENT NOT NULL,

    -- cpr numre er base64 af hashede numre
  patientCpr varchar(80) NOT NULL,

  doctorOrganisationIdentifier varchar(6) NOT NULL, -- ydernummer

  assignedFrom datetime NOT NULL,
  assignedTo datetime,

  reference varchar(40) NOT NULL,

  PRIMARY KEY (pk),
  KEY `patientCpr` (`patientCpr`)
) ENGINE=InnoDB COLLATE=utf8_bin;
