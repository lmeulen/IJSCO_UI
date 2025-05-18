# IJSCO_UI (Beta)
Applicatie voor het snel en flexibel indelen bij (schaak-)toernooien.

Deze (java) applicatie kan ratinglijsten en deelnemerslijsten inlezen. Op basis van configuratieinstellingen worden
wedstrijdschema's (indelingen) voorgesteld. Hiertoe wordt een (volledig beveiligde) Excel aangemaakt; alleen wedstrijd-
uitslagen kunnen worden ingevoerd. Hierin kunnen voorlopige deelnemerslijsten mee afgedrukt worden (op indeling, op
naam en op rating). Last-minute af- en aanmeldingen kunnen snel verwerkt worden. Er kan snel een nieuwe indeling
worden gemaakt om snel met het toernooi te kunnen starten. In de Excel is na invoering van de laatste uitslagen direct
de stand duidelijk zichtbaar (per groep en in een overzicht van alle groepen). Na afronding van het toernooi kan de Excel
ingevoerd worden in de applicatie voor de verwerking. Hierbij kunnen diverse exportformaten gekozen worden (o.a. FIDE2006
voor verwerking door de bond). Zie de mogelijkheden voor alle mogelijkheden.

Mogelijkheden:

    Configureerbaar voor allerlei soorten Round-Robin groepsindelingen
    Online of Offline laatste ratinglijst ophalen
    Importeren deelnemerslijst (KNSBnummer, optioneel: naam en rating)
    Handmatig toevoegen van deelnemers (met behulp van een (deel van de) naam of KNSBrating)
    Handmatig aanpassen van naam of rating
    Software komt op basis van het aantal deelnemers en configuratie tot de mogelijke groepsindelingen.
    Selecteren gewenste of meest ideale groepsindeling en aanmaken van een proef/tijdelijke toernooiformulier (Excel)
    Uitdraaien van (proef/tijdelijke) deelnemerslijsten (op naam, op groep en op rating)
    Uitdraaien van de (proef/tijdelijke) rondeindeling per groep
    Eenvoudig invoeren van uitslagen per groep. Het is onmogelijk de Excel per ongeluk kapot te maken.
    Eenvoudig tussenstanden uitdraaien per groep
    Eenvoudig einduitslag (direct na invoeren laatste uitslag beschikbaar) uit draaien per groep
    Eenvoudig totaaloverzicht uitdraaien met de uitslagen van alle groepen.
    Imporeren van de Excel na afloop toernooi voor verwerking
    Automatisch aanmaken van diverse eindetoernooi rapportages en FIDE2006 Data-Exchange bestand
    RESTful API koppeling met schaakrating.nl
    Exportformaten:
        Fide2006
        Json
        CSV
        XLS
    Importformaten
        CSV
        Excel
        JSON
        Online

Voor informatie: Neem contact op met Lars (github/oxygenius) of kom bij het Open Source team van www.schaakrating.nl. Mail hiervoor naar l.p.dam@schaakrating.nl
