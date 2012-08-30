CREATE TABLE YderregisterKeyValue (
    
    `Key` VARCHAR(100) NOT NULL,
    Value VARCHAR(100) NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Yderregister - yder
--
CREATE TABLE Yderregister (
    PID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    HistIdYder VARCHAR(16),
    AmtKodeYder VARCHAR(2),
    AmtTxtYder VARCHAR(60),
    YdernrYder VARCHAR(6),
    PrakBetegn VARCHAR(50),
    AdrYder VARCHAR(50),
    PostnrYder VARCHAR(4),
    PostdistYder VARCHAR(20),
    TilgDatoYder VARCHAR(8),
    AfgDatoYder VARCHAR(8),
    HvdSpecKode VARCHAR(2),
    HvdSpecTxt VARCHAR(60),
    HvdTlf VARCHAR(8),
    EmailYder VARCHAR(50),
    WWW VARCHAR(78),
    ValidFrom DateTime NOT NULL,
    ValidTo DateTime,
    ModifiedDate DateTime NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Yderregister - person
--
CREATE TABLE YderregisterPerson (
    PID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    HistIdPerson VARCHAR(16),
    YdernrPerson VARCHAR(6),
    TilgDatoPerson VARCHAR(8),
    AfgDatoPerson VARCHAR(8),
    CprNr VARCHAR(10),
    PersonrolleKode VARCHAR(2),
    PersonrolleTxt VARCHAR(60),
    ValidFrom DateTime NOT NULL,
    ValidTo DateTime,
    ModifiedDate DateTime NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;
