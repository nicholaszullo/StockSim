# Stock Market Simulation
## Instructions
To begin the simulation, use StockSim.java. Ensure that the classpath contains sqlite as noted in the classpath folder. To end the simulation, kill the process with ctrl+c. You can also start with a & to run in the background and then kill the JVM process from the command line, just comment out the logging.
You'll need an account with developer.tdameritrade and to create an app. You'll get an ApiKey from the app, which should be stored in ApiKey.key. Next you'll need to get a refresh token from Ameritrade. Place the refresh token in RefreshToken.key. If you for some reason want to link it to a real account, place the account number in AccountNumber.key (you'll also need to add the proper methods to APIHandler, it only supports retrieving account data, not making trades).

## Objective
This environment is a harness to plug algorithms that buy stocks into the Buyer class and algorithms that sell stocks into the Seller class. These algorithms access the account balance and positions through synchonized shared memory in ThreadDriver.

## Key Features

### JSON Parsing
The JSON parser is generic and can handle lots of JSON strings. Developing this required learning about proper parsing methodologies and was a big part of the project dispite playing a small role in the end result.

### API Integration
In order to receive data on the markets, we needed to use an API that provides it. Integrating API responses with the JSON parser allows for clean processing of the API response. The API used is well suited for this project. There is a 120 call per minute throttle, but that is more than enough for the small simulator.

### Database Integration
The API calls are processed into a database for the algorithms to access. This is done easily thanks to the DatabaseHandler. Simply provide the DatabaseHandler with an SQL request and it will process it. This makes for clean inserting and querying by the API and algorithms.

### Multithreading
Much of the simulation utilizes multithreading. Each stock being tracked in the database receives its own thread dedicated to updating that stock's price. Each buying and selling algorithm runs its own thread for each stock it is tracking. These multithreading capabilities allow for expansion at scale, and will allow for much more data to be processed at a time. 

## End Goal
The end goal of this project is to run the simulator in the background during the day and end the day with a profit. Once algorithms are found that can do this consistently, the simulator can be linked into real money in the market simply with the API. This will be a constantly moving goal, as algorithms are tried and tested over time.
