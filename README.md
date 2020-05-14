**DB Setup**
- DB engine settings could be changed in `/resources/application.conf` file
- Run `SchemaImporter` to import DB schema
- Currently, all data files are download manually. This could be changed later if we choose to use cron jobs for period updates.
- Run `DataImporter` to import data (change limits, if needed). Wait for `import - Done!` message to appear on the terminal window) 

**Web Server**  
Run `WebServer` to launch HTTP server on default `8080` port.  
Currently, If multiple tabs are open, it might give "Internal Server Error". Since it uses SQLLite DB and sometimes it gives "database file is locked".   
Check web server logs in the terminal window (in case of any error)


**Routes**   
Routes are shown in `RouteProvider` e.g.

- http://localhost:8080/genres/action
- http://localhost:8080/genres/horror

- http://localhost:8080/titles/Girl
- http://localhost:8080/titlesLike/Girl (`titlesLike` will show multiple titles)

- http://localhost:8080/kevinNumber/Bruce%20Lee

- Screenshots are in `/src/main/resources`
- Unit tests added in `KevinNumberCalculatorSpec`


**KevinNumberCalculator**
- Calculated value is stored in `kevin_numbers` to avoid repetition
