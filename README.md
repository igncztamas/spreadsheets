1.	Feladat
Mini táblázatkezelő program. Adatok bevitele, módosítása, rajtuk értelmezett függvények végrehajtása, a cellaértékek megjelenítése, illetve táblázatok mentése, betöltése. Grafikus felületen keresztül való kezelés.

2.	Felhasználói dokumentáció
•	Cellába írás: dupla klikk/kiválasztás és írás, véglegesítés: enter.
•	Cellákat kijelölve: másolás: ctrl+c (egy a programon belüli vágólapra); beillesztés: ctrl+v (mindig bal felső cellához illeszt); törlés: delete. Sorokra/oszlopokra az összes oszlop/sor kijelölhető az adott sorban/oszlopban, shift nyomvatartásával egy kiválasztás bővíthető.
•	Az oszlop/sor fejlécben két sor/oszlop határán egérgombot lenyomva tartva változtatható a szélesség/magasság.
•	Formulák: „{ }” között, a formulán belül: cella referenciák: „:x:y:”, ahol x az oszlop, y a sorszám; függvények: „SUM(x1,y1,x2, y2)” ahol (x1, y1) a bal felső, (x2, y2) a jobb alsó sarok, rosszul formázott/rekurzív formulát a program nem fog kiértékelni. Értelmezett függvények: SUM(), AVG(), COUNT(), MIN(), MAX().
•	File kezelés: bal felső sarokban lévő gomb, a megjelenő menüben bevihető a file elérési útvonal majd a megfelelő gomb véglegesíti, a read funkciónál az első mező az értékeket elválasztó karakter. A file formátumát a program nem teszteli.

3.	Egyéb
•	Felhasznált könyvtár: https://johanley.github.io/formula4j/index.html
