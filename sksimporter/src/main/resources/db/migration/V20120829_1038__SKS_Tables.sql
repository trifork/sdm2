CREATE TABLE Organisation (
	OrganisationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,

	Nummer VARCHAR(20) NOT NULL,
	Navn VARCHAR(60),
	Organisationstype VARCHAR(30) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,

	INDEX (OrganisationPID, ModifiedDate),
	INDEX (Nummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;
