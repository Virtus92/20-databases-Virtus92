START TRANSACTION;

--
-- Datenbank: `ClientManagement`
--
CREATE DATABASE IF NOT EXISTS `ClientManagement` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ClientManagement`;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `CLIENTS`
--

CREATE TABLE `CLIENTS` (
  `ID` int(11) NOT NULL,
  `FIRSTNAME` varchar(50) NOT NULL,
  `LASTNAME` varchar(50) NOT NULL,
  `ACTIVE` tinyint(4) NOT NULL,
  `CREDITLIMIT` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indizes für die Tabelle `CLIENTS`
--
ALTER TABLE `CLIENTS`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT für Tabelle `CLIENTS`
--
ALTER TABLE `CLIENTS`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;
