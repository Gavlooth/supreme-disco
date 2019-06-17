# mobytronics-car-pooling

description

Iceland car pooling information fetching

## Installation
This works from the repl. However modifications to create an 
Uberjar should be trivial

## Usage
from the user namespace within the repl hit (go)
This by default initiates an http server on port 3334 
If you want to change the jetty port you should modify 
/resources/config.edn or add an entry in /config/config.edn



## Examples
There are two endpoint on this service
/services/drivers and /services/passengers
Hiting the endpoints would fetct the first nx8 passenger, or driver
details with all the extra info like distance of origin and destination,
passenger notes  e.t.c. e.t.c.
Example query    /services/drivers?page=1  //first 8 items
**
...

### Bugs
Dont know
...

### Todo
Other aproaches like streaming all the pages/or using regexes to exctract data 
instead of hickory

## License

Copyright © 2019 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
